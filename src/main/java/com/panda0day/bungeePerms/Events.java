package com.panda0day.bungeePerms;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String prefix = BungeePerms.getInstance().getCachedPrefixes().getOrDefault(player.getUniqueId().toString(), "");
        String suffix = BungeePerms.getInstance().getCachedSuffixes().getOrDefault(player.getUniqueId().toString(), "");

        String formattedMessage = prefix + player.getName() +  " " + suffix + ": " + event.getMessage();
        event.setFormat(formattedMessage);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        String prefix = BungeePerms.getInstance().getCachedPrefixes().getOrDefault(player.getUniqueId().toString(), "");
        String suffix = BungeePerms.getInstance().getCachedSuffixes().getOrDefault(player.getUniqueId().toString(), "");

        player.setPlayerListName(prefix + player.getName() + suffix);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        BungeePerms.getInstance().getCachedPrefixes().remove(player.getName());
        BungeePerms.getInstance().getCachedSuffixes().remove(player.getName());
    }
}
