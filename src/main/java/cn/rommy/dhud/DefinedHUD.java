
package cn.rommy.dhud;

import cn.rommy.dhud.command.CoordMode;
import cn.rommy.dhud.command.DarkMode;
import cn.rommy.dhud.command.TimeMode;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class DefinedHUD extends JavaPlugin {

    protected static DefinedHUD instance;

    public DefinedHUD() {
        instance = this;
    }

    // import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;

    // Version for reflection
    private static String versionStr;

    /** Time elapsed for the last update. */
    private long benchmarkStart;

    protected BukkitTask msgSenderTask;
    protected BukkitTask biomeUpdateTask;

    /**
     * Initial setup:
     * Load config, get version.
     * Configure CommandHandler.
     */
    @Override
    public void onEnable() {
        try {
            Util.printToTerminal(Util.GRN + "DefinedHUD Enabling...");

            // Save initial cfg or load.
            this.saveDefaultConfig(); // Silent fails if config.yml already exists
            if (!Util.loadConfig()) {
                throw new Exception(Util.ERR + "Error while reading config.yml.");
            }

            // Version check
            // Eg: org.bukkit.craftbukkit.v1_16_R2.blabla
            String ver = Bukkit.getServer().getClass().getPackage().getName();
            versionStr = ver.split("\\.")[3];
            Util.apiVersion = Integer.parseInt(versionStr.split("_")[1]);
            Util.serverVendor = ver.split("\\.")[2];

            // Setup command executor
            this.getCommand(Util.CMD_NAME).setExecutor(new CommandExecutor(this));

            // Start sender and biome updater tasks
            msgSenderTask = startMessageUpdaterTask(Util.getMessageUpdateDelay());
            biomeUpdateTask = startBiomeUpdaterTask(Util.getBiomeUpdateDelay());

            // Print "enabling success" message to console (maybe having no need for color?)
            Util.printToTerminal(Util.GRY + ">>" + Util.GRN + " DefinedHUD enabled successfully! Server version: " + versionStr);
        }
        catch (Exception e) {
            Util.printToTerminal(e.getMessage());
            Util.printToTerminal("Shutting down...");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    /** Clean up while shutting down (Currently nothing). */
    @Override
    public void onDisable() {
        Util.printToTerminal(Util.GRN + "DefinedHUD disabled successfully!");
    }

    /**
     * Starts task whose job is to get each player's config and send the right
     * message accordingly. Does NOT change any value from {@link PlayerCfg}.
     * @return BukkitTask created.
     */
    public BukkitTask startMessageUpdaterTask(long refreshPeriod) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            benchmarkStart = System.nanoTime();
            for (Player p : instance.getServer().getOnlinePlayers()) {

                // Skip players that are not on the list
                if (!PlayerCfg.isEnabled(p)) {
                    continue;
                }

                // Assumes that online players << saved players

                PlayerCfg cfg = PlayerCfg.getConfig(p);

                if (cfg.coordMode == CoordMode.DISABLED && cfg.timeMode == TimeMode.DISABLED) {
                    PlayerCfg.removePlayer(p);
                    continue;
                }

                // Setting dark mode colors -> Assume disabled : 0
                String color1; // Text
                String color2; // Values

                if (cfg.darkMode == DarkMode.AUTO) {
                    if (cfg.isInBrightBiome) {
                        color1 = Util.dark1;
                        color2 = Util.dark2;
                    }
                    else {
                        color1 = Util.bright1;
                        color2 = Util.bright2;
                    }
                }
                else if (cfg.darkMode == DarkMode.DISABLED) {
                    color1 = Util.bright1;
                    color2 = Util.bright2;
                }
                else { // DarkMode.ENABLED
                    color1 = Util.dark1;
                    color2 = Util.dark2;
                }

                // Coordinates enabled
                if (cfg.coordMode == CoordMode.ENABLED) {
                    switch (cfg.timeMode) {
                        case DISABLED:
                            sendToActionBar(p, color1 + "XYZ: "
                                    + color2 + CoordMode.getCoordinates(p) + " "
                                    + color1 + Util.getPlayerDirection(p));
                            break;
                        case CURRENT_TICK:
                            sendToActionBar(p, color1 + "XYZ: "
                                    + color2 + CoordMode.getCoordinates(p) + " "
                                    + color1 + String.format("%-10s", Util.getPlayerDirection(p))
                                    + color2 + TimeMode.getTimeTicks(p));
                            break;
                        case CLOCK24:
                            sendToActionBar(p, color1 + "XYZ: "
                                    + color2 + CoordMode.getCoordinates(p) + " "
                                    + color1 + String.format("%-10s", Util.getPlayerDirection(p))
                                    + color2 + TimeMode.getTime24(p));
                            break;
                        case CLOCK12:
                            sendToActionBar(p, color1 + "XYZ: "
                                    + color2 + CoordMode.getCoordinates(p) + " "
                                    + color1 + String.format("%-10s", Util.getPlayerDirection(p))
                                    + color2 + TimeMode.getTime12(p, color1, color2));
                            break;
                        case VILLAGER_SCHEDULE:
                            sendToActionBar(p, color1 + "XYZ: "
                                    + color2 + CoordMode.getCoordinates(p) + " "
                                    + color1 + String.format("%-10s", Util.getPlayerDirection(p))
                                    + color2 + TimeMode.getVillagerTime(p, color1, color2));
                            break;
                        default: // Ignored
                    }
                }

                // Coordinates disabled
                else if (cfg.coordMode == CoordMode.DISABLED) {
                    switch (cfg.timeMode) {
                        case CURRENT_TICK:
                            sendToActionBar(p, color2 + TimeMode.getTimeTicks(p));
                            break;
                        case CLOCK12:
                            sendToActionBar(p, color2 + TimeMode.getTime12(p, color1, color2));
                            break;
                        case CLOCK24:
                            sendToActionBar(p, color2 + TimeMode.getTime24(p));
                            break;
                        case VILLAGER_SCHEDULE:
                            sendToActionBar(p, color2 + TimeMode.getVillagerTime(p, color1, color2));
                            break;
                        default: // Ignored
                    }
                }
            }
            Util.benchmark = System.nanoTime() - benchmarkStart;
        }, 0L, refreshPeriod);
    }

    /**
     * Runs expensive tasks in a new thread (for now, biome fetching).
     */
    public BukkitTask startBiomeUpdaterTask(long refreshPeriod) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            for (Player p : instance.getServer().getOnlinePlayers()) {
                if (PlayerCfg.isEnabled(p)) {
                    if (PlayerCfg.getConfig(p).darkMode == DarkMode.AUTO) {
                        Util.updateIsInBrightBiome(p);
                    }
                }
            }
        }, 0L, refreshPeriod);
    }

    /**
     * Sends a message to the player's actionbar using reflected methods.
     * @param p Recipient player.
     * @param msg Message to send.
     */
    private static void sendToActionBar(Player p, String msg) {
        // Using Spigot-API instead of NMS reflection.
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
    }
}
