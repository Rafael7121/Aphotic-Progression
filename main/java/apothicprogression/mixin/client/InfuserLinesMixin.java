package apothicprogression.mixin.client;
import java.util.List;
import apothicprogression.TooltipRows;
import net.minecraft.network.chat.FormattedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(targets="fuzs.enchantinginfuser.client.gui.components.InfuserMenuButton",remap=false)
public class InfuserLinesMixin {
    @ModifyArg(method="refreshTooltip",at=@At(value="INVOKE",target="Lfuzs/puzzleslib/api/client/gui/v2/components/tooltip/TooltipBuilder;addLines(Ljava/util/List;)Lfuzs/puzzleslib/api/client/gui/v2/components/tooltip/TooltipBuilder;"),index=0)
    private List<FormattedText> ap$rows(List<FormattedText> lines) { return TooltipRows.expand(lines); }
}
