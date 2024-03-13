package org.by1337.bairx.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.by1337.bairx.BAirDropX;
import org.by1337.blib.BLib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionChecker implements Listener {
    public final String currentVersion;
    private String actualVersion;
    private String downloadLink;
    private String message;
    private String messageRaw = """
            ["",{"text":"[BAirDropX]","color":"green"},{"text":" Вышла новая версия плагина!"},{"text":" Текущая '"},{"text":"%s","color":"red"},{"text":"', новая '"},{"text":"%s","color":"green"},{"text":"'","color":"#6AAB73"},{"text":"\\n"},{"text":"[BAirDropX]","color":"green"},{"text":" Ссылка на скачивание "},{"text":"%s","italic":true,"color":"aqua","clickEvent":{"action":"open_url","value":"%s"},"hoverEvent":{"action":"show_text","contents":"%s"}}]
            """;
    private final Listener listener;

    public VersionChecker() {
        listener = this;
        currentVersion = BAirDropX.getInstance().getDescription().getVersion();
        new Thread(() -> {
            String result = parsePage();
            if (result != null) {
                String[] args = result.split("=");
                actualVersion = args[0];
                downloadLink = args[1];
                if (actualVersion.equals(currentVersion)) return;
                message = String.format(messageRaw, currentVersion, actualVersion, downloadLink, downloadLink, downloadLink);
                Bukkit.getPluginManager().registerEvents(listener, BAirDropX.getInstance());
                Bukkit.getOnlinePlayers().forEach(this::trySendUpdateMessage);
            }
        }).start();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        trySendUpdateMessage(player);
    }

    private void trySendUpdateMessage(Player player) {
        if (player.hasPermission("bair.update")) {
            BLib.getApi().getCommandUtil().tellRaw(
                    message,
                    player
            );
        }
    }

    private String parsePage() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://raw.githubusercontent.com/By1337/BAirDropX/master/version");
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(15000);
            connection.setRequestMethod("GET");

            int code = connection.getResponseCode();

            if (code == 200) {
                try (InputStream inputStream = connection.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    return String.join("\n", reader.lines().toList());
                }
            }
        } catch (IOException ignore) {
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}
