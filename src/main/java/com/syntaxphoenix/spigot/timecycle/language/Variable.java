package com.syntaxphoenix.spigot.timecycle.language;

import java.util.function.Function;

import org.bukkit.plugin.Plugin;

import com.syntaxphoenix.spigot.timecycle.TimeCycle;
import com.syntaxphoenix.spigot.timecycle.helper.PrefixHelper;
import com.syntaxphoenix.syntaxapi.utils.java.UniCode;

public enum Variable implements IVariable {

	//
	// General Variables
	//
	PREFIX("$name &8" + UniCode.ARROWS_RIGHT + "&7", value -> value.replace("$name", PrefixHelper.getPrefix())),

	// Config types
	CONFIG_TYPE_DEFAULT("Default"),

	//
	;

	private final String value;
	private final Function<String, String> function;

	Variable(String value) {
		this(value, null);
	}

	Variable(String value, Function<String, String> function) {
		this.value = value;
		this.function = function;
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
	public Function<String, String> modifier() {
		return function;
	}

	@Override
	public Plugin getOwner() {
		return TimeCycle.getPlugin();
	}

}
