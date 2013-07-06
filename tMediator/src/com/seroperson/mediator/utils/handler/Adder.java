package com.seroperson.mediator.utils.handler;

import java.util.Collection;
import java.util.HashSet;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.seroperson.mediator.screen.OnlineList;
import com.seroperson.mediator.tori.stuff.Player;

public class Adder extends ChangeHandler {

	public Adder(final OnlineList screen) {
		super(screen);
	}

	@Override
	public boolean start() {
		if(!super.start())
			return isFinished();
		
		final Table main = getScreen().getMainTable();

		for(final Player player : getList()) {
			getScreen().updatePlayer(player);
			getScreen().updateServer(player);
			main.add(getScreen().getLabelMap().get(player)).align(Align.left);
			main.row();
		}
		
		getList().clear();

		return isFinished();
	}

	@Override
	protected Collection<Player> setList() {
		return new HashSet<Player>();
	}

	@Override
	protected boolean isFinished() {
		return super.isFinished() && getScreen().getActionsSize() == 0;
	}

}
