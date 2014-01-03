package com.seroperson.mediator;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.seroperson.mediator.parsing.Parser;
import com.seroperson.mediator.refresh.ServerHandler;
import com.seroperson.mediator.screen.OnlineList;
import com.seroperson.mediator.tori.stuff.Global;
import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.tori.stuff.Server;

public class Debugger extends OnlineList {

	private final String[] names = new String[] { "Albert", "Nick", "Michael", "Jordan", "Robert", "John", "Huenos", "Eric", "Walter",
			"Nilson", "Barny", "Jennifer", "Julia", "Ann", "Cameron", "Thomas", "James", "Nicholas",
			"Lee", "Jackson", "Sam", "Garry", "Jared", "Ken", "Scott", "Ross", "Paul" };
	private final String[] clans = new String[] { "Strangers", "Invaders", "Punishers", "GoodGuys" };
	private final String[] rooms = new String[] { "Tourney", "ClanWar", "MyAwesomeRoom", "Bar", "Club" };
	private final Random randomizer = new Random();
	private DebugRefresher handler;
	
	public Debugger(Mediator game) {
		super(game);
	}

	@Override
	public ServerHandler initServerHandler() {
		handler = new DebugRefresher(this, game);
		return handler;
	}
	
	private Player getRandomPlayer() {
		final String clan = randomizer.nextBoolean() ? clans[randomizer.nextInt(clans.length)] : Parser.CNONE;
		final String name = names[randomizer.nextInt(names.length)];
		final String room = rooms[randomizer.nextInt(rooms.length)];
		final Player newplayer = new Player(name, clan, new Server("", room, "", null, null, 0), new char[] { '[', ']' });
		
		if(handler.getQueuePlayers().contains(newplayer))
			return getRandomPlayer();
		
		return newplayer;
	}

	public void remove() {
		ArrayList<Player> players = handler.getQueuePlayers();
		if(players.size() > 0) {
			int size = players.size()-1;
			if(size == 0)
				size++;
			players.remove(randomizer.nextInt(size));
		}
	}

	@Override
	public boolean keyDown (final int keycode) {
		ArrayList<Player> players = handler.getQueuePlayers();
		
		final Player nextrandom = getRandomPlayer();
		
		if(keycode > 243 && keycode < 255)
			for(int i = 243; i < keycode; i++) {
				Player player = getRandomPlayer();
				players.add(player);
				System.out.println(player.getName() +" generatred");
			}

		try {
			switch(keycode) {
				case Keys.A: // add
					players.add(nextrandom);
					break;
				case Keys.C: // combo
					remove();
					players.add(nextrandom);
					break;
				case Keys.R: // remove
					remove();
					break;
				case Keys.E: // error
					throw new Exception("ERROR");
				case Keys.N:
					throw new Exception("NET_ERROR");
				case Keys.D: // double
					remove();
					remove();
					players.add(nextrandom);
					break;
				case Keys.G: // global
					handler.getQueueGlobals().add(new Global("Global-test", System.currentTimeMillis(), nextrandom.getServer().getRoom(), nextrandom.getName()));
					break;
				case Keys.ESCAPE:
					Gdx.app.exit();
					break;
			}
		}
		catch(Exception e) { 
			if(e.getMessage().equals("ERROR"))
					game.handleThrow(e);
			if(e.getMessage().equals("NET_ERROR"))
					game.handleNetThrow(e);
		}
		
		handler.run();
		
		return false;
	}

}
