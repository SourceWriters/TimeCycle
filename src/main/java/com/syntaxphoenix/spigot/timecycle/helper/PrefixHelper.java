package com.syntaxphoenix.spigot.timecycle.helper;

import com.syntaxphoenix.spigot.timecycle.TimeCycle;
import com.syntaxphoenix.spigot.timecycle.helper.color.ColorIterator;
import com.syntaxphoenix.spigot.timecycle.settings.config.DefaultConfig;
import com.syntaxphoenix.spigot.timecycle.utils.version.MinecraftVersion;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import net.md_5.bungee.api.ChatColor;

public final class PrefixHelper {

	private static final Container<String> PREFIX = Container.of();
	private static final Container<Boolean> SUPPORTED = Container.of();
	private static final MinecraftVersion VERSION = new MinecraftVersion(1, 16, 0);

	private PrefixHelper() {}

	public static String getPrefix() {
		if (PREFIX.isPresent()) {
			return PREFIX.get();
		}
		String prefix = ChatColor.translateAlternateColorCodes('&', DefaultConfig.PREFIX);
		if (DefaultConfig.GRADIENT && isGradientSupported()) {
			prefix = ChatColor.stripColor(prefix);
			ColorIterator gradient = ColorHelper.linearGradient(DefaultConfig.GRADIENT_COLOR_1, DefaultConfig.GRADIENT_COLOR_0, prefix.length());
			StringBuilder builder = new StringBuilder();
			char[] chars = prefix.toCharArray();
			for (char character : chars) {
				builder.append(ColorHelper.hexToMinecraftColor(gradient.next())).append(character);
			}
			return PREFIX.replace(builder.toString()).get();
		} else {
			return PREFIX.replace(prefix).get();
		}
	}

	public static void clear() {
		PREFIX.replace(null);
	}

	public static boolean isGradientSupported() {
		if (SUPPORTED.isPresent()) {
			return SUPPORTED.get();
		}
		return SUPPORTED.replace(TimeCycle.VERSION.isHigher(VERSION) || TimeCycle.VERSION.isSimilar(VERSION)).get();
	}

}
