package apothicprogression.mixin.client;
import apothicprogression.Progression;
import apothicprogression.InfuserPrice;
import net.minecraft.client.Minecraft;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(targets="fuzs.enchantinginfuser.client.gui.components.InfuserEnchantButton", remap=false)
public class InfuserButtonMixin {
    @Inject(method="getStringValue", at=@At("HEAD"), cancellable=true)
    private void ap$short(int cost, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(cost >= 1_000_000_000 ? cost/1_000_000_000+"B" : cost >= 1_000_000 ? cost/1_000_000+"M" : cost >= 1_000 ? cost/1_000+"k" : Integer.toString(cost));
    }
    @Inject(method="getLevelsComponent", at=@At("HEAD"), cancellable=true)
    private void ap$tooltip(int cost, boolean active, CallbackInfoReturnable<Component> cir) {
        var player=Minecraft.getInstance().player;
        long exact=player != null && player.containerMenu instanceof InfuserPrice price ? price.ap$infuserPrice() : cost;
        cir.setReturnValue(apothicprogression.client.XpTooltip.describe(exact,player));
    }
}
