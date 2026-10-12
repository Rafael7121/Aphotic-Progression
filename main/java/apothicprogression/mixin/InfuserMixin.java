package apothicprogression.mixin;

import apothicprogression.*;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(targets="fuzs.enchantinginfuser.world.inventory.InfuserMenu", remap=false)
public abstract class InfuserMixin implements InfuserPrice {
    @Shadow private boolean markedDirty;
    @Shadow public abstract ItemStack getEnchantableStack();
    @Shadow public abstract ItemEnchantments getItemEnchantments();
    @Unique private Long ap$transactionCost;
    @Override public long ap$infuserPrice() { return ap$transactionCost == null ? ap$quote() : ap$transactionCost; }
    @Unique private long ap$quote() {
        return Progression.cost(EnchantmentHelper.getEnchantmentsForCrafting(getEnchantableStack()), getItemEnchantments());
    }
    // The stock DataSlot only transmits 16 bits. Both sides have the full selection,
    // so derive the displayed cost from it instead of using the truncated DataSlot.
    @Inject(method="getEnchantingCost", at=@At("HEAD"), cancellable=true)
    private void ap$getCost(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(XpMath.displayInt(ap$transactionCost == null ? ap$quote() : ap$transactionCost));
    }
    @Inject(method="calculateEnchantingCost", at=@At("HEAD"), cancellable=true)
    private void ap$calculate(CallbackInfoReturnable<Integer> cir) { cir.setReturnValue(XpMath.displayInt(ap$quote())); }
    @Inject(method="canEnchant", at=@At("HEAD"), cancellable=true)
    private void ap$canEnchant(Player p, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!getEnchantableStack().isEmpty() && markedDirty && Progression.affordable(p, ap$quote()));
    }
    @WrapMethod(method="lambda$clickEnchantButton$1")
    private void ap$enchant(Player p, Level level, BlockPos pos, Operation<Void> original) {
        long cost = ap$quote();
        if (!markedDirty || !Progression.charge(p, cost)) return;
        ap$transactionCost = cost;
        try { original.call(p, level, pos); }
        finally { ap$transactionCost = null; }
    }
    @Inject(method="processEnchantingCost", at=@At("HEAD"), cancellable=true)
    private void ap$skipLevels(Player p, Level l, BlockPos pos, int cost, CallbackInfo ci) { ci.cancel(); }
}
