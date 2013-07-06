package com.seroperson.mediator.utils.handler;

import java.util.ArrayList;
import java.util.Collection;

import com.seroperson.mediator.screen.OnlineList;
import com.seroperson.mediator.tori.stuff.Player;

public abstract class ChangeHandler {

	private final Collection<Player> changequeue;
	private final OnlineList screen;
	private boolean started;

	public ChangeHandler(final OnlineList screen) {
		changequeue = setList();
		this.screen = screen;
	}

	protected Collection<Player> setList() {
		return new ArrayList<Player>();
	}

	protected boolean isFinished() {
		return changequeue.size() == 0;
	}

	protected Collection<Player> getList() {
		return changequeue;
	}

	protected OnlineList getScreen() {
		return screen;
	}

	public void add(final Player player) {
		changequeue.add(player);
	}

	public void reset() {
		changequeue.clear();
		started = false;
	}

	public boolean start() {
		if(started)
			return false;
		else
			return started = true;
	}

}
