package com.panda0day.bungeePerms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.charset.StandardCharsets;
import java.util.*;

public final class BungeePerms extends JavaPlugin implements PluginMessageListener, Listener {
    private static final String CHANNEL = "bungee:permissions";
    private static BungeePerms instance;

    private final Map<String, String> cachedPrefixes = new HashMap<>();
    private final Map<String, String> cachedSuffixes = new HashMap<>();
    private final Map<String, String> cachedPermissions = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, this);
        getServer().getPluginManager().registerEvents(new Events(), this);
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

        handleReceivePrefixSuffix(data);
        handleReceivePermissions(data);
    }

    private void handleReceivePrefixSuffix(String[] data) {
        String action = data[0];
        if (!action.equals("setSuffixPrefix")) return;

        String playerUuid = data[1];
        String prefix = data[2];
        String suffix = data[3];
        if (suffix.equals("null")) {
            suffix = "";
        }

        cachedPrefixes.put(playerUuid, ChatColor.translateAlternateColorCodes('&', prefix));
        cachedSuffixes.put(playerUuid, ChatColor.translateAlternateColorCodes('&', suffix));
    }

    private void handleReceivePermissions(String[] data) {
        String action = data[0];
        if (!action.equals("setPermissions")) return;

        String playerUuid = data[1];
        List<String> permissions = new ArrayList<>();
        for (int i = 2; i < data.length; i++) {
            String permission = data[i];
            permissions.add(permission);
        }
        cachedPermissions.put(playerUuid, String.join(";", permissions));

        Player player = Bukkit.getPlayer(UUID.fromString(playerUuid));
        if (player == null) return;

        PermissionAttachment attachment = player.addAttachment(this);
        permissions.forEach(permission -> {
            attachment.setPermission(permission, true);
        });
    }

    public static String getChannel() {
        return CHANNEL;
    }

    public static BungeePerms getInstance() {
        return instance;
    }

    public Map<String, String> getCachedSuffixes() {
        return cachedSuffixes;
    }

    public Map<String, String> getCachedPrefixes() {
        return cachedPrefixes;
    }

    public Map<String, String> getCachedPermissions() {
        return cachedPermissions;
    }
}
