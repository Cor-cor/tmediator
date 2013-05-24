package com.seroperson.mediator.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.tori.stuff.Server;

public class Parser {

	private final static String TORIBASH = "[TORIBASH].+";
	private final static String REGEXP_SERVER = "(\\d{1,3}\\.+\\d{1,3}+\\.+\\d{1,3}+\\.+\\d{1,3}):(\\d{1,}).([A-Za-z0-9]+)";
	private final static String REGEXP_CLIENT = "[^\\s]+([a-zA-Z0-9]{1,30})";
	private final static String REGEXP_CLAN = "[^a-zA-Z0-9]+"; // FIXME (!)arr size = 3

	public final static String CNONE = "none";

	public static Server[] getServers(final String notparsedinformation) {
		final StringTokenizer st = new StringTokenizer(notparsedinformation, "\n");
		final ArrayList<Server> servers = new ArrayList<Server>(50);
		boolean prevdescisnull = false;
		while(st.hasMoreTokens()) {
			final Object[] arr = handleString(st, prevdescisnull);
			final Server server = (Server)arr[0];
			prevdescisnull = (Boolean)arr[1];
			servers.add(server);
		}
		return servers.toArray(new Server[servers.size()]);
	}

	@SuppressWarnings("unused")
	private static Object[] handleString(final StringTokenizer st, final boolean prevdescisnull) {
		String currentStr = null;
		{
			if(!prevdescisnull)
				currentStr = st.nextToken();
			if(prevdescisnull || currentStr.matches(TORIBASH)) {
				boolean descnull = false;
				String desc = null;
				String room;
				final String mod = null;
				List<Player> players;
				String host;
				int port;
				currentStr = st.nextToken(); // skip INFO

				currentStr = st.nextToken();
				SERVER: {
					final Pattern pattern = Pattern.compile(REGEXP_SERVER);
					final Matcher matcher = pattern.matcher(currentStr);
					matcher.find();
					host = matcher.group(1);
					port = Integer.valueOf(matcher.group(2));
					room = matcher.group(3);
				}

				currentStr = st.nextToken();
				CLIENTS: {
					final Pattern pattern = Pattern.compile(REGEXP_CLIENT);
					final Matcher matcher = pattern.matcher(currentStr.split(";")[1]);
					players = new ArrayList<Player>(5);
					while(matcher.find()) {
						final String withclantag = matcher.group(0);
						String withoutclantag = null;
						String clantag = null;
						if(withclantag.contains("[")) { // or "]"
							final String[] splitted = withclantag.split(REGEXP_CLAN);
							clantag = splitted[1];
							withoutclantag = splitted[2];
						}
						else {
							clantag = CNONE;
							withoutclantag = withclantag;
						}
						players.add(new Player(withoutclantag, clantag, null));
					}
					// TODO (?) #findplayer(string[] allplayers)
				}

				currentStr = st.nextToken();
				NEWGAME: {

				}
				/* TODO handle NEWGAME (?) */

				currentStr = st.nextToken();
				DESC: {
					if(currentStr.matches(TORIBASH)) {
						descnull = true;
						break DESC; // yea, desc can be null -.-
					}
					final String[] arr = currentStr.split(";");
					desc = arr[1].trim();
				}

				final Server server = new Server(desc, room, mod, players.toArray(new Player[players.size()]), host, port);
				for(final Player p : players)
					p.setServer(server);
				return new Object[] { server, new Boolean(descnull) };
			}
			else {
				System.out.println(currentStr +" error in parser"); // (?)
				return new Object[] { null, true};
			}
		}
	}

}
