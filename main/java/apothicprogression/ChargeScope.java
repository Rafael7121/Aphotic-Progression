package apothicprogression;

import net.minecraft.world.entity.player.Player;

/** Only suppress the legacy debit inside a transaction already paid in raw XP. */
public final class ChargeScope implements AutoCloseable {
    private static final ThreadLocal<Player> PAID = new ThreadLocal<>();
    private final Player previous;
    private ChargeScope(Player player) { previous = PAID.get(); PAID.set(player); }
    public static ChargeScope paid(Player player) { return new ChargeScope(player); }
    public static boolean isPaid(Player player) { return PAID.get() == player; }
    @Override public void close() { if (previous == null) PAID.remove(); else PAID.set(previous); }
}
