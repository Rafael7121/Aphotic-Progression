package apothicprogression.mixin;
import java.util.List;
import apothicprogression.*;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.shadowsoffire.apothic_enchanting.table.ApothEnchantmentMenu;
import dev.shadowsoffire.apothic_enchanting.util.MiscUtil;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value=ApothEnchantmentMenu.class, remap=false)
public abstract class TableMixin extends AbstractContainerMenu implements TablePrices {
    protected TableMixin(MenuType<?> t, int id) { super(t,id); }
    @Shadow private List<EnchantmentInstance> getEnchantmentList(ItemStack stack, int slot, int power) { throw new AssertionError(); }
    @Unique private long[] ap$prices = new long[3];
    @Override public long ap$price(int slot) { return ap$prices[slot]; }
    @Inject(method="<init>", at=@At("TAIL"))
    private void ap$sync(CallbackInfo ci) {
        for (int slot=0; slot<3; slot++) for (int word=0; word<4; word++) {
            final int s=slot, shift=word*16;
            addDataSlot(new DataSlot() {
                public int get() { return (int)(ap$prices[s] >>> shift) & 65535; }
                public void set(int value) { ap$prices[s] = (ap$prices[s] & ~(65535L << shift)) | ((value & 65535L) << shift); }
            });
        }
    }
    @Unique private boolean ap$isInfusion(List<EnchantmentInstance> list) {
        return !list.isEmpty() && list.getFirst().enchantment.unwrapKey()
            .map(k -> k.location().toString().equals("apothic_enchanting:infusion")).orElse(false);
    }
    @Inject(method="getEnchantmentList", at=@At("RETURN"))
    private void ap$quote(ItemStack stack, int slot, int power, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (ap$prices == null) ap$prices = new long[3];
        var list = cir.getReturnValue();
        ap$prices[slot] = ap$isInfusion(list) ? MiscUtil.getExpCostForSlot(power,slot) : Progression.cost(stack,list);
    }
    @Inject(method="clickMenuButton", at=@At("HEAD"), cancellable=true)
    private void ap$check(Player p, int slot, CallbackInfoReturnable<Boolean> cir) {
        if (slot < 0 || slot >= 3 || !Progression.affordable(p,ap$price(slot))) cir.setReturnValue(false);
    }
    @WrapMethod(method="lambda$clickMenuButton$0")
    private void ap$enchant(Player p, ItemStack input, int slot, int power, ItemStack lapis, int count, Level level, BlockPos pos, Operation<Void> original) {
        var list = getEnchantmentList(input,slot,((ApothEnchantmentMenu)(Object)this).costs[slot]);
        if (list.isEmpty()) return;
        if (!Progression.charge(p,ap$prices[slot])) return;
        try (var paid = ChargeScope.paid(p)) { original.call(p,input,slot,power,lapis,count,level,pos); }
    }
}
