package com.seroperson.mediator.settings;

import com.badlogic.gdx.math.Vector2;

public class Settings {

	private final static Settings defaultsettings;

	static {
		final SettingsDef def = new SettingsDef();
		def.rooms = new String[1];
		def.names = new String[] { "hampa", "nirs", "korvin", "slipanc" };
		def.clans = new String[] { "Aliens", "Impro", "Imba", "Abyss" };
		defaultsettings = new Settings(def);
	}

	private final int period;
	private final int port;
	private final int sort;
	private final String server;
	private final String forum_uri;

	private final String[] names;
	private final String[] clans;
	private final String[] rooms;

	private final float padLeft;
	private final float padBottom;
	
	private final Boolean showlogo;
	private final Boolean globals;
	private final Boolean unminimizeonnewplayer;
	private final Float[] round;
	
	private final Vector2 position;
	
	public Settings() {
		period = defaultsettings.period;
		port = defaultsettings.port;
		sort = defaultsettings.sort;
		forum_uri = defaultsettings.forum_uri;
		server = defaultsettings.server;
		names = defaultsettings.names;
		clans = defaultsettings.clans;
		rooms = defaultsettings.rooms;
		globals = defaultsettings.globals;
		unminimizeonnewplayer = defaultsettings.unminimizeonnewplayer;
		showlogo = defaultsettings.showlogo;
		padLeft = defaultsettings.padLeft;
		padBottom = defaultsettings.padBottom;
		round = defaultsettings.round;
		position = new Vector2(defaultsettings.position);
	}

	public Settings(final SettingsDef def) {
		period = def.period > 30000 ? def.period : 30000;
		port = def.port;
		sort = def.sort;
		server = def.server == null ? defaultsettings.server : def.server;
		names = def.names == null ? defaultsettings.names : def.names;
		clans = def.clans  == null ? defaultsettings.clans : def.clans;
		rooms = def.rooms == null ? defaultsettings.rooms : def.rooms;
		padLeft = def.padLeft;
		padBottom = def.padBottom;
		globals = def.globals == null ? defaultsettings.globals : def.globals;
		forum_uri = def.uri == null ? defaultsettings.forum_uri : def.uri;
		showlogo = def.showlogo == null ? defaultsettings.showlogo : def.showlogo;
		unminimizeonnewplayer = def.unminimizeonnewplayer == null ? defaultsettings.unminimizeonnewplayer : def.unminimizeonnewplayer;
		round = def.round == null ? defaultsettings.round : def.round;
		position = new Vector2(def.position == null ? defaultsettings.position : def.position);
	}

	public int getPeriod() {
		return period;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getSortingType() {
		return sort;
	}

	public String getServer() {
		return server;
	}

	public String getForumURI() {
		return forum_uri;
	}

	public String[] getRooms() { 
		return rooms;
	}
	
	public String[] getNames() {
		return names;
	}

	public String[] getClans() {
		return clans;
	}

	public Float[] getShapeSettings() {
		return round;
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	public boolean isShowingLogotype() {
		return showlogo;
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

	public float getPadBottom() {
		return padBottom;
	}

	public static Settings getDefaultSettings() {
		return defaultsettings;
	}

}
