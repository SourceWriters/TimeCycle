package com.syntaxphoenix.spigot.timecycle.sleep;

import static com.syntaxphoenix.spigot.timecycle.sleep.SleepTimer.TIMER;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.syntaxphoenix.spigot.timecycle.TimeCycle;
import com.syntaxphoenix.spigot.timecycle.language.IMessage;
import com.syntaxphoenix.spigot.timecycle.settings.config.DefaultConfig;
import com.syntaxphoenix.spigot.timecycle.utils.general.Placeholder;

public final class SleepStorage {

	public static final SleepStorage STORAGE = new SleepStorage();

	private final Map<UUID, Long> sleeper = Collections.synchronizedMap(new HashMap<>());

	private SleepStorage() {}

	public void reload() {
		TIMER.unloadAll();
		sleeper.clear();
		for (World world : Bukkit.getWorlds()) {
			load(world);
		}
	}

	private long load(World world) {
		if (world == null) {
			return 0;
		}
		List<Player> players = world.getPlayers();
		float max = players.size();
		long current = players.stream().filter(Player::isSleeping).count();
		if (current != 0) {
			float percentage = (current / max) * 100;
			if (percentage >= DefaultConfig.PERCENTAGE) {
				if (DefaultConfig.INSTANT_SKIP) {
					setTimeLater(world);
					return current;
				}
				TIMER.load(world);
			} else if (!DefaultConfig.INSTANT_SKIP) {
				TIMER.unload(world);
			}
		}
		return current;
	}

	private void innerUpdate(long current, float max, World world) {
		if (max == 0) {
			return;
		}
		float percentage = (current / max) * 100;
		if (percentage >= DefaultConfig.PERCENTAGE) {
			if (DefaultConfig.INSTANT_SKIP) {
				setTimeLater(world);
				return;
			}
			TIMER.load(world);
		} else if (!DefaultConfig.INSTANT_SKIP) {
			TIMER.unload(world);
		}
	}

	public long update(World world) {
		float max = world.getPlayers().size();
		long current = getSleeping(world.getUID());
		innerUpdate(current, max, world);
		return current;
	}

	public long getSleeping(UUID worldId) {
		if (sleeper.containsKey(worldId)) {
			return sleeper.get(worldId);
		}
		return load(Bukkit.getWorld(worldId));
	}

	public long getNeeded(World world) {
		float max = world.getPlayers().size();
		if (max <= 1) {
			return (int) max;
		}
		return (long) Math.ceil((max * DefaultConfig.PERCENTAGE) / 100);
	}

	public void setSleeping(UUID worldId, long value) {
		sleeper.put(worldId, value);
	}

	public void incSleeping(UUID worldId) {
		setSleeping(worldId, getSleeping(worldId) + 1);
	}

	public void decSleeping(UUID worldId) {
		long sleeping = getSleeping(worldId) - 1;
		if (sleeping < 0) {
			sleeping = 0;
		}
		setSleeping(worldId, sleeping);
	}

	public void setTimeLater(World world) {
		Bukkit.getScheduler().runTaskLater(TimeCycle.getPlugin(), () -> {
			world.setTime(SleepTimer.DAY_TIME);
			announce(world);
		}, 10);
	}

	public void announce(World world) {
		IMessage message = DefaultConfig.getMessage();
		for (Player player : world.getPlayers()) {
			message.send(player, Placeholder.of("player", player.getName()));
		}
	}

}
