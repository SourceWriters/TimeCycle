package com.syntaxphoenix.spigot.timecycle;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.syntaxphoenix.spigot.timecycle.command.LanguageCommand;
import com.syntaxphoenix.spigot.timecycle.command.TimeCommand;
import com.syntaxphoenix.spigot.timecycle.helper.Centering;
import com.syntaxphoenix.spigot.timecycle.language.FallbackHandler;
import com.syntaxphoenix.spigot.timecycle.language.Translations;
import com.syntaxphoenix.spigot.timecycle.settings.ConfigTimer;
import com.syntaxphoenix.spigot.timecycle.settings.config.DefaultConfig;
import com.syntaxphoenix.spigot.timecycle.sleep.PlayerBedListener;
import com.syntaxphoenix.spigot.timecycle.sleep.SleepStorage;
import com.syntaxphoenix.spigot.timecycle.sleep.SleepTimer;
import com.syntaxphoenix.spigot.timecycle.utils.version.MinecraftVersion;
import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LoggerState;
import com.syntaxphoenix.syntaxapi.logging.SynLogger;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import net.md_5.bungee.api.ChatColor;

public final class TimeCycle extends JavaPlugin {

	public static final MinecraftVersion VERSION = MinecraftVersion.fromString(Bukkit.getVersion().split(" ")[2].replace(")", ""));
	public static final MinecraftVersion NEEDED = new MinecraftVersion(1, 14, 0);

	private static Container<TimeCycle> PLUGIN = Container.of();
	private static Container<ILogger> LOGGER = Container.of();

	public static TimeCycle getPlugin() {
		if (PLUGIN.filter(JavaPlugin::isEnabled).isPresent()) {
			return PLUGIN.get();
		}
		return PLUGIN.replace(getPlugin(TimeCycle.class)).get();
	}

	public static ILogger getPluginLogger() {
		if (LOGGER.isPresent()) {
			return LOGGER.get();
		}
		return LOGGER.replace(new SynLogger(System.out, LoggerState.EXTENDED_STREAM)).get();
	}

	/*
	 * 
	 */

	private BukkitTask configTask;
	private BukkitTask sleepTask;

	@Override
	public void onEnable() {

		if (VERSION.isLower(NEEDED)) {
			Bukkit.getConsoleSender().sendMessage(Centering.center(ChatColor.translateAlternateColorCodes('&', ""
				+ "\n" + "&8======================================================="
				+ "\n" + "&3TimeCycle"
				+ "\n" + "&8<----------->"
				+ "\n" + "&7Your Minecraft version &b" + VERSION.toString() + " &7is &3unsupported&7!"
				+ "\n" + "&7Please use to &b1.14.0&7 or &3higher &7instead!"
				+ "\n" + "&8======================================================="
			)));
			return;
		}

		setupConfig();
		setupSleep();
		new PlayerBedListener(this);
		
		register("timecycle", new LanguageCommand());
		
	}

	@Override
	public void onDisable() {

		if (VERSION.isLower(NEEDED)) {
			return;
		}

		sleepTask.cancel();
		configTask.cancel();

	}

	/*
	 * 
	 */

	private void setupConfig() {
		// Register Fallback Listener
		Bukkit.getPluginManager().registerEvents(new FallbackHandler(), this);
		
		// Load Translations
		Translations.MANAGER.reloadCatch();
		
		// Setup ConfigTimer -> 5s delay / 3s interval
		configTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, ConfigTimer.TIMER, 0, 60);

		// Load configs
		ConfigTimer.TIMER.load(DefaultConfig.CONFIG);
	}

	private void setupSleep() {
		// Setup SleepTimer -> 3s delay / 4t interval
		sleepTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, SleepTimer.TIMER, 60, 4);

		SleepStorage.STORAGE.reload();

	}
	
	/*
	 * 
	 */
	
	private void register(String name, TimeCommand command) {
		PluginCommand plugin = getCommand(name);
		plugin.setExecutor(command);
		plugin.setTabCompleter(command);
	}

}
