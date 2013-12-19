package com.seroperson.mediator.utils.handler;

import com.seroperson.mediator.screen.list.VisualList;
import com.seroperson.mediator.tori.stuff.Player;

public class Updater extends ChangeHandler {

	public Updater(final VisualList list) {
		super(list);
	}

	@Override
	public boolean start() {
		if(!super.start())
			return isFinished();

		for(final Player player : getList())
			getVisualList().updateServer(player);

		getList().clear();

		return isFinished();
	}

	@Override
	public boolean isFinished() {
		return super.isFinished() && !getVisualList().isInAction();
	}

}
