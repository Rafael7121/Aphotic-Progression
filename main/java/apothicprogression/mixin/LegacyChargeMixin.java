package apothicprogression.mixin;
import apothicprogression.ChargeScope;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets="dev.shadowsoffire.placebo.util.EnchantmentUtils", remap=false)
public class LegacyChargeMixin {
    @Inject(method="chargeExperience", at=@At("HEAD"), cancellable=true)
    private static void ap$skipPaid(Player p, int cost, CallbackInfoReturnable<Boolean> cir) {
        if (ChargeScope.isPaid(p)) cir.setReturnValue(true);
    }
}
