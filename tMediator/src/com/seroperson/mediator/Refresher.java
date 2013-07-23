package com.seroperson.mediator;

import static com.seroperson.mediator.Mediator.getSettings;
import static com.seroperson.mediator.parsing.Parser.getGlobal;
import static com.seroperson.mediator.parsing.Parser.getServers;

import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.TimerTask;

import com.seroperson.mediator.parsing.Parser;
import com.seroperson.mediator.screen.OnlineList;
import com.seroperson.mediator.tori.stuff.Global;
import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.tori.stuff.Server;
import com.seroperson.mediator.viewer.ServerViewer;

public class Refresher extends TimerTask {

	private Socket socket;
	private final OnlineList onlinelist;
	private final Mediator mediator;
	private final URL forum;

	public Refresher(final Mediator m, final OnlineList list) throws Throwable {
		onlinelist = list;
		mediator = m;
		forum = new URL(getSettings().getForumURI());
	}

	@Override
	public void run() {
		try {

			mediator.setServers(getServers(mediator, getNPServers()));
			onlinelist.refresh(getPlayersOnline(mediator.getServers(), getSettings().getNames(), getSettings().getClans(), onlinelist.getServerViewer()));

			Globals: {

				if(!getSettings().isGlobalsTracking())
					break Globals;
				final Global current = getGlobal(getNPGlobal());
				if(current == null)
					break Globals;
				final Global last = mediator.getLastGlobal();
				if(last == null || !current.getMessage().equalsIgnoreCase(last.getMessage()))
					mediator.addGlobal(current);

			}

		}
		catch (final Throwable e) {
			mediator.handleNetThrow(e);
		}
	}

	private String getNPGlobal() throws Throwable {
		final URLConnection connection = forum.openConnection();
		connection.connect();
		final Scanner reader = new Scanner(new InputStreamReader(connection.getInputStream()));
		final StringBuilder builder = new StringBuilder();
		try {
			while(reader.hasNextLine()) {
				final String str = reader.nextLine();
				if(str.contains("<!-- latest ingame broadcast -->")) {
					while(true) {
						final String string = reader.nextLine();
						if(string.contains("<!-- / latest ingame broadcast -->"))
							break;
						builder.append(string);
					}
					break;
				}
				if(str.contains("<!-- nav buttons bar -->")) // too late
					break;
			}
		}
		finally {
			reader.close();
		}

		return builder.toString();
	}

	private String getNPServers() throws Throwable {
		socket = new Socket(getSettings().getServer(), getSettings().getPort());
		final Scanner reader = new Scanner(new InputStreamReader(socket.getInputStream()));
		final StringBuilder builder = new StringBuilder();

		try {
			while(reader.hasNextLine()) {
				final String currentStr = reader.nextLine();
				builder.append(currentStr).append("\n");
			}
		}
		finally {
			socket.close();
			reader.close();
		}

		return builder.toString();
	}

	public static List<Player> getPlayersOnline(final Server[] servers, final String[] allplayers, final String[] clans, final ServerViewer siv) {
		final List<Player> online = new ArrayList<Player>();
		final List<String> caught = new ArrayList<String>(Arrays.asList(allplayers));

		Collection<Server> viewer = null;
		Collection<Server> added = null;
		if(siv != null) {
			viewer = siv.getServers();
			added = new ArrayList<Server>(viewer.size());
		}

		for(final Server server : servers) {
			if(server == null)
				continue;

			if(siv != null) {
				if(viewer.contains(server)) {
					siv.add(server, server.getRoom(), false);
					added.add(server);
				}
			}

			for(final Player player : server.getPlayers()) {
				switch(handle(player, clans, online, caught)) {
					case 0:
					case 3:
						online.add(player);
				}
			}
		}

		if(siv != null) {

			viewer.removeAll(added);

			for(final Server server : viewer)
				siv.add(null, server.getRoom(), false);

			siv.update();

		}

		return online;
	}

	/**
	 * -1 none
	 * 0 - online & clan
	 * 1 - s offline & clan;
	 * 2 - already & clan
	 * 3 - wanted
	 */
	private static int handle(final Player p, final String[] clans, final Collection<Player> alreadyChecked, final List<String> wanted) {
		for(int index = 0; index < wanted.size(); index++) {
			String wname = wanted.get(index);
			if(wname.equalsIgnoreCase(p.getName())) { 
				wanted.remove(index);
				return 3;
			}
		}
		if(alreadyChecked.contains(p))
			return 2;
		if(p.getClan().equals(Parser.CNONE))
			return -1;
		for(final String clan : clans)
			if(clan.equalsIgnoreCase(p.getClan()))
				return 0;
		return 1;
	}

}
