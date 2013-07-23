package com.seroperson.mediator;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.seroperson.mediator.screen.OnlineList;
import com.seroperson.mediator.tori.stuff.Global;
import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.tori.stuff.Server;

/* something like a "debugger" ._.
 * TODO */
public class Debugger extends InputAdapter {

	private final String[] names = new String[] { "Albert", "Nick", "Michael", "Jordan", "Robert", "John", "Huenos", "Eric", "Walter",
			"Nilson", "Barny", "Jennifer", "Julia", "Ann", "Cameron", "Thomas", "James", "Nicholas",
			"Lee", "Jackson", "Sam", "Garry", "Jared", "Ken", "Scott", "Ross", "Paul" };
	private final String[] clans = new String[] { "Strangers", "Invaders", "Punishers", "GoodGuys" };
	private final String[] rooms = new String[] { "Tourney", "ClanWar", "MyAwesomeRoom", "Bar", "Club" };
	private final List<Player> players = new ArrayList<Player>();
	private final Random randomizer = new Random();
	private final OnlineList list;

	public Debugger(final OnlineList list) {
		this(list, 5);
	}

	public Debugger(final OnlineList list, final int scount) {
		this.list = list;
		for(int i = 0; i < scount; i++)
			players.add(getRandomPlayer());
		this.list.refresh(players);
	}

	private Player getRandomPlayer() {
		final String clan = /*randomizer.nextBoolean() ?*/clans[randomizer.nextInt(clans.length)]/* : Parser.CNONE*/;
		final String name = names[randomizer.nextInt(names.length)];
		final String room = rooms[randomizer.nextInt(rooms.length)];
		final Player newplayer = new Player(name, clan, new Server("", room, "", null, null, 0), null);
		
		if(players.contains(newplayer))	// TODO filter like a Refresher
			return getRandomPlayer();
		
		return newplayer;
	}

	public void remove() {
		if(players.size() > 0) {
			int size = players.size()-1;
			if(size == 0)
				size++;
			players.remove(randomizer.nextInt(size));
		}
	}

	@Override
	public boolean keyDown (final int keycode) {
		if(list.getActionsSize() > 0)
			return false;
		final Player nextrandom = getRandomPlayer();

		if(keycode > 243 && keycode < 255)
			for(int i = 243; i < keycode; i++) {
				Player player = getRandomPlayer();
				players.add(player);
				System.out.println(player.getName() +" generatred");
			}

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
				try {
					throw new NullPointerException("Test");
				}
				catch(Throwable t) {
					list.getGame().handleThrow(t);
					return false;
				}
			case Keys.N:
				try {
					throw new ConnectException("Test");
				}
				catch(Throwable t) {				// Net error
					list.getGame().handleThrow(t);
					return false;
				}
			case Keys.D: // double
				remove();
				remove();
				players.add(nextrandom);
				break;
			case Keys.G: // global
				list.getGame().addGlobal(new Global("New global here", System.currentTimeMillis(), nextrandom.getServer().getRoom(), nextrandom.getName()));
				break;
			case Keys.ESCAPE:
				Gdx.app.exit();
				break;
		}
		
		list.refresh(players);
		return false;
	}

}
