package com.syntaxphoenix.spigot.timecycle.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.syntaxphoenix.spigot.timecycle.language.Language;
import com.syntaxphoenix.spigot.timecycle.language.LanguageProvider;
import com.syntaxphoenix.spigot.timecycle.language.Message;
import com.syntaxphoenix.spigot.timecycle.language.Translations;
import com.syntaxphoenix.spigot.timecycle.utils.general.Placeholder;

public class LanguageCommand extends TimeCommand {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			Message.ONLY_PLAYER.send(sender);
			return true;
		}

		if (args.length == 0) {
			Message.COMMAND_LANGUAGE_USAGE.send(sender);
			return true;
		}

		if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("timecycle.reload")) {
			Message.CONFIG_RELOAD_NEEDED.send(sender, Placeholder.of("name", "Translation"));
			Translations.MANAGER.reloadCatch();
			Message.CONFIG_RELOAD_DONE.send(sender, Placeholder.of("name", "Translation"));
			return true;
		}

		String[] arg = new String[] {
				args[0],
				args[0].toLowerCase(),
				args[0].toUpperCase()
		};

		Language[] languages = Translations.MANAGER.getLanguages();
		Language choosen = null;
		for (Language language : languages) {
			for (String current : arg) {
				if (language.getId().equalsIgnoreCase(current) || language.getName().equalsIgnoreCase(current)) {
					choosen = language;
				}
			}
		}

		if (choosen == null) {
			Message.COMMAND_LANGUAGE_NON222EXISTENT.send(sender, Placeholder.of("language", arg[0]));
			return true;
		}

		LanguageProvider.setLanguageOf((Player) sender, choosen);
		Message.COMMAND_LANGUAGE_CHANGED.send(sender, Placeholder.of("language", choosen.getName()));

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		ArrayList<String> list = new ArrayList<>();
		if (args.length == 0) {
			Language[] languages = Translations.MANAGER.getLanguages();
			for (Language language : languages) {
				list.add(language.getId());
				list.add(language.getName());
			}
			Collections.sort(list);
		} else if (args.length == 1) {
			String[] arg = new String[] {
					args[0],
					args[0].toLowerCase()
			};
			Language[] languages = Translations.MANAGER.getLanguages();
			for (Language language : languages) {
				for (String current : arg) {
					if (language.getId().equalsIgnoreCase(current) || language.getId().startsWith(current)) {
						list.add(language.getId());
					} else if (language.getName().equalsIgnoreCase(current) || language.getName().startsWith(current)) {
						list.add(language.getName());
					}
				}
			}
			Collections.sort(list);
		}
		return list;
	}

}
