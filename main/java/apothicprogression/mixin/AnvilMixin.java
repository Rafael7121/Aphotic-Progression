package apothicprogression.mixin;
import apothicprogression.*;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value=AnvilMenu.class, priority=900, remap=false)
public class AnvilMixin {
    @Unique private long ap$cost() {
        var menu = (AnvilMenu)(Object)this;
        return Progression.cost(menu.getSlot(0).getItem(), menu.getSlot(2).getItem());
    }
    @Inject(method="mayPickup", at=@At("HEAD"), cancellable=true)
    private void ap$mayPickup(Player p, boolean hasStack, CallbackInfoReturnable<Boolean> cir) {
        long cost = ap$cost();
        if (cost > 0) cir.setReturnValue(hasStack && Progression.affordable(p, cost));
    }
    @WrapMethod(method="onTake")
    private void ap$onTake(Player p, ItemStack result, Operation<Void> original) {
        long cost = Progression.cost(((AnvilMenu)(Object)this).getSlot(0).getItem(), result);
        if (cost == 0) { original.call(p, result); return; }
        if (!Progression.charge(p, cost)) return;
        try (var paid = ChargeScope.paid(p)) { original.call(p, result); }
    }
}
