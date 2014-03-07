package com.seroperson.mediator;

import java.util.Arrays;
import java.util.Timer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.seroperson.mediator.rubash.Logotype;
import com.seroperson.mediator.screen.MainScreen;
import com.seroperson.mediator.screen.OnlineList;
import com.seroperson.mediator.settings.Settings;
import com.seroperson.mediator.settings.SettingsLoader;
import com.seroperson.mediator.tori.stuff.Global;
import com.seroperson.mediator.tori.stuff.Server;
import com.seroperson.mediator.utils.CaseHandler;
import com.seroperson.mediator.utils.ThrowHandler;

public class Mediator extends Game implements CaseHandler, ThrowHandler {

	// TODO (!) rewrite without libgdx

	public static InitializationListener defaultInitialization = new InitializationListener() {
		public Screen initialScreen(Mediator mediator) { 
			return mediator.settings.isShowingLogotype() ? new Logotype(mediator, new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2), 2) : new OnlineList();
		} 
	};
	public static InitializationListener debugInitialization = new InitializationListener() {
		public Screen initialScreen(Mediator mediator) {
			return new Debugger();
		} 
	};
	private boolean minimized = false;
	private boolean crashed;
	private Server[] servers;
	private Texture skinTexture;
	private Settings settings;
	private Global[] globals = new Global[5];
	private Skin skin;
	private final Timer timer;
	private final Interpolation interpolation = Interpolation.circle;
	private final ObjectMap<String, TextureRegion> buttons = new ObjectMap<String, TextureRegion>();
	private final InitializationListener initialization;
	private static Mediator mediator = new Mediator(defaultInitialization);
	
	protected Mediator(InitializationListener initialization) {
		timer = new Timer("Timer", true);
		settings = SettingsLoader.getSettings(this);
		this.initialization = initialization;
	}

	@Override
	public void create() {
		skin = new Skin(Gdx.files.internal("skin/skin.json")); 
		skinTexture = new Texture(Gdx.files.internal("skin/skin.png"));
		buttons.put("minimize", new TextureRegion(skinTexture, 0, 0, 15, 15));
		buttons.put("close", new TextureRegion(skinTexture, 17, 0, 15, 15));
		buttons.put("back", new TextureRegion(skinTexture, 34, 0, 15, 15));
		setScreen(initialization.initialScreen(this));
	}

	@Override
	public void setScreen(Screen screen) { 
		if(screen instanceof MainScreen) { 
			MainScreen cast = (MainScreen) screen;
			Gdx.input.setInputProcessor(cast.getInputProcessor());
			cast.initServerHandler();
		}
		super.setScreen(screen);
	} 
	
	@Override
	public void render() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(.9f, .9f, .9f, 1f);
		super.render();
	}

	public synchronized Settings getSettings() {
		return settings;
	}

	public synchronized void setSettings(final Settings s) {
		settings = s;
	}

	public Texture getSkinTexture() {
		return skinTexture;
	}

	public TextureRegion getRegion(final String name) {
		return buttons.get(name);
	}

	public synchronized boolean isMinimized() {
		return minimized;
	}

	public Server getServerByRoom(final String room) {
		for(final Server s : servers)
			if(room.equalsIgnoreCase(s.getRoom()))
				return s;
		return null;
	}

	public Interpolation getInterpolation() { 
		return interpolation;
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

	public Skin getSkin() { 
		return skin;
	}
	
	public static Mediator getMediator() { 
		return mediator;
	}
	
	public static void setMediator(Mediator mediator) { 
		Mediator.mediator = mediator;
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
		crashed = true;
	}

	@Override
	public void handleGlobal(final Global g) {
	}
	
	@Override
	public void handleNetThrow(final Throwable t) { 
	}

	public boolean isCrashed() {
		return crashed;
	}
	
}
