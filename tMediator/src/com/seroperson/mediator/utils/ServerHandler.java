package com.seroperson.mediator.utils;

import java.util.Collection;
import java.util.List;
import java.util.TimerTask;

import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.parsing.Parser;
import com.seroperson.mediator.screen.OnlineList;
import com.seroperson.mediator.tori.stuff.Global;
import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.tori.stuff.Server;

public abstract class ServerHandler extends TimerTask {

	private final OnlineList list;
	private final Mediator mediator;
	
	protected ServerHandler(OnlineList list, Mediator mediator) { 
		this.list = list;
		this.mediator = mediator;
	}
	
	protected abstract Server[] getServers() throws Throwable;
	
	protected abstract Global getGlobal() throws Throwable;
	
	protected abstract List<Player> getPlayersOnline(final Server[] servers);
	
	protected boolean handle(final Player p, final List<String> clans, final Collection<Player> alreadyChecked, final List<String> wanted) {
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
	
	public void run() { 

		try {
			
			mediator.setServers(getServers());
			list.refresh(getPlayersOnline(mediator.getServers()));
			
			Globals: {

				if(!Mediator.getSettings().isGlobalsTracking())
					break Globals;
				final Global current = getGlobal();
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
	
	public OnlineList getOnlineList() {
		return list;
	}
	
	public Mediator getMediator() { 
		return mediator;
	}
	
}
