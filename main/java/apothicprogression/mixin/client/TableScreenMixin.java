package apothicprogression.mixin.client;
import apothicprogression.*;
import dev.shadowsoffire.apothic_enchanting.table.ApothEnchantmentMenu;
import dev.shadowsoffire.apothic_enchanting.util.TooltipUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(targets="dev.shadowsoffire.apothic_enchanting.table.ApothEnchantmentScreen", remap=false)
public class TableScreenMixin {
    @ModifyArg(method="render",at=@At(value="INVOKE",target="Ldev/shadowsoffire/apothic_enchanting/table/ApothEnchantmentScreen;drawOnLeft(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;I)V"),index=1)
    private java.util.List<net.minecraft.network.chat.Component> ap$rows(java.util.List<net.minecraft.network.chat.Component> lines) {
        return TooltipRows.components(lines);
    }
    @Shadow @Final protected ApothEnchantmentMenu menu;
    @Unique private long ap$hoverPrice;
    @Redirect(method="render", at=@At(value="INVOKE",target="Ldev/shadowsoffire/apothic_enchanting/util/MiscUtil;getExpCostForSlot(II)I"))
    private int ap$cost(int power, int slot) {
        ap$hoverPrice=((TablePrices)menu).ap$price(slot);
        return XpMath.displayInt(ap$hoverPrice);
    }
    @Redirect(method="render", at=@At(value="INVOKE",target="Ldev/shadowsoffire/placebo/util/EnchantmentUtils;getLevelForExperience(I)I"))
    private int ap$constantTimeLevel(int points) { return XpMath.levelFor(points); }
    @Redirect(method="render", at=@At(value="INVOKE",target="Ldev/shadowsoffire/apothic_enchanting/util/TooltipUtil;lang(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent ap$rawXpLabel(String type, String key, Object[] args) {
        if (!type.equals("info") || !key.equals("xp_cost")) return TooltipUtil.lang(type,key,args);
        var p=Minecraft.getInstance().player;
        return apothicprogression.client.XpTooltip.describe(ap$hoverPrice,p);
    }
}
