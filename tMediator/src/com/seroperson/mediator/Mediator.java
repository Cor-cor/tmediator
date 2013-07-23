package com.seroperson.mediator;

import java.util.Arrays;
import java.util.Timer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.seroperson.mediator.rubash.Logotype;
import com.seroperson.mediator.screen.OnlineList;
import com.seroperson.mediator.settings.Settings;
import com.seroperson.mediator.settings.SettingsLoader;
import com.seroperson.mediator.tori.stuff.Global;
import com.seroperson.mediator.tori.stuff.Server;
import com.seroperson.mediator.utils.CaseListener;
import com.seroperson.mediator.utils.ThrowHandler;

public class Mediator extends Game implements CaseListener, ThrowHandler {

	// TODO (!) rewrite without libgdx

	private static final String version = "0.11-beta";
	private static final Interpolation interpolation = Interpolation.circle;
	private static final ObjectMap<String, TextureRegion> buttons = new ObjectMap<String, TextureRegion>();
	private static boolean minimized = false;
	private static boolean debug;
	private static Texture skinTexture;
	private static Settings settings;
	private final Timer timer;
	private Server[] servers;
	private Global[] globals = new Global[5];

	public Mediator() {
		this(false);
	}

	private Mediator(final boolean debug) {
		Mediator.debug = debug;
		timer = new Timer("Timer", true);
	}

	@Override
	public void create() {

		settings = SettingsLoader.getSettings(this);

		skinTexture = new Texture(Gdx.files.internal("skin/skin.png"));
		buttons.put("minimize", new TextureRegion(Mediator.getSkinTexture(), 0, 0, 15, 15));
		buttons.put("close", new TextureRegion(Mediator.getSkinTexture(), 17, 0, 15, 15));
		buttons.put("back", new TextureRegion(Mediator.getSkinTexture(), 34, 0, 15, 15));

		Gdx.graphics.setVSync(false);

		setScreen(new Logotype(this, new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2), 2));
	}

	@Override
	public void render() {
		Gdx.gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl10.glClearColor(.9f, .9f, .9f, 1f);
		super.render();
	}

	public synchronized static Settings getSettings() {
		return settings;
	}

	public synchronized static void setSettings(final Settings s) {
		settings = s;
	}

	public static Texture getSkinTexture() {
		return skinTexture;
	}

	public static TextureRegion getRegion(final String name) {
		return buttons.get(name);
	}

	public static boolean isDebug() {
		return debug;
	}

	public static synchronized boolean isMinimized() {
		return minimized;
	}

	public static Server getServerByRoom(final String room, final Server[] servers) {
		for(final Server s : servers)
			if(room.equalsIgnoreCase(s.getRoom()))
				return s;
		return null;
	}

	public static Interpolation getInterpolation() { 
		return interpolation;
	}
	
	public static String getVersion() { 
		return version;
	}
	
	public Global[] getGlobals() {
		return globals;
	}

	public void addGlobal(final Global g) {
		final int index = getLastGlobalIndex();
		if(index == globals.length - 1) 
			globals = Arrays.copyOf(globals, globals.length+5);
		globals[index + 1] = g;
		handleGlobal(g);
	}

	public int getLastGlobalIndex() {
		int index = 0;
		for(final Global g : globals) {
			if(g == null)
				return index - 1;
			index++;
		}
		return globals.length - 1;
	}

	public Global getLastGlobal() {
		final int index = getLastGlobalIndex();
		if(index < 0)
			return null;
		return globals[index];
	}

	public Server[] getServers() {
		return servers;
	}

	public void setServers(final Server[] servers) {
		this.servers = servers;
	}

	public Timer getTimer() {
		return timer;
	}

	@Override
	public void dispose() {
		getScreen().dispose();
	}

	@Override
	public void unMinimize() {
		minimized = false;
	}

	@Override
	public void minimize() {
		minimized = true;
		
		if(getScreen().getClass() != OnlineList.class)
			return;
		
		OnlineList scr = ((OnlineList)getScreen());
		scr.setAnimation(false);	
		scr.render(Integer.MAX_VALUE); // fast action clearing
		//TODO refactoring?
	}

	@Override
	public void handleThrow(final Throwable t) {
	}

	@Override
	public void handleGlobal(final Global g) {
	}
	
	@Override
	public void handleNetThrow(final Throwable t) { 
	}

}
