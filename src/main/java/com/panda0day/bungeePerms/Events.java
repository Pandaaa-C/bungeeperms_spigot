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
        getLogger().info("onChat: " + player.getName());
        String prefix = prefixes.getOrDefault(player.getName(), "");
        String suffix = suffixes.getOrDefault(player.getName(), "");

        String formattedMessage = prefix + player.getName() +  " " + suffix + ": " + event.getMessage();
        event.setFormat(formattedMessage);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        String prefix = prefixes.getOrDefault(player.getName(), "");
        String suffix = suffixes.getOrDefault(player.getName(), "");

        player.setPlayerListName(prefix + player.getName() + suffix);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        prefixes.remove(player.getName());
        suffixes.remove(player.getName());
    }
}
