# AutoServerUpdater

**AutoServerUpdater** is a lightweight utility designed to ensure your Minecraft server always runs on the latest available build for your specific version. This project is a **fork** of the original [AutoServerUpdater](https://github.com/notTamion/AutoServerUpdater), specifically updated to maintain compatibility with modern server infrastructures.

### 🔄 Fork Improvements

The primary goal of this fork was to modernize the update logic to support the latest industry standards.

  * **Fill v3 API Implementation:** Fully compatible with the new PaperMC "Fill" download service.
  * **Mandatory Identity Headers:** Implements the required `User-Agent` headers and contact information as per PaperMC’s latest security and analytics requirements.
  * **Intelligent Build Tracking:** Instead of relying on sequential API sorting, this version scans all available build metadata to find the highest build ID, ensuring accuracy even for Alpha and Experimental releases.
  * **Redirect Handling:** Optimized to handle modern data CDNs used by major server software providers.

### 📦 Supported Software

This plugin currently supports automated updates for:

  * [Paper](https://papermc.io/)
  * [Purpur](https://purpurmc.org/)

### ⚠️ Disclaimer

> [\!DANGER]
> Using auto-updaters in production environments is at your own risk. This plugin will overwrite your server JAR and trigger a restart using `Bukkit.getServer().spigot().restart()`. Ensure you have a proper restart script and regular backups configured.

-----

**Note:** If no restart script is configured on your host, the server will shut down to apply the update but will not automatically start back up.
