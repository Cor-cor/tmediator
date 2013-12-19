package com.seroperson.mediator.refresh;

import java.util.List;

import com.seroperson.mediator.tori.stuff.Player;

public interface RefreshHandler {
	
	public abstract void refresh(final List<Player> players);

}
