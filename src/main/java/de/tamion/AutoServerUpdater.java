package de.tamion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.papermc.paper.ServerBuildInfo;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

public final class AutoServerUpdater extends JavaPlugin {

    private final String USER_AGENT = "AutoServerUpdater/1.0 (contact@tamion.de)";

    @Override
    public void onEnable() {
        saveDefaultConfig();

        File serverJar = new File(System.getProperty("java.class.path"));
        ServerBuildInfo buildInfo = ServerBuildInfo.buildInfo();

        int currentBuildInt = buildInfo.buildNumber().isPresent() ? buildInfo.buildNumber().getAsInt() : -1;
        String mcVersion = buildInfo.minecraftVersionId();

        try {
            String downloadUrl = null;
            String latestBuildStr = null;

            if (buildInfo.brandName().contains("Paper")) {
                JsonNode root = fetchJson("https://fill.papermc.io/v3/projects/paper/versions/" + mcVersion + "/builds");

                if (root != null && root.isArray() && root.size() > 0) {
                    int highestBuildFound = -1;
                    JsonNode bestBuildObj = null;

                    for (JsonNode buildNode : root) {
                        if (buildNode.has("id")) {
                            int buildId = buildNode.get("id").asInt();
                            if (buildId > highestBuildFound) {
                                highestBuildFound = buildId;
                                bestBuildObj = buildNode;
                            }
                        }
                    }

                    if (bestBuildObj != null) {
                        latestBuildStr = String.valueOf(highestBuildFound);
                        if (highestBuildFound > currentBuildInt) {
                            JsonNode downloads = bestBuildObj.get("downloads");
                            if (downloads != null && downloads.has("server:default")) {
                                downloadUrl = downloads.get("server:default").get("url").asText();
                            }
                        }
                    }
                }

            } else if (buildInfo.brandName().contains("Purpur")) {
                JsonNode root = fetchJson("https://api.purpurmc.org/v2/purpur/" + mcVersion + "/latest");
                if (root != null && root.has("build")) {
                    latestBuildStr = root.get("build").asText();
                    int latestPurpurBuild = Integer.parseInt(latestBuildStr);
                    if (latestPurpurBuild > currentBuildInt) {
                        downloadUrl = "https://api.purpurmc.org/v2/purpur/" + mcVersion + "/" + latestBuildStr + "/download";
                    }
                }
            }

            if (downloadUrl != null) {
                getLogger().warning("Update found! Current build: " + currentBuildInt + " -> New build: " + latestBuildStr);
                downloadFile(downloadUrl, serverJar);

                String action = getConfig().getString("update-action", "RESTART").toUpperCase();
                if (action.equals("STOP")) {
                    getLogger().warning("Download complete. Shutting down server as requested...");
                    Bukkit.shutdown();
                } else {
                    getLogger().warning("Download complete. Triggering Spigot restart...");
                    Bukkit.getServer().spigot().restart();
                }
            } else {
                getLogger().info("Server is up to date (Build " + currentBuildInt + ").");
                Bukkit.getPluginManager().disablePlugin(this);
            }

        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error during update process: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JsonNode fetchJson(String urlString) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(5000);
        if (conn.getResponseCode() != 200) return null;
        try (InputStream in = conn.getInputStream()) {
            return new ObjectMapper().readTree(in);
        }
    }

    private void downloadFile(String urlString, File target) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setInstanceFollowRedirects(true);
        try (InputStream in = conn.getInputStream()) {
            FileUtils.copyInputStreamToFile(in, target);
        }
    }
}