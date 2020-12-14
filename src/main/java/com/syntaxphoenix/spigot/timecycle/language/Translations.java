package com.syntaxphoenix.spigot.timecycle.language;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

import org.bukkit.Bukkit;

import com.syntaxphoenix.spigot.timecycle.TimeCycle;
import com.syntaxphoenix.spigot.timecycle.event.language.TimeCycleTranslationEvent;
import com.syntaxphoenix.spigot.timecycle.utils.general.Placeholder;
import com.syntaxphoenix.syntaxapi.config.yaml.YamlConfig;

public final class Translations {

	public static final TranslationManager MANAGER = new TranslationManager();

	/*
	 * 
	 */

	public static String translate(Language language, IMessage message) {
		return translate(language.getId(), message);
	}

	public static String translate(Language language, IMessage message, Placeholder... placeholders) {
		return translate(language.getId(), message, placeholders);
	}

	public static String translate(Language language, IVariable variable) {
		return translate(language.getId(), variable);
	}

	public static String translate(Language language, IVariable variable, Placeholder... placeholders) {
		return translate(language.getId(), variable, placeholders);
	}

	public static String translate(Language language, String id, TranslationType type) {
		return translate(language.getId(), id, type);
	}

	public static String translate(Language language, String id, TranslationType type, Placeholder... placeholders) {
		return translate(language.getId(), id, type, placeholders);
	}

	/*
	 * 
	 */

	public static String translate(String language, IMessage message) {
		return message(language, message.translationId());
	}

	public static String translate(String language, IMessage message, Placeholder... placeholders) {
		return message(language, message.translationId(), placeholders);
	}

	public static String translate(String language, IVariable variable) {
		return variable(language, variable.translationId());
	}

	public static String translate(String language, IVariable variable, Placeholder... placeholders) {
		return variable(language, variable.translationId());
	}

	public static String translate(String language, String id, TranslationType type) {
		return MANAGER.getTranslation(language).get(id, type);
	}

	public static String translate(String language, String id, TranslationType type, Placeholder... placeholders) {
		return replace(language, translate(language, id, type), placeholders);
	}

	/*
	 * 
	 */

	public static String message(String language, String id) {
		return replaceDefaults(language, translate(language, id, TranslationType.MESSAGE));
	}

	public static String variable(String language, String id) {
		return translate(language, id, TranslationType.VARIABLE);
	}

	public static String message(String language, String id, Placeholder... placeholders) {
		return translate(language, id, TranslationType.MESSAGE, placeholders);
	}

	public static String variable(String language, String id, Placeholder... placeholders) {
		return translate(language, id, TranslationType.VARIABLE, placeholders);
	}

	/*
	 * 
	 */

	public static String replace(String language, String message, Placeholder... placeholders) {
		if (message.equals(LanguageProvider.NULL_VALUE))
			return message;
		for (Placeholder placeholder : placeholders)
			message = placeholder.replaceTranslated(language, message);
		return replaceDefaults(language, message);
	}

	public static String replaceDefaults(String language, String message) {
		if (message.equals(LanguageProvider.NULL_VALUE))
			return message;
		Placeholder[] placeholders = Arrays.stream(Variable.values()).map(ITranslatable::placeholder).toArray(Placeholder[]::new);
		for (Placeholder placeholder : placeholders)
			message = placeholder.replaceTranslated(language, message);
		return replaceConsens(message);
	}

	public static String replaceConsens(String message) {
		if (message.equals(LanguageProvider.NULL_VALUE))
			return message;
		Placeholder[] placeholders = Consens.get();
		for (Placeholder placeholder : placeholders)
			message = placeholder.replace(message);
		return message;
	}

	/*
	 * 
	 * 
	 */

	public static final class TranslationManager {

		private final File folder = new File("plugins//TimeCycle//translation");
		private final File languageFile = new File(folder, "languages.yml");

		private final ArrayList<TranslationStorage> translations = new ArrayList<>();
		private final TranslationStorage fallback = new TranslationStorage("default", folder);

		private final ArrayList<Language> languages = new ArrayList<>();
		private final YamlConfig language = new YamlConfig();

		private TranslationManager() {}

		/*
		 * 
		 */

		public TranslationManager reloadCatch() {
			try {
				return reload();
			} catch (IOException e) {
				TimeCycle.getPluginLogger().log(e);
			}
			return this;
		}

		public TranslationManager reload() throws IOException {

			//
			// Clear fallback translations

			fallback.clear();

			//
			// Load fallback from source

			Bukkit.getPluginManager().callEvent(new TimeCycleTranslationEvent(this, fallback));

			//
			// Overwrite fallback from file

			fallback.reload(false);

			//
			// Search for new languages

			File[] folders = folder.listFiles();
			for (File folder : folders) {
				if (!folder.isDirectory())
					continue;
				String name = folder.getName();
				if (name.equals("default"))
					continue;
				if (translations.stream().anyMatch(storage -> storage.getLanguage().equals(name)))
					continue;
				translations.add(new TranslationStorage(name, this.folder));
			}

			//
			// Reload all languages

			if (!translations.isEmpty()) {
				for (TranslationStorage translation : translations) {
					translation.reload();
				}
			}
			fallback.reload(false);

			//
			// Clear language names

			language.clear();
			languages.clear();

			//
			// Set default language name

			language.set(LanguageProvider.DEFAULT_LANGUAGE.getId(), LanguageProvider.DEFAULT_LANGUAGE.getName());

			//
			// Load language names

			language.load(languageFile);
			if (!languageFile.exists()) {
				languageFile.createNewFile();
			}

			//
			// Create language objects

			LanguageProvider.DEFAULT_LANGUAGE.setName(language.check("en_UK", "English"));

			for (String key : language.getKeys()) {
				Language lang = new Language(key);
				String name = language.check(key, key);
				lang.setName(name);
				languages.add(lang);
			}

			language.save(languageFile);

			//
			// Notify that translations have been loaded

			Bukkit.getPluginManager().callEvent(new TimeCycleTranslationEvent(this, fallback));

			return this;
		}

		/*
		 * 
		 */

		public TranslationManager saveCatch() {
			try {
				return save();
			} catch (IOException e) {
				TimeCycle.getPluginLogger().log(e);
			}
			return this;
		}

		public TranslationManager save() throws IOException {

			//
			// Save fallback

			fallback.save();

			//
			// Save language names

			language.save(languageFile);

			//
			// Save other languages

			if (!translations.isEmpty()) {
				for (TranslationStorage translation : translations) {
					translation.save();
				}
			}

			return this;
		}

		/*
		 * 
		 */

		public Language[] getLanguages() {
			return languages.toArray(new Language[0]);
		}

		public Language getLanguage(String value) {
			Optional<Language> language;
			for (RequestType type : RequestType.values()) {
				if ((language = getOption(value, type)).isPresent()) {
					return language.get();
				}
			}
			return LanguageProvider.DEFAULT_LANGUAGE;
		}

		public Language getLanguage(String value, RequestType type) {
			return getOption(value, type).orElse(LanguageProvider.DEFAULT_LANGUAGE);
		}

		private final Optional<Language> getOption(String value, RequestType type) {
			Predicate<Language> predicate = null;
			switch (type) {
			case ID: {
				predicate = language -> language.getId().equals(value);
				break;
			}
			case NAME: {
				predicate = language -> language.getName().equals(value);
				break;
			}
			default:
				return Optional.empty();
			}
			return languages.stream().filter(predicate).findAny();
		}

		/*
		 * 
		 */

		public boolean hasTranslation(String language) {
			return translations.stream().anyMatch(storage -> storage.getLanguage().equals(language)) || fallback.getLanguage().equals(language);
		}

		public TranslationStorage getTranslation(String language) {
			return translations.stream().filter(storage -> storage.getLanguage().equals(language)).findFirst().orElse(fallback);
		}

		public TranslationStorage getFallbackTranslation() {
			return fallback;
		}

		/*
		 * 
		 */

		public String getMessage(String language, String id) {
			return get(language, id, TranslationType.MESSAGE);
		}

		public String getVariable(String language, String id) {
			return get(language, id, TranslationType.VARIABLE);
		}

		public String get(String language, String id, TranslationType type) {
			return getTranslation(language).get(id, type);
		}

		/*
		 * 
		 * 
		 * 
		 */

		public static final class TranslationStorage {

			private final YamlConfig variable = new YamlConfig();
			private final YamlConfig message = new YamlConfig();

			private final String language;
			private final File folder;

			private File messageFile;
			private File variableFile;

			private TranslationStorage(String language, File folder) {
				this.language = language;
				this.folder = new File(folder, language);
			}

			/*
			 * 
			 */

			public String getLanguage() {
				return language;
			}

			public File getFolder() {
				return folder;
			}

			/*
			 * 
			 */

			public TranslationStorage reload() throws IOException {
				return reload(true);
			}

			public TranslationStorage reload(boolean clear) throws IOException {
				if (!folder.exists()) {
					return save();
				}

				File[] files = folder.listFiles();

				if (files.length != 0) {
					for (File file : files) {
						String name = file.getName();
						if (!(name.endsWith("yaml") || name.endsWith("yml")))
							continue;
						if (name.startsWith("variables")) {
							if (clear) {
								variable.clear();
							}
							variable.load(file);
							variableFile = file;
							continue;
						}
						if (name.startsWith("messages")) {
							if (clear) {
								message.clear();
							}
							message.load(file);
							messageFile = file;
						}
					}
				}

				return save();
			}

			/*
			 * 
			 */

			public TranslationStorage save() throws IOException {

				if (variableFile == null)
					variableFile = new File(folder, "variables.yml");
				if (messageFile == null)
					messageFile = new File(folder, "messages.yml");

				variable.save(variableFile);
				message.save(messageFile);

				return this;

			}

			/*
			 * 
			 */

			public String get(String id, TranslationType type) {
				Object value = read(id, type);
				if (value == null)
					return LanguageProvider.NULL_VALUE;
				if (value instanceof String)
					return (String) value;
				return value.toString();
			}

			public void set(String id, TranslationType type, String value) {
				write(id, type, value);
			}

			public String check(String id, TranslationType type, String fallback) {
				String value;
				if (!(value = get(id, type)).equals(LanguageProvider.NULL_VALUE))
					return value;
				set(id, type, fallback);
				return fallback;
			}

			/*
			 * 
			 */

			private final Object read(String id, TranslationType type) {
				switch (type) {
				case MESSAGE:
					return message.get(id);
				case VARIABLE:
					return variable.get(id);
				default:
					return null;
				}
			}

			private final void write(String id, TranslationType type, Object value) {
				switch (type) {
				case MESSAGE: {
					message.set(id, value);
					return;
				}
				case VARIABLE: {
					variable.set(id, value);
					return;
				}
				default:
				}
			}

			/*
			 * 
			 */

			public TranslationStorage clear() {
				variable.clear();
				message.clear();
				return this;
			}

		}

	}

}
