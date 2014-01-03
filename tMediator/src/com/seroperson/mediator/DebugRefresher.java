package com.seroperson.mediator;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.seroperson.mediator.refresh.RefreshHandler;
import com.seroperson.mediator.refresh.ServerHandler;
import com.seroperson.mediator.tori.stuff.Global;
import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.tori.stuff.Server;

public class DebugRefresher extends ServerHandler {
	
	private final ArrayList<Global> queue_globals = new ArrayList<Global>();
	private final ArrayList<Player> queue_players = new ArrayList<Player>();
	private final ArrayList<Server> queue_servers = new ArrayList<Server>();

	protected DebugRefresher(RefreshHandler handler, Mediator mediator) {
		super(handler, mediator);
	}

	@Override
	protected Server[] getServers() throws Throwable {
		ObjectMap<String, ArrayList<Player>> temporary = new ObjectMap<String, ArrayList<Player>>(); 
		
		for(Player player : queue_players) { 
			String room = player.getServer().getRoom();
			if(!temporary.containsKey(room)) {
				ArrayList<Player> list = new ArrayList<Player>(); 
				temporary.put(room, list);
				list.add(player);
			}
			else {
				ArrayList<Player> players = temporary.get(room);
				if(!players.contains(player))
					players.add(player);
			}
		}
		
		queue_servers.clear();
		for(Entry<String, ArrayList<Player>> entry : temporary.entries())  
			queue_servers.add(new Server("Desc", entry.key, "Mod", entry.value.toArray(new Player[entry.value.size()]), "Host", 00000));
		
		return queue_servers.toArray(new Server[queue_servers.size()]);
	}

	@Override
	protected Global getGlobal() throws Throwable {
		return queue_globals.size() > 0 ? queue_globals.remove(0) : null;
	}

	@Override
	protected List<Player> getPlayersOnline(Server[] servers) {
		return queue_players;
	}
	
	public ArrayList<Server> getQueueServers() {
		return queue_servers;
	}

	public ArrayList<Global> getQueueGlobals() {
		return queue_globals;
	}
	
	public ArrayList<Player> getQueuePlayers() {
		return queue_players;
	}

}
