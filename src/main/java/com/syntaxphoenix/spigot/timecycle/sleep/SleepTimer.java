package com.syntaxphoenix.spigot.timecycle.sleep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.syntaxphoenix.spigot.timecycle.TimeCycle;
import com.syntaxphoenix.spigot.timecycle.settings.config.DefaultConfig;

public final class SleepTimer implements Runnable {

	public static final SleepTimer TIMER = new SleepTimer();

	public static final long DAY_TIME = 23458;
	public static final long NIGHT_TIME = 12541;

	private final List<World> speedUp = Collections.synchronizedList(new ArrayList<>());

	private SleepTimer() {}

	public void unloadAll() {
		World[] worlds = speedUp.toArray(new World[0]);
		for (World world : worlds) {
			unload(world);
		}
	}

	public boolean load(World world) {
		boolean output;
		if (output = !speedUp.contains(world)) {
			speedUp.add(world);
		}
		return output;
	}

	public boolean unload(World world) {
		boolean output;
		if (output = speedUp.contains(world)) {
			speedUp.remove(world);
		}
		return output;
	}

	@Override
	public void run() {
		if (speedUp.isEmpty()) {
			return;
		}
		World[] worlds = speedUp.toArray(new World[0]);
		long modifier = Math.round(12 * DefaultConfig.SPEED_MODIFIER);
		for (World world : worlds) {
			long time = world.getTime();
			time += modifier;
			if (time >= 24000) {
				time = (24000 - time) * (-1);
			}
			setTime(world, time);
			if ((time >= DAY_TIME) || (time <= NIGHT_TIME && time >= 0)) {
				unload(world);
				SleepStorage.STORAGE.announce(world);
			}
		}
	}

	private void setTime(World world, long time) {
		Bukkit.getScheduler().runTask(TimeCycle.getPlugin(), () -> world.setTime(time));
	}

}
