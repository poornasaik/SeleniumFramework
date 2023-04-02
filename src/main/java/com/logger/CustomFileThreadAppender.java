package com.logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

@Plugin(name = "CustomFileThreadAppender", category = "Core", elementType = "appender", printObject = true)
public class CustomFileThreadAppender extends AbstractAppender {
	private static final Map<Long, FileAppender> appenders = new ConcurrentHashMap<>();
	private final Set<File> threadFiles = new TreeSet<>();

	public CustomFileThreadAppender(final String name, final Filter filter, final Layout<? extends Serializable> layout,
			final boolean ignoreExceptions, final Property[] properties) {
		super(name, filter, layout, ignoreExceptions, properties);
		addShutdownHook();
	}

	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			stop();
		}));
	}

	@Override
	public void start() {
		super.start();
	}

	@Override
	public synchronized void append(LogEvent event) {
		long threadId = Thread.currentThread().getId();
		FileAppender appender = appenders.get(threadId);

		if (appender == null) {
			appender = FileAppender.newBuilder()
					.withFileName("logs_temp/thread_" + String.format("%04d", threadId) + ".log").withAppend(true)
					.setName("ThreadLogger-" + threadId).setLayout(getLayout()).setFilter(getFilter()).build();
			appender.start();
			File file = new File(appender.getFileName());
			threadFiles.add(file);
			appenders.put(threadId, appender);
		}
		appender.append(event);
	}

	@PluginFactory
	public static CustomFileThreadAppender createAppender(
			@PluginAttribute("name") @Required(message = "No name provided for thread Appender") final String name,
			@PluginAttribute("entryPerNewLine") final boolean newLine, @PluginAttribute("raw") final boolean raw,
			@PluginElement("Layout") final Layout<? extends Serializable> layout,
			@PluginElement("Filter") final Filter filter) {
		return new CustomFileThreadAppender(name, filter, layout, newLine, null);
	}

	@Override
	public void stop() {

		for (FileAppender appender : appenders.values()) {
			appender.stop();
		}
		super.stop();
		try {
			mergeFilesAndDelete();
		} catch (IOException e) {
			LogManager.getLogger().error("Error while merging log files: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	private void mergeFilesAndDelete() throws IOException {
		try (PrintWriter writer = new PrintWriter(new FileWriter(new File("logs", "Consolidated.log"), true))) {
			writer.println("************************** Logger Started **************************");
			for (File threadFile : threadFiles) {
				writer.println("========================== Log start ==========================");
				try (BufferedReader reader = new BufferedReader(new FileReader(threadFile))) {
					String line;
					while ((line = reader.readLine()) != null) {
						writer.println(line);
					}
				}
				writer.println("=========================== Log End ===========================");

			}
			writer.println("************************** Logger ended ****************************\n");

			for (File threadFile : threadFiles) {
				Files.deleteIfExists(threadFile.toPath());
			}
			File file = new File("logs_temp");
			if (!file.delete()) {
				LogManager.getLogger().error("Failed to delete thread log files directory {}", file.getName());
			}

		}
	}
}