package com.seroperson.mediator.threads;

import static com.seroperson.mediator.Mediator.getSettings;
import static com.seroperson.mediator.parsing.Parser.getServers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.ServerViewer;
import com.seroperson.mediator.parsing.Parser;
import com.seroperson.mediator.screen.OnlineList;
import com.seroperson.mediator.settings.Settings;
import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.tori.stuff.Server;

public class Refresher extends Thread {

	private final String server;
	private Socket socket;
	private final int port;
	private final long sleeptime; // 30 sec as default
	private final OnlineList onlinelist;

	public Refresher(final Settings settings, final OnlineList list) {
		setName("Refresher");
		setDaemon(true);
		onlinelist = list;
		sleeptime = settings.getPeriod();
		server = settings.getServer();
		port = settings.getPort();
	}

	@Override
	public void run() {
		while(!isInterrupted()) {
			try {
				onlinelist.refresh(
						getPlayersOnline(getServers(getInformation()),
						getSettings().getNames(),
						getSettings().getClans(),
						onlinelist.getServerViewer(),
						onlinelist.getPlayersInList()));

				sleep(sleeptime);
			} catch (final Throwable e1) {
				((Mediator)onlinelist.getGame()).handleThrow(e1);
			}
		}
	}

	private synchronized String getInformation() throws Throwable {
		socket = Gdx.net.newClientSocket(Protocol.TCP, server, port, new SocketHints());
		if(socket.isConnected()) {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			final StringBuilder builder = new StringBuilder();
			{
				while(true) {
					final String currentStr = reader.readLine();
					if(currentStr == null)
						break;
					builder.append(currentStr).append("\n");
				}
				socket.getInputStream().close();
				socket.dispose();
				reader.close();
			}
			return builder.toString();
		}
		return null;
	}

	public static Player[] getPlayersOnline(final Server[] servers, final String[] allspecplayers, final String[] clans, final ServerViewer siv, final Player[] lastonlinelist) {
		final ArrayList<Player> playersonline = new ArrayList<Player>();
		final ArrayList<String> findedplayers = new ArrayList<String>();
		List<Server> sivServers = null;
		List<Server> addedServers = null;
		if(siv != null) {
			sivServers = siv.getServers();
			addedServers = new ArrayList<Server>(sivServers.size());
		}

		for(final String s : allspecplayers)
			findedplayers.add(s);

		for(final Server server : servers) {
			if(server == null)
				continue;

			if(siv != null) {
				for(final Server sivServer : sivServers) {
					if(server.getRoom().equalsIgnoreCase(sivServer.getRoom())) {
						siv.add(server, server.getRoom(), false);
						addedServers.add(server);
						break;
					}
				}
			}


			for(final Player player : server.getPlayers()) {
				boolean Continue = true;
				final Player mo = getPlayerByName(player.getName(), lastonlinelist);
				final String str = mo == null ? null : mo.getName();
				if(str != null) {
					if(!player.getClan().equals(Parser.CNONE))
						for(final String s : clans) {
							if(s.equalsIgnoreCase(player.getClan())) {
								Continue = false;
								break;
							}
						}
					if(Continue) {
						for(final String s : allspecplayers)
							if(s.equalsIgnoreCase(player.getName()))
								Continue = false;
					}
					if(Continue)
						continue;
					playersonline.add(player);
					findedplayers.remove(player.getName());
					continue;
				}

				Continue = false;
				switch(handleClan(player, clans, playersonline, findedplayers)) {
					case 0:
						playersonline.add(player);
						Continue = true;
						break;
					case 2:
						Continue = true;
						break;
					case 3:
						playersonline.add(player);
						int i = 0;
						for(final String s : findedplayers) {
							if(s.equalsIgnoreCase(player.getName())) {
								findedplayers.remove(i);
								break;
							}
							i++;
						}
						Continue = true;
						break;
					default:
						Continue = true;
						break;
					}
				if(Continue)
					continue;

				for(int i = 0; i < findedplayers.size(); i++) {
					final String neededPlayer = findedplayers.get(i);
					if(player.getName().equalsIgnoreCase(neededPlayer)) {
						playersonline.add(player);
						findedplayers.remove(i);
					}
				}
			}
		}

		if(sivServers != null) {

			for(final Server server : sivServers)
				addedServers.remove(server);

			for(final Server server : addedServers)
				siv.add(null, server.getRoom(), false);

		}

		return playersonline.toArray(new Player[playersonline.size()]);
	}

	/** -1 none
	 *  0 - online & clan
	 *  1 - s offline & clan;
	 *  2 - already & clan
	 *  3 - wanted  */
	private static int handleClan(final Player p, final String[] clans, final List<Player> alreadyChecked, final List<String> wanted) {
		for(final String s : wanted)
			if(s.equalsIgnoreCase(p.getName()))
				return 3;
		if(alreadyChecked.contains(p))
			return 2;
		if(p.getClan().equals(Parser.CNONE))
			return -1;
		for(final String clan : clans)
			if(clan.equalsIgnoreCase(p.getClan()))
				return 0;
		return 1;
	}

	private static Player getPlayerByName(final String str, final Player[] lastonlinelist) {
		for (final Player element : lastonlinelist)
			if(element != null)
				if(element.getName().equalsIgnoreCase(str))
					return element;
		return null;
	}

}
