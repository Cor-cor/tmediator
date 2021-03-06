package com.seroperson.mediator.refresh;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.parsing.Parser;
import com.seroperson.mediator.settings.Settings;
import com.seroperson.mediator.tori.stuff.Global;
import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.tori.stuff.Server;
import com.seroperson.mediator.viewer.ServerViewer;
import com.seroperson.mediator.viewer.ServerViewerContainer;

public class Refresher extends ServerHandler {

	private Socket socket;
	private final ServerViewerContainer container;
	private final URL broadcasts;

	public Refresher(final RefreshHandler handler, ServerViewerContainer con) throws MalformedURLException {
		super(handler);
		container = con;
		broadcasts = new URL(Mediator.getMediator().getSettings().getBroadcastURL());
	}

	protected Global getGlobal() throws Throwable {
		if(!Mediator.getMediator().getSettings().isGlobalsTracking())
			return null;
		
		final URLConnection connection = broadcasts.openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64)");
		connection.connect();
		//ToDo: check server response
		final Scanner reader = new Scanner(new InputStreamReader(connection.getInputStream()));
		final StringBuilder builder = new StringBuilder();
		try {
			builder.append(reader.nextLine());
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		finally {
			reader.close();
		}
		return Parser.getGlobal(builder.toString());
	}

	protected Server[] getServers() throws IOException {
		final Mediator mediator = Mediator.getMediator();
		final Settings settings = mediator.getSettings();
		socket = new Socket(settings.getServer(), settings.getPort());
		
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

		return Parser.getServers(mediator, builder.toString());
	}

	protected List<Player> getPlayersOnline(final Server[] servers) {
		final Settings settings = Mediator.getMediator().getSettings();
		final ServerViewer siv = container.getServerViewer();
		final List<Player> online = new ArrayList<Player>();
		final List<String> clans = new ArrayList<String>(Arrays.asList(settings.getClans()));
		final List<String> caught = new ArrayList<String>(Arrays.asList(settings.getNames()));
		final List<String> rooms = new ArrayList<String>(Arrays.asList(settings.getRooms()));
		
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
		
			for(final Player player : server.getPlayers()) PEach: {

					for(int index = 0; index < rooms.size(); index++) {
						String room = rooms.get(index);
						if(room == null)
							break;
						if(room.equalsIgnoreCase(server.getRoom())) {
							rooms.remove(index);	
							online.addAll(Arrays.asList(server.getPlayers()));
							break PEach;
						}
					}
				
				if(handle(player, clans, online, caught))
					online.add(player);
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

	private boolean handle(final Player p, final List<String> clans, final Collection<Player> alreadyChecked, final List<String> wanted) {
		if(alreadyChecked.contains(p))
			return false;
		for(int index = 0; index < wanted.size(); index++) {
			String wname = wanted.get(index);
			if(wname.equalsIgnoreCase(p.getName())) { 
				wanted.remove(index);
				return true;
			}
		}
		if(p.getClan().equals(Parser.CNONE))
			return false;
		for(final String clan : clans)
			if(clan.equalsIgnoreCase(p.getClan()))
				return true;
		return false;
	}
	
}
