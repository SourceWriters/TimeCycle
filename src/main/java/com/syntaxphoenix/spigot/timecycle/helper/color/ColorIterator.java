package com.syntaxphoenix.spigot.timecycle.helper.color;

import java.util.Iterator;

import com.syntaxphoenix.spigot.timecycle.helper.ColorHelper;

public abstract class ColorIterator implements Iterator<String> {

	@Override
	public String next() {
		return ColorHelper.toHexColor(nextColor());
	}

	public abstract float[] nextColor();
	
	public abstract void reset();

}
