package com.seroperson.mediator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.seroperson.mediator.rubash.Logotype;
import com.seroperson.mediator.settings.Settings;
import com.seroperson.mediator.settings.SettingsLoader;

public class Mediator extends Game implements CasesListener {

	private static ObjectMap<String, TextureRegion> buttons = new ObjectMap<String, TextureRegion>();
	private static Settings settings;
	private static Texture skinTexture;
	private final CasesListener ml;
	private ShapeRenderer renderer;
	private final float scale;
	private final int slw;

	public Mediator(final CasesListener ml, final float scale, final int strokeLineWidth) {
		this.scale = scale;
		this.ml = ml;
		slw = strokeLineWidth;
	}

	@Override
	public void create() {
		if(Gdx.files.isExternalStorageAvailable())
			settings = SettingsLoader.getSettings();

		skinTexture = new Texture(Gdx.files.internal("skin/skin.png"));
		buttons.put("minimize", new TextureRegion(Mediator.getSkinTexture(), 0, 0, 15, 15));
		buttons.put("close", new TextureRegion(Mediator.getSkinTexture(), 17, 0, 15, 15));
		buttons.put("back", new TextureRegion(Mediator.getSkinTexture(), 34, 0, 15, 15));

		renderer = new ShapeRenderer();
		renderer.setColor(0, 0, 0, 1);
		setScreen(new Logotype(settings, this, new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2), scale));
	}

	@Override
	public void render() {
		Gdx.gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl10.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl10.glLineWidth(slw);
		renderer.begin(ShapeType.Line);
		renderer.rect(slw/2, -slw, Gdx.graphics.getWidth()-slw, Gdx.graphics.getHeight()+slw/2);
		renderer.end();
		super.render();
	}

	public synchronized static Settings getSettings() {
		return settings;
	}

	public static void setSettings(final Settings settings) {
		Mediator.settings = settings;
	}

	public static Texture getSkinTexture() {
		return skinTexture;
	}

	public static TextureRegion getRegion(final String name) {
		return buttons.get(name);
	}

	@Override
	public void dispose() {
		getScreen().dispose();
	}


	@Override
	public void minimize(final Mediator mediator) {
		ml.minimize(mediator);
	}


	@Override
	public void handleThrow(final Throwable t) {
		ml.handleThrow(t);
	}

}
