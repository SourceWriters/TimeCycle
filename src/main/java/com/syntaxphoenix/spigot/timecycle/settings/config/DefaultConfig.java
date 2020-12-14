package com.syntaxphoenix.spigot.timecycle.settings.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.bukkit.plugin.Plugin;

import com.syntaxphoenix.spigot.timecycle.TimeCycle;
import com.syntaxphoenix.spigot.timecycle.helper.PrefixHelper;
import com.syntaxphoenix.spigot.timecycle.language.IMessage;
import com.syntaxphoenix.spigot.timecycle.language.IVariable;
import com.syntaxphoenix.spigot.timecycle.language.LanguageProvider;
import com.syntaxphoenix.spigot.timecycle.language.Message;
import com.syntaxphoenix.spigot.timecycle.language.Translations;
import com.syntaxphoenix.spigot.timecycle.language.Variable;
import com.syntaxphoenix.spigot.timecycle.settings.Config;
import com.syntaxphoenix.spigot.timecycle.settings.migration.DefaultConfigMigration;
import com.syntaxphoenix.syntaxapi.random.NumberGeneratorType;
import com.syntaxphoenix.syntaxapi.random.RandomNumberGenerator;

public class DefaultConfig extends Config {

	public static final DefaultConfig CONFIG = new DefaultConfig();

	public static String LANGUAGE = "default";
	public static String PREFIX = "&3TimeCycle";
	public static boolean GRADIENT = true;
	public static String GRADIENT_COLOR_0 = "#07C7F7";
	public static String GRADIENT_COLOR_1 = "#19DEA6";

	public static int PERCENTAGE = 40;
	public static float SPEED_MODIFIER = 5f;
	public static boolean INSTANT_SKIP = false;

	private static RandomNumberGenerator RANDOM = NumberGeneratorType.MURMUR.create(System.currentTimeMillis());
	private static ArrayList<IdMessage> MESSAGES = new ArrayList<>();
	private static ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();

	private static ReadLock READ = LOCK.readLock();
	private static WriteLock WRITE = LOCK.writeLock();

	private DefaultConfig() {
		super(new File("plugins//TimeCycle//config.yml"), DefaultConfigMigration.class, 1);
	}

	@Override
	protected IVariable getName() {
		return Variable.CONFIG_TYPE_DEFAULT;
	}

	@Override
	protected void onLoad() {
		LANGUAGE = check("language", LANGUAGE);

		LanguageProvider.setConsoleLanguage(Translations.MANAGER.getLanguage(LANGUAGE));

		PREFIX = check("prefix.name", PREFIX);
		GRADIENT = check("prefix.gradient.enabled", GRADIENT);
		GRADIENT_COLOR_0 = check("prefix.gradient.color-0", GRADIENT_COLOR_0);
		GRADIENT_COLOR_1 = check("prefix.gradient.color-1", GRADIENT_COLOR_1);

		PrefixHelper.clear();
		PrefixHelper.getPrefix();

		PERCENTAGE = check("sleep.percentage", (Number) PERCENTAGE).intValue();
		SPEED_MODIFIER = check("sleep.speed", (Number) SPEED_MODIFIER).floatValue();
		INSTANT_SKIP = check("sleep.instant", INSTANT_SKIP);

		RANDOM.setSeed(check("message.seed", (Number) RANDOM.getCompressedState()).longValue());
		List<String> temp = config.getStringList("message.list");
		if (temp != null) {
			readMessages(temp);
		}
		READ.lock();
		config.set("message.list", writeMessages());
		READ.unlock();
	}

	private void readMessages(List<String> messages) {
		WRITE.lock();
		MESSAGES.clear();
		for (String message : messages) {
			MESSAGES.add(new IdMessage(message));
		}
		WRITE.unlock();
	}

	private List<String> writeMessages() {
		ArrayList<String> list = new ArrayList<>();
		READ.lock();
		for (IdMessage message : MESSAGES) {
			list.add(message.originalId());
		}
		READ.unlock();
		return list;
	}

	public static IMessage getMessage() {
		READ.lock();
		int size = MESSAGES.size();
		READ.unlock();
		if (size == 0) {
			return Message.SLEEP_GOOD222MORNING_DEFAULT;
		}
		READ.lock();
		IMessage message = MESSAGES.get(RANDOM.nextInt(size));
		READ.unlock();
		return message;
	}

	private class IdMessage implements IMessage {

		private final String id;
		private final String original;

		public IdMessage(String id) {
			id = id.replace('.', '_').toUpperCase();
			this.original = id.replace('_', '.').toLowerCase();
			this.id = "SLEEP_GOOD222MORNING_" + id;
		}

		@Override
		public Plugin getOwner() {
			return TimeCycle.getPlugin();
		}

		@Override
		public String id() {
			return id;
		}

		public String originalId() {
			return original;
		}

		@Override
		public String value() {
			return original;
		}

	}

}
