package com.syntaxphoenix.spigot.timecycle.helper;

import com.syntaxphoenix.spigot.timecycle.helper.color.ColorIterator;
import com.syntaxphoenix.spigot.timecycle.helper.color.LinearGradient;
import com.syntaxphoenix.syntaxapi.utils.java.lang.StringBuilder;

import net.md_5.bungee.api.ChatColor;

public final class ColorHelper {

	private ColorHelper() {}

	public static final String HEX_FORMAT = "%02X";

	public static String toHexColor(float[] color) {
		StringBuilder builder = new StringBuilder("#");
		builder.append(String.format(HEX_FORMAT, Math.min(255, Math.abs(Math.round(color[0] * 255.0f)))));
		builder.append(String.format(HEX_FORMAT, Math.min(255, Math.abs(Math.round(color[1] * 255.0f)))));
		builder.append(String.format(HEX_FORMAT, Math.min(255, Math.abs(Math.round(color[2] * 255.0f)))));
		return builder.toStringClear();
	}

	public static String hexToMinecraftColor(String hexColor) {
		if (hexColor.startsWith("#")) {
			hexColor = hexColor.replace("#", "");
		}
		if (hexColor.length() < 6) {
			return null;
		}
		StringBuilder builder = new StringBuilder(ChatColor.COLOR_CHAR + "x");
		char[] chars = hexColor.toCharArray();
		for (char character : chars) {
			builder.append(ChatColor.COLOR_CHAR).append(character);
		}
		return builder.toStringClear();
	}

	public static float[] fromHexColor(String color) {
		float[] output = new float[3];
		if (color.startsWith("#")) {
			color = color.replace("#", "");
		}
		if (color.length() < 6) {
			return output;
		}
		output[0] = Integer.parseInt(color.substring(0, 2), 16) / 255.0f;
		output[1] = Integer.parseInt(color.substring(2, 4), 16) / 255.0f;
		output[2] = Integer.parseInt(color.substring(4, 6), 16) / 255.0f;
		return output;
	}

	public static float interpolate(float ratio, float color0, float color1) {
		return color0 * ratio + color1 * (1.0f - ratio);
	}
	
	public static ColorIterator linearGradient(String hexColor0, String hexColor1, int steps) {
		return new LinearGradient(fromHexColor(hexColor0), fromHexColor(hexColor1), steps);
	}

	public static ColorIterator linearGradient(String hexColor, int steps) {
		return new LinearGradient(fromHexColor(hexColor), fromHexColor(hexColor), steps);
	}

}
