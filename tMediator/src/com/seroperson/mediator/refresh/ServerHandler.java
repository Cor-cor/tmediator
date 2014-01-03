package com.seroperson.mediator.refresh;

import java.util.Collection;
import java.util.List;
import java.util.TimerTask;

import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.parsing.Parser;
import com.seroperson.mediator.tori.stuff.Global;
import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.tori.stuff.Server;

public abstract class ServerHandler extends TimerTask {

	private final RefreshHandler handler;
	private final Mediator mediator;
	
	protected ServerHandler(RefreshHandler handler, Mediator mediator) { 
		this.handler = handler;
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
			handler.refresh(getPlayersOnline(Mediator.getServers()));
			
			final Global current = getGlobal();
			
			if(current == null)
				return;
			
			final Global last = mediator.getLastGlobal();
			if(last == null || !current.getMessage().equalsIgnoreCase(last.getMessage()))
				mediator.addGlobal(current);

		}
		catch (final Throwable e) {
			mediator.handleNetThrow(e);
			e.printStackTrace();
		}
		
	}
		
	public Mediator getMediator() { 
		return mediator;
	}
	
}