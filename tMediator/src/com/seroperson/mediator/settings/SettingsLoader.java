package com.seroperson.mediator.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.badlogic.gdx.utils.Json;
import com.seroperson.mediator.utils.ThrowHandler;

public class SettingsLoader {

	public static Settings getSettings(final ThrowHandler th) {
		try {
			final File settingsfile = getSettingsFile();
			return readSettingsFromFile(settingsfile);
		}
		catch (final Throwable e) {
			th.handleThrow(e);
		}
		return null;
	}

	public static File getSettingsFile() throws IOException {
		final String home = System.getProperty("user.home", "");
		final String tmediator = new StringBuilder(home).append("/tmediator").toString();
		final File directory = new File(tmediator);
		if(!directory.exists()) {
			directory.mkdirs();
		}
		final File settings = new File(new StringBuilder(tmediator).append("/settings.dat").toString());
		if(!settings.exists()) {
			settings.createNewFile();
			writeSettingsToFile(Settings.getDefaultSettings(), settings);
		}
		return settings;
	}

	public static Settings readSettingsFromFile(final File file) throws IOException {
		final BufferedReader br = new BufferedReader(new FileReader(file));
		final StringBuilder str = new StringBuilder();
		try {
			while(true) {
				final String string = br.readLine();
				if(string == null)
					break;
				str.append(string);
			}
		}
		finally {
			br.close();
		}
		return new Json().fromJson(Settings.class, str.toString());
	}

	public static void writeSettingsToFile(final Settings settings, final File file) throws IOException {
		FileWriter w = null;
		try {
			if(file.canWrite()) {
				final Json json = new Json();
				w = new FileWriter(file);
				w.write(json.prettyPrint(settings));
			}
		}
		finally {
			if(w != null) {
				w.flush();
				w.close();
			}
		}
	}

}
