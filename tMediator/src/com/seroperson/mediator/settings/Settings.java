package com.seroperson.mediator.settings;

public class Settings {

	private final static Settings defaultsettings;

	static {
		final SettingsDef def = new SettingsDef();
		def.period = 20000;
		def.port = 22000;
		def.server = "176.9.64.22";
		def.uri = "http://forum.toribash.com/forumdisplay.php?f=35";
		def.names = new String[] { "hampa", "nirs", "korvin", "slipanc" };
		def.clans = new String[] { "Aliens", "Impro", "Imba", "Abyss" };
		def.globals = true;
		def.unminimizeonnewplayer = true;
		defaultsettings = new Settings(def);
	}

	private final int period;
	private final int port;
	private final String server;
	private final String forum_uri;

	private final String[] names;
	private final String[] clans;

	private final float padLeft;
	private final float padTop;
	private final float padBottom;

	private final Boolean globals;
	private final Boolean unminimizeonnewplayer;

	public Settings() {
		period = 0;
		port = 0;
		forum_uri = null;
		server = null;
		names = null;
		clans = null;
		globals = null;
		unminimizeonnewplayer = null;
		padLeft = 0;
		padTop = 0;
		padBottom = 0;
	}

	public Settings(final SettingsDef def) {
		period = def.period > 30000 ? def.period : 30000;
		port = def.port;
		server = def.server;
		names = def.names;
		clans = def.clans;
		padLeft = def.padLeft;
		padTop = def.padTop;
		padBottom = def.padBottom;
		globals = def.globals;
		forum_uri = def.uri;
		unminimizeonnewplayer = def.unminimizeonnewplayer;
	}

	public int getPeriod() {
		return period;
	}

	public int getPort() {
		return port;
	}

	public String getServer() {
		return server;
	}

	public String getForumURI() {
		return forum_uri;
	}

	public String[] getNames() {
		return names;
	}

	public String[] getClans() {
		return clans;
	}

	public boolean isGlobalsTracking() {
		return globals;
	}

	public boolean isMinimizeAction() { 
		return unminimizeonnewplayer;
	}
	
	public float getPadLeft() {
		return padLeft;
	}

	public float getPadTop() {
		return padTop;
	}

	public float getPadBottom() {
		return padBottom;
	}

	public static Settings getDefaultSettings() {
		return defaultsettings;
	}

}
