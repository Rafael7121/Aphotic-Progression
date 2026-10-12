package apothicprogression.client;

import apothicprogression.*;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Player;

public final class XpTooltip {
    private XpTooltip() {}
    private static Locale locale() {
        return Locale.forLanguageTag(Minecraft.getInstance().getLanguageManager().getSelected().replace('_','-'));
    }
    private static String number(long n) { return NumberFormat.getIntegerInstance(locale()).format(n); }
    private static Component bar(long points) {
        var bar=XpBar.fromPoints(points);
        if (bar.tenthsPercent()==0) return Component.translatable("apothic_progression.bar_whole",number(bar.level()));
        var format=NumberFormat.getNumberInstance(locale());
        format.setMinimumFractionDigits(1);
        format.setMaximumFractionDigits(1);
        return Component.translatable("apothic_progression.bar_partial",number(bar.level()),format.format(bar.tenthsPercent()/10.0));
    }
    public static MutableComponent describe(long cost, Player player) {
        boolean affordable=player != null && Progression.affordable(player,cost);
        var text=Component.empty();
        text.append(Component.translatable("apothic_progression.xp_cost",number(cost))
            .withStyle(affordable ? ChatFormatting.GREEN : ChatFormatting.RED));
        text.append("\n").append(Component.translatable("apothic_progression.from_zero",bar(cost)).withStyle(ChatFormatting.GRAY));
        if (player == null) return text;
        long balance=Progression.balance(player);
        if (player.getAbilities().instabuild) {
            text.append("\n").append(Component.translatable("apothic_progression.creative").withStyle(ChatFormatting.AQUA));
        } else if (balance >= cost) {
            text.append("\n").append(Component.translatable("apothic_progression.after_payment",bar(balance),bar(balance-cost)).withStyle(ChatFormatting.YELLOW));
        } else {
            text.append("\n").append(Component.translatable("apothic_progression.missing",number(cost-balance)).withStyle(ChatFormatting.RED));
        }
        return text;
    }
}
