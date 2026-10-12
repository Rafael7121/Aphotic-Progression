package apothicprogression;

import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.network.chat.Component;

public final class Progression {
    private Progression() {}
    public static long cost(ItemEnchantments before, ItemEnchantments after) {
        long cost = 0;
        for (var entry : after.entrySet())
            cost += XpMath.upgrade(before.getLevel(entry.getKey()), entry.getIntValue());
        return cost;
    }
    public static long cost(ItemStack before, ItemStack after) {
        return cost(EnchantmentHelper.getEnchantmentsForCrafting(before), EnchantmentHelper.getEnchantmentsForCrafting(after));
    }
    public static long cost(ItemStack before, List<EnchantmentInstance> selected) {
        var old = EnchantmentHelper.getEnchantmentsForCrafting(before);
        var next = new ItemEnchantments.Mutable(old);
        for (var e : selected) next.upgrade(e.enchantment, e.level);
        return cost(old, next.toImmutable());
    }
    public static long balance(Player player) {
        long base = XpMath.playerBase(player.experienceLevel);
        long partial = Math.round((double)player.experienceProgress * player.getXpNeededForNextLevel());
        return base > Long.MAX_VALUE-partial ? Long.MAX_VALUE : base+partial;
    }
    public static boolean affordable(Player player, long cost) {
        return cost >= 0 && (player.getAbilities().instabuild || balance(player) >= cost);
    }
    public static boolean charge(Player player, long cost) {
        if (!affordable(player, cost)) return false;
        if (player.getAbilities().instabuild || cost == 0) return true;
        long left = balance(player)-cost;
        player.experienceLevel = XpMath.levelFor(left);
        player.experienceProgress = (float)((double)(left-XpMath.playerBase(player.experienceLevel))/player.getXpNeededForNextLevel());
        player.totalExperience = XpMath.displayInt(left);
        return true;
    }
    public static String number(long points) { return NumberFormat.getIntegerInstance(Locale.US).format(points); }
    public static Component label(long points) {
        return Component.translatable("apothic_progression.xp_cost", number(points));
    }
}
