package com.panda0day.bungeePerms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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

        setPlayerTabPriority(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        BungeePerms.getInstance().getCachedPrefixes().remove(player.getName());
        BungeePerms.getInstance().getCachedSuffixes().remove(player.getName());
    }

    public void setPlayerTabPriority(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null || scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }

        String prefix = BungeePerms.getInstance().getCachedPrefixes().getOrDefault(player.getUniqueId().toString(), "");
        String suffix = BungeePerms.getInstance().getCachedSuffixes().getOrDefault(player.getUniqueId().toString(), "");

        String teamName = getPriority(prefix);
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        team.setPrefix(prefix);
        team.setSuffix(suffix);

        // Remove player from any other teams
        for (Team t : scoreboard.getTeams()) {
            if (t.hasEntry(player.getName())) {
                t.removeEntry(player.getName());
            }
        }

        team.addEntry(player.getName());
    }


    private String getPriority(String prefix) {
        if (prefix.contains("Owner")) return "0_Owner";
        if (prefix.contains("Manager")) return "1_Manager";
        if (prefix.contains("Player")) return "2_Player";
        return "3_Default";
    }
}
