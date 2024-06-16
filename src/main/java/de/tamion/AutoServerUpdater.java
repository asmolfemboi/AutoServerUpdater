package de.tamion;

import com.destroystokyo.paper.util.VersionFetcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.papermc.paper.ServerBuildInfo;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;

public final class AutoServerUpdater extends JavaPlugin {

    @Override
    public void onEnable() {
        File serverjar = new File(System.getProperty("java.class.path"));
        ServerBuildInfo buildInfo = ServerBuildInfo.buildInfo();

        String build = "" + buildInfo.buildNumber().orElse(-1);
        String version = buildInfo.minecraftVersionId();
        try {
            if (buildInfo.brandName().contains("Paper")) {
                String[] builds = new ObjectMapper().readTree(new URL("https://api.papermc.io/v2/projects/paper/versions/" + version)).get("builds").toString().replaceAll("\\[", "").replaceAll("]", "").split(",");
                String latestbuild = builds[builds.length - 1];
                if (!latestbuild.equals(build)) {
                    getLogger().warning("Old Paper build detected. Updating from build " + build + " to " + latestbuild);
                    FileUtils.copyURLToFile(new URL("https://api.papermc.io/v2/projects/paper/versions/" + version + "/builds/" + latestbuild + "/downloads/paper-" + version + "-" + latestbuild + ".jar"), serverjar);
                    getLogger().warning("Downloaded Paper build " + latestbuild + ". Restarting... If no restart script has been setup you will need to manually start the server");
                    Bukkit.getServer().spigot().restart();
                    return;
                }
            } else if (buildInfo.brandName().contains("Purpur")) {
                String latestbuild = new ObjectMapper().readTree(new URL("https://api.purpurmc.org/v2/purpur/" + version + "/latest")).get("build").asText();
                if (!latestbuild.equals(build)) {
                    getLogger().warning("Old Purpur build detected. Updating from build " + build + " to " + latestbuild);
                    FileUtils.copyURLToFile(new URL("https://api.purpurmc.org/v2/purpur/" + version + "/" + latestbuild + "/download"), serverjar);
                    getLogger().warning("Downloaded Purpur build " + latestbuild + ". Restarting... If no restart script has been setup you will need to manually start the server");
                    Bukkit.getServer().spigot().restart();
                    return;
                }
            } else {
                getLogger().log(Level.SEVERE, "SERVER SOFTWARE NOT SUPPORTED BY AUTOSERVERUPDATER");
            }
            getLogger().info("Latest Build installed!");
            Bukkit.getPluginManager().disablePlugin(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
