package com.seroperson.mediator.utils.handler;

import com.seroperson.mediator.screen.OnlineList;
import com.seroperson.mediator.tori.stuff.Player;

public class Updater extends ChangeHandler {

	public Updater(final OnlineList screen) {
		super(screen);
	}

	@Override
	public boolean start() {
		if(!super.start())
			return isFinished();

		for(final Player player : getList())
			getScreen().updateServer(player);

		getList().clear();

		return isFinished();
	}

	@Override
	public boolean isFinished() {
		return super.isFinished() && getScreen().getActionsSize() == 0;
	}

}
