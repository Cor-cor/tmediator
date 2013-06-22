package com.seroperson.mediator.screen;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.tori.stuff.Server;
import com.seroperson.mediator.viewer.ServerViewer;

public class ServerInputListener extends InputListener {

	private final Runnable runnable;
	private final OnlineList list;
	private final Player player;
	private long counter;
	private int click = 0;
	
	public ServerInputListener(OnlineList lst, Player p) { 
		this.player = p;
		this.list = lst;
		this.runnable = new Runnable() {
			@Override
			public void run() {
				if(list.getServerViewer() == null)
					list.setServerViewer(new ServerViewer(list));
				Server s = Mediator.getServerByRoom(player.getServer().getRoom(), list.getServers());
				list.getServerViewer().add(s, s.getRoom(), true);
			}
		};
	}
	
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		if(button == Buttons.MIDDLE)
			runnable.run();
		else if(button == Buttons.LEFT) { 
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