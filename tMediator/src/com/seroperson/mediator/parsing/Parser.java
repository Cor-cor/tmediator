package com.seroperson.mediator.parsing;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.seroperson.mediator.tori.stuff.*;
import com.seroperson.mediator.utils.ThrowHandler;

public class Parser {

	private final static String TORIBASH = "TORIBASH";
	private final static String REGEXP_SERVER = "([\\d{1,3}\\.]+):(\\d{1,})\\s(\\p{ASCII}+)";
	private final static String REGEXP_CLIENT = "[^\\s]+\\w"; 
	private final static String REGEXP_MOD = "[^\\s]+\\.tbm";
	private final static String REGEXP_COLORS = "\\^\\d{2}+";
	private final static String REGEXP_JOIN_CMD = "[\\\\\\/][jJ][oO]\\s(\\w+)";
	private final static char[][] limiters = new char[][] { new char[] { '[', ']' }, new char[] { '(', ')' } };
	private static String prevGlobal = "";
	public final static String CNONE = "none";

	public static Server[] getServers(final ThrowHandler th, final String npservers) {
		final StringTokenizer st = new StringTokenizer(npservers, "\n");
		final ArrayList<Server> servers = new ArrayList<Server>(50);
		boolean prevdescisnull = false;
		try {
			while(st.hasMoreTokens()) {
				final Object[] arr = handleString(npservers, st, prevdescisnull);
				final Server server = (Server) arr[0];
				prevdescisnull = (Boolean) arr[1];
				servers.add(server);
			}
		}
		catch (final Exception e) {
			th.handleThrow(e);
		}

		return servers.toArray(new Server[servers.size()]);
	}

	public static Global getGlobal(String npglobal) throws Throwable {
		//ToDo: probly there is more elegant solution but I'm dumb and lazy
		if (npglobal.equals(prevGlobal))
			return null;
		prevGlobal = npglobal;
		
		final GsonBuilder gsonb = new GsonBuilder();
		gsonb.registerTypeAdapter(Date.class, new DateDeserializer());
		final Gson gson = gsonb.create();
		
		final Broadcast bc = gson.fromJson(npglobal, Broadcast.class);
		final BroadcastedMessage global = bc.broadcasts[0];
		final Matcher roomMatcher = Pattern.compile(REGEXP_JOIN_CMD).matcher(global.message);
		final String room;
		if (roomMatcher.find())
			room = roomMatcher.group(1);
		else
			room = "unknown room";
		return new Global(global.message.trim().replaceAll(REGEXP_COLORS, ""), global.post_time.getTime(), room, global.username);
	}

	@SuppressWarnings("unused")
	private static Object[] handleString(final String fullStr, final StringTokenizer st, final boolean prevdescisnull) throws ParseException {
		String currentStr = null;

		{
			if(!prevdescisnull)
				currentStr = st.nextToken();
			Main: {
				if(!(prevdescisnull || currentStr.startsWith(TORIBASH)))
					break Main;
				boolean descnull = false;
				String desc = null;
				String room;
				String mod;
				List<Player> players;
				String host;
				int port;
				currentStr = st.nextToken(); // skip INFO

				currentStr = st.nextToken();
				SERVER: {
					final Pattern pattern = Pattern.compile(REGEXP_SERVER);
					final Matcher matcher = pattern.matcher(currentStr);
					matcher.find();
					host = matcher.group(1).trim();
					port = Integer.valueOf(matcher.group(2).trim());
					room = matcher.group(3).trim();
				}

				currentStr = st.nextToken();
				CLIENTS: {
					final Pattern pattern = Pattern.compile(REGEXP_CLIENT);
					String[] stringTokens = currentStr.split(";");
					String playersList = stringTokens.length > 1 ? stringTokens[1] : ""; 
					final Matcher matcher = pattern.matcher(playersList);
					players = new ArrayList<Player>(5);
					while(matcher.find()) {
						final String withclantag = matcher.group(0).trim();
						char[] clanlimiters = null;
						String withoutclantag = null;
						String clantag = null;
						for(char[] lpack : limiters) { 
							if(withclantag.charAt(0) == lpack[0]) {
								final int clpos = withclantag.indexOf(lpack[1]); // TODO as regex
								clantag = withclantag.substring(1, clpos);
								withoutclantag =  withclantag.substring(clpos+1).trim();
								clanlimiters = lpack;
								break;
							}
						}
						if(clantag == null) {
							clantag = CNONE;
							withoutclantag = withclantag;
						}
						players.add(new Player(withoutclantag, clantag, null, clanlimiters));
					}
				}

				currentStr = st.nextToken();
				NEWGAME: {
					if(currentStr.contains(" classic ")) {
						mod = "classic";
						break NEWGAME;
					}
					final Pattern pattern = Pattern.compile(REGEXP_MOD);
					final Matcher matcher = pattern.matcher(currentStr);
					if (matcher.find())
						mod = matcher.group().trim();
					else
						mod = "undefined mod";
				}

				if(st.hasMoreTokens())
					currentStr = st.nextToken();

				DESC: {
					if(currentStr.startsWith(TORIBASH)) {
						descnull = true;
						break DESC; // yea, desc can be null -.-
					}
					final String[] arr = currentStr.split(";");
					if(arr.length < 1)
						break Main;
					desc = arr[arr.length-1].trim().replaceAll(REGEXP_COLORS, "");
				}

				final Server server = new Server(desc, room, mod, players.toArray(new Player[players.size()]), host, port);
				for(final Player p : players)
					p.setServer(server);
				return new Object[] { server, new Boolean(descnull) };
			}
			int index = fullStr.indexOf(currentStr);
			throw new ParseException(new StringBuilder("Parse error; \n").append(currentStr).append("\n ").append(String.valueOf(index)).toString(), index);

		}
	}

}
