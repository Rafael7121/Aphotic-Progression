package apothicprogression;

public final class XpMath {
    private XpMath() {}
    public static long cumulative(int level) {
        if (level < 0 || level > 255) throw new IllegalArgumentException("Enchantment level outside 0..255");
        return 10L * level * level * level;
    }
    public static long upgrade(int before, int after) {
        return Math.max(0L, cumulative(after) - cumulative(before));
    }
    public static long playerBase(int level) {
        long l = Math.max(0, level);
        if (l <= 16) return l*l + 6*l;
        if (l <= 31) return (5*l*l - 81*l + 720)/2;
        // Avoid overflow for malformed/extreme player levels.
        if (l > 1_000_000_000L) return Long.MAX_VALUE;
        return (9*l*l - 325*l + 4440)/2;
    }
    public static int levelFor(long points) {
        int low = 0, high = 1_000_000_000;
        while (low < high) {
            int mid = low + (high-low+1)/2;
            if (playerBase(mid) <= points) low = mid; else high = mid-1;
        }
        return low;
    }
    public static int displayInt(long points) { return (int)Math.min(Integer.MAX_VALUE, points); }
}
