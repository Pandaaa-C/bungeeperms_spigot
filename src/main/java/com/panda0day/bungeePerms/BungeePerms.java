package com.panda0day.bungeePerms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class BungeePerms extends JavaPlugin implements PluginMessageListener, Listener {
    private static final String CHANNEL = "bungee:permissions";
    private final Map<String, String> prefixes = new HashMap<>();
    private final Map<String, String> suffixes = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(getChannel())) return;

        String receivedMessage = new String(message, StandardCharsets.UTF_8);
        String[] data = receivedMessage.split(";");
        String action = data[0];
        if (!action.equals("setSuffixPrefix")) return;

        String playerName = data[1];
        String prefix = data[2];
        String suffix = data[3];
        if (suffix.equals("null")) {
            suffix = "";
        }

        prefixes.put(playerName, prefix);
        suffixes.put(playerName, suffix);

        getLogger().info("Cached prefix and suffix for " + playerName + ": " + prefix + " | " + suffix);
    }

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

    public CompletableFuture<String> queryPrefix(Player player) {
        CompletableFuture<String> future = new CompletableFuture<>();
        String message = "getPrefix;" + player.getName();

        player.sendPluginMessage(this, CHANNEL, message.getBytes());

        Bukkit.getScheduler().runTaskLater(this, () -> {
            String prefix = prefixes.getOrDefault(player.getName(), "");
            future.complete(prefix);
        }, 10L);
        return future;
    }

    public CompletableFuture<String> querySuffix(Player player) {
        CompletableFuture<String> future = new CompletableFuture<>();
        String message = "getSuffix;" + player.getName();
        player.sendPluginMessage(this, getChannel(), message.getBytes());

        getServer().getScheduler().runTaskLater(this, () -> {
            String suffix = suffixes.getOrDefault(player.getName(), "");
            future.complete(suffix);
        }, 10L);

        return future;
    };

    public void updatePlayerPrefix(String playerName, String newPrefix) {
        prefixes.put(playerName, newPrefix);
    }

    public void updatePlayerSuffix(String playerName, String newSuffix) {
        suffixes.put(playerName, newSuffix);
    }

    public static String getChannel() {
        return CHANNEL;
    }
}
