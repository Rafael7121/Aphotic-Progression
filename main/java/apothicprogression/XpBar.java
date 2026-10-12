package apothicprogression;

/** A position on the player's XP bar, not an enchantment level or a debit in levels. */
public record XpBar(int level, int tenthsPercent) {
    public static XpBar fromPoints(long points) {
        if (points < 0) throw new IllegalArgumentException("Negative XP balance");
        int level = XpMath.levelFor(points);
        long partial = points-XpMath.playerBase(level);
        long needed = XpMath.playerBase(level+1)-XpMath.playerBase(level);
        int fraction = needed > 0 ? (int)Math.min(999, Math.round((double)partial/needed*1000)) : 0;
        return new XpBar(level, fraction);
    }
}
