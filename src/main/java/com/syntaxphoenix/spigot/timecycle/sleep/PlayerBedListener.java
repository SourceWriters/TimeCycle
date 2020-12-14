package com.syntaxphoenix.spigot.timecycle.sleep;

import static com.syntaxphoenix.spigot.timecycle.sleep.SleepStorage.STORAGE;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.event.world.TimeSkipEvent.SkipReason;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.syntaxphoenix.spigot.timecycle.TimeCycle;
import com.syntaxphoenix.spigot.timecycle.language.LanguageProvider;
import com.syntaxphoenix.spigot.timecycle.language.Message;
import com.syntaxphoenix.spigot.timecycle.utils.general.Placeholder;

public class PlayerBedListener implements Listener {

	public PlayerBedListener(TimeCycle plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnter(PlayerBedEnterEvent event) {
		if (event.getBedEnterResult() != BedEnterResult.OK) {
			return;
		}
		World world = event.getBed().getWorld();
		if (world.getEnvironment() != Environment.NORMAL) {
			return;
		}
		STORAGE.incSleeping(world.getUID());
		long sleeping = STORAGE.getSleeping(world.getUID());
		long needed = STORAGE.getNeeded(world);
		STORAGE.update(world);
		for (Player player : world.getPlayers()) {
			Message.SLEEP_ENTER.send(player, Placeholder.of("player", event.getPlayer().getName()), Placeholder.of("now", sleeping),
				Placeholder.of("needed", needed));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeave(PlayerBedLeaveEvent event) {
		World world = event.getBed().getWorld();
		if (world.getEnvironment() != Environment.NORMAL) {
			return;
		}
		STORAGE.decSleeping(world.getUID());
		STORAGE.update(world);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSkip(TimeSkipEvent event) {
		World world = event.getWorld();
		if (world.getEnvironment() != Environment.NORMAL) {
			return;
		}
		if (event.getSkipReason() != SkipReason.NIGHT_SKIP) {
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldChange(PlayerChangedWorldEvent event) {
		World from = event.getFrom();
		if (from.getEnvironment() != Environment.NORMAL) {
			return;
		}
		STORAGE.update(from);
		World now = event.getPlayer().getWorld();
		if (now.getEnvironment() != Environment.NORMAL) {
			return;
		}
		STORAGE.update(now);
	}

	@EventHandler
	public void onLocaleChange(PlayerLocaleChangeEvent event) {
		LanguageProvider.updateLanguageOf(event.getPlayer(), event.getLocale());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		LanguageProvider.updateLanguageOf(event.getPlayer());
		World world = event.getPlayer().getWorld();
		if (world.getEnvironment() != Environment.NORMAL) {
			return;
		}
		STORAGE.update(world);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		World world = event.getPlayer().getWorld();
		if (world.getEnvironment() != Environment.NORMAL) {
			return;
		}
		STORAGE.update(world);
	}

}
