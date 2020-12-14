package com.syntaxphoenix.spigot.timecycle.event.language;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.syntaxphoenix.spigot.timecycle.language.Translations.TranslationManager;
import com.syntaxphoenix.spigot.timecycle.language.Translations.TranslationManager.TranslationStorage;

public class TimeCycleTranslationLoadedEvent extends Event {

	private final TranslationManager manager;
	private final TranslationStorage storage;

	public TimeCycleTranslationLoadedEvent(TranslationManager manager, TranslationStorage storage) {
		this.manager = manager;
		this.storage = storage;
	}

	public final TranslationManager getManager() {
		return manager;
	}

	public final TranslationStorage getStorage() {
		return storage;
	}

	/*
	 * Bukkit Stuff
	 */

	public static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
