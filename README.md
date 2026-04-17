# 🚀 AutoServerUpdater

**Keep your server running on the latest security and performance patches without lifting a finger.**

This project is a **fork** of the original [AutoServerUpdater](https://github.com/notTamion/AutoServerUpdater), specifically updated to maintain compatibility with modern server infrastructures and the latest API standards.

### 🔄 Why this fork?
The original plugin broke after **PaperMC migrated their download service from Bibliothek (v2) to Fill (v3)** and deprecated legacy endpoints. This fork resolves those connection issues and introduces more robust build-tracking logic.

### ✨ Key Features
* **API v3 Support:** Fully compatible with the new PaperMC Fill download service.
* **Intelligent Build Tracking:** Scans all available API metadata to identify the highest build ID, ensuring accuracy even for Alpha and Experimental releases.
* **Configurable Update Actions:** Choose between a Spigot-native `RESTART` or a clean `STOP` (perfect for Docker or Pterodactyl users).
* **User-Agent Compliance:** Implements mandatory identity headers to comply with PaperMC's latest infrastructure requirements.
* **Purpur & Paper Support:** Seamlessly handles updates for both major server softwares.

### 📦 Supported Software
* [Paper](https://papermc.io/)
* [Purpur](https://purpurmc.org/)

### 🛠 Configuration
The plugin creates a `config.yml` on the first run. You can customize the behavior after a download:
```yaml
# Options: 
# RESTART - Uses Spigot's restart command (default).
# STOP    - Safely shuts down the server. Recommended for loop scripts.
update-action: RESTART
```

### ⚠️ Disclaimer
> [!DANGER]
> Using auto-updaters in production environments is at your own risk. This plugin overwrites your active server JAR. Always ensure you have regular backups configured before enabling automated updates.

**Important regarding `while true` loops:**
If you are using a startup loop (e.g., `while true; do java...; done`), it is **highly recommended** to set `update-action` to `STOP`. 

Using `RESTART` in combination with a shell loop often triggers a new server instance before the old one has fully terminated. This can lead to:
* **`Failed to bind to port`** errors (Port already in use).
* **`session.lock`** errors (World already in use).
* **Zombie processes** (Multiple instances running simultaneously).

---

**Note:** If no restart script or loop is configured on your host, the server will shut down to apply the update but will not automatically start back up.

### ⚖️ License & Fork Information
* **Divergence:** This version contains a complete rewrite of the API fetching logic (switching to Fill v3), adds JSON parsing via Jackson, and implements mandatory User-Agent headers and configurable shutdown logic.
* **Original License:** The original project is licensed under the **MIT License**.
