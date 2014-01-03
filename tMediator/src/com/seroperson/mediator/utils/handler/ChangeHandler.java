package com.seroperson.mediator.utils.handler;

import java.util.ArrayList;
import java.util.Collection;

import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.screen.list.VisualList;


public abstract class ChangeHandler {

	private final Collection<Player> changequeue;
	private final VisualList list;
	protected boolean started;

	public ChangeHandler(final VisualList screen) {
		changequeue = initList();
		this.list = screen;
	}

	protected Collection<Player> initList() {
		return new ArrayList<Player>();
	}

	protected boolean isFinished() {
		return changequeue.size() == 0;
	}

	public Collection<Player> getList() {
		return changequeue;
	}

	protected VisualList getVisualList() {
		return list;
	}

	public void add(final Player player) {
		changequeue.add(player);
	}

	public void reset() {
		changequeue.clear();
		started = false;
	}

	public boolean start() {
		if(!started)
			return started = true;
		return false;
	}

}
