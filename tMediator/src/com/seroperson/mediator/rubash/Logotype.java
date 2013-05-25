package com.seroperson.mediator.rubash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.screen.OnlineList;
import com.seroperson.mediator.screen.ScreenAdapter;
import com.seroperson.mediator.settings.Settings;
import com.seroperson.mediator.threads.Refresher;

public class Logotype extends ScreenAdapter {

	private State state = State.In;
	private final TextureRegion textureregion;
	private final SpriteBatch batch = new SpriteBatch();
	private final Color color = new Color(batch.getColor());
	private final float scale;
	private final float inSpeed = 0.0002f;
	private final float outSpeed = 0.002f;
	private float waitTime = 0.5f;
	private final Vector2 position = new Vector2();
	private final Mediator mediator;
	private final Settings settings;

	public Logotype(final Settings settings, final Mediator mediator, final Vector2 position, final float scale) {
		textureregion = new TextureRegion(new Texture(Gdx.files.internal("skin/logotype.png")), 0, 0, 256, 120);
		color.a = 0;
		batch.setColor(color);
		this.settings = settings;
		this.scale = scale;
		this.mediator = mediator;
		this.position.set(position).sub(textureregion.getRegionWidth()/scale/2, textureregion.getRegionHeight()/scale/2);
	}

	@Override
	public void render(final float delta) {
		batch.begin();
		Gdx.gl10.glEnable(GL10.GL_BLEND);
		if(state.equals(State.In)) {
			color.a += inSpeed;
			if(color.a >= 1) {
				color.a = 1;
				state = State.Wait;
			}
		}
		if(state.equals(State.Wait)) {
			waitTime -= delta;
			if(waitTime <= 0) {
				waitTime = 0;
				state = State.Out;
			}
		}
		if(state.equals(State.Out)) {
			color.a -= outSpeed;
			if(color.a <= 0) {
				color.a = 0;

				final OnlineList list = new OnlineList(mediator);
				mediator.setScreen(list);
				new Refresher(settings, list).start();

				dispose();
				return;
			}
		}
		batch.setColor(color.r, color.g, color.b, color.a);
		batch.draw(textureregion, position.x, position.y, 0, 0, textureregion.getRegionWidth(), textureregion.getRegionHeight(), 1/scale, 1/scale, 0);
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	private enum State {

		In(), Wait(), Out();

	}

}
