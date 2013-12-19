package com.seroperson.mediator.screen;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.tori.stuff.Server;
import com.seroperson.mediator.viewer.ServerViewer;
import com.seroperson.mediator.viewer.ServerViewerContainer;

public class ServerInputListener extends InputListener {

	private final Runnable runnable;
	private final Player player;
	private long counter;
	private int click = 0;

	public ServerInputListener(final ServerViewerContainer container, final Player p) {
		player = p;
		runnable = new Runnable() {
			@Override
			public void run() {
				ServerViewer viewer = container.getServerViewer();
				if(viewer == null)
					container.setServerViewer(viewer = new ServerViewer(container)); // TODO remove
				final Server s = Mediator.getServerByRoom(player.getServer().getRoom(), Mediator.getServers());
				if(s != null)
					viewer.add(s, s.getRoom(), true);
			}
		};
	}

	@Override
	public boolean touchDown (final InputEvent event, final float x, final float y, final int pointer, final int button) {
		if(button == Buttons.MIDDLE)
			runnable.run();
		else
			if(button == Buttons.LEFT) {
				if(System.currentTimeMillis() - counter > 1000) {
					counter = 0;
					click = 0;
					counter = System.currentTimeMillis();
				}
				else
					click++;
				if(click >= 1) {
					runnable.run();
					click = 0;
				}
			}
		return false;
	}

}
