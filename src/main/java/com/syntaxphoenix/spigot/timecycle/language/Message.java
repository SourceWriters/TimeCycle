package com.syntaxphoenix.spigot.timecycle.language;

import org.bukkit.plugin.Plugin;

import com.syntaxphoenix.spigot.timecycle.TimeCycle;

public enum Message implements IMessage {

	//
	// General Messages
	//
	NO_PERMISSION("$prefix &7You're lacking the permission &3$permission &7to do this!"),
	ONLY_PLAYER("$prefix &7This can only be done as Player!"),
	WORK_IN_PROGRESS("$prefix &7This feature is &bwork in progress&7!"),

	// Command
	COMMAND_LANGUAGE_USAGE("$prefix &7Please provide the language you want to have!"),
	COMMAND_LANGUAGE_NON222EXISTENT("$prefix &7The language '&3$language&7' is non existent!"),
	COMMAND_LANGUAGE_CHANGED("$prefix &7Your language was updated to &b$language"),

	// Config
	CONFIG_RELOAD_NEEDED("$prefix &7Detected a change in config &b$name&7, reloading..."),
	CONFIG_RELOAD_DONE("$prefix &7Config &b$name &7was &3successfully&7 reloaded!"),

	// Good Morning
	SLEEP_GOOD222MORNING_DEFAULT("$prefix &7Good morning, &3$player&7!"),
	SLEEP_ENTER("$prefix &b$player &7started to sleep... &8[&b$now&8/&3$needed&8]"),

	//
	;

	private final String value;

	Message(String value) {
		this.value = value;
	}

	Message(String... values) {
		StringBuilder builder = new StringBuilder();
		int length = values.length - 1;
		for (int index = 0; index < values.length; index++) {
			builder.append(values[index]);
			if (index != length) {
				builder.append("\n");
			}
		}
		this.value = builder.toString();
	}

	/*
	 * 
	 */

	@Override
	public String id() {
		return id(name());
	}

	@Override
	public String value() {
		return value;
	}

	@Override
	public Plugin getOwner() {
		return TimeCycle.getPlugin();
	}

}
