package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player disconnect.
 */
public class PlayerDisconnectEvent implements PlayerEvent {

    private final Player player;

    public PlayerDisconnectEvent(@NotNull Player player) {
        this.player = player;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }
}
