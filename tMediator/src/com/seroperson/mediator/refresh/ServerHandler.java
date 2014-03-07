package com.seroperson.mediator.refresh;

import java.util.List;
import java.util.TimerTask;

import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.tori.stuff.Global;
import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.tori.stuff.Server;

public abstract class ServerHandler extends TimerTask {

	private final RefreshHandler handler;
	
	protected ServerHandler(RefreshHandler handler) { 
		this.handler = handler;
	}
	
	protected abstract Server[] getServers() throws Throwable;
	
	protected abstract Global getGlobal() throws Throwable;
	
	protected abstract List<Player> getPlayersOnline(final Server[] servers);
	
	public void run() { 

		final Mediator mediator = Mediator.getMediator();
		
		try {
			mediator.setServers(getServers());
			handler.refresh(getPlayersOnline(mediator.getServers()));
			
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
		
}