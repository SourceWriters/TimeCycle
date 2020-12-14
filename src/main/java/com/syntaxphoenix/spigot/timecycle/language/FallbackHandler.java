package com.syntaxphoenix.spigot.timecycle.language;

import static com.syntaxphoenix.spigot.timecycle.language.TranslationType.*;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.syntaxphoenix.spigot.timecycle.event.language.TimeCycleTranslationEvent;
import com.syntaxphoenix.spigot.timecycle.event.language.TimeCycleTranslationLoadedEvent;
import com.syntaxphoenix.spigot.timecycle.helper.PrefixHelper;
import com.syntaxphoenix.spigot.timecycle.language.Translations.TranslationManager.TranslationStorage;

public class FallbackHandler implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLoad(TimeCycleTranslationEvent event) {

		TranslationStorage storage = event.getStorage();

		for (Message message : Message.values()) {
			storage.set(message.translationId(), MESSAGE, message.value());
		}

		for (Variable variable : Variable.values()) {
			storage.set(variable.translationId(), VARIABLE, variable.value());
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDone(TimeCycleTranslationLoadedEvent event) {
		
		PrefixHelper.clear();
		
	}

}
