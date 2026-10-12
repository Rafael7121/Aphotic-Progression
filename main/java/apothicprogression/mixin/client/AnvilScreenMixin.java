package apothicprogression.mixin.client;
import apothicprogression.Progression;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(value=AnvilScreen.class, remap=false)
public class AnvilScreenMixin {
    @Redirect(method="renderLabels", at=@At(value="INVOKE", target="Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent ap$label(String key, Object[] args) {
        var menu=((AnvilScreen)(Object)this).getMenu();
        long cost=Progression.cost(menu.getSlot(0).getItem(),menu.getSlot(2).getItem());
        return cost > 0 && key.equals("container.repair.cost") ? Component.literal(Progression.number(cost)+" XP") : Component.translatable(key,args);
    }
}
