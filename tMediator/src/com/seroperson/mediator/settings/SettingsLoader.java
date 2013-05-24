package com.seroperson.mediator.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class SettingsLoader {

	public static Settings getSettings() {
		final File settingsfile = getSettingsFile();
		try {
			return readSettingsFromFile(settingsfile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static File getSettingsFile() {
		final FileHandle directory = Gdx.files.external("tmediator");
		if(!directory.exists()) {
			directory.mkdirs();
		}
		final FileHandle settings = Gdx.files.external("tmediator/settings.dat");
		final File settingsfile = settings.file();
		if(!settings.exists()) {
			try {
				settingsfile.createNewFile();
				writeSettingsToFile(Settings.getDefaultSettings(), settingsfile);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return settingsfile;
	}

	public static Settings readSettingsFromFile(final File file) throws IOException {
		final BufferedReader br = new BufferedReader(new FileReader(file));
		final StringBuilder str = new StringBuilder();
		while(true) {
			final String string = br.readLine();
			if(string == null)
				break;
			str.append(string);
		}
		br.close();
		return new Json().fromJson(Settings.class, str.toString());
	}

	public static void writeSettingsToFile(final Settings settings, final File file) throws IOException {
		FileWriter w = null;
		if(file.canWrite()) {
			final Json json = new Json();
			w = new FileWriter(file);
			w.write(json.toJson(settings));
		}
		if(w != null) {
			w.flush();
			w.close();
		}
	}

}