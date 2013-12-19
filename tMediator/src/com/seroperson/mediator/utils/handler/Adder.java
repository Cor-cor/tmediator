package com.seroperson.mediator.utils.handler;

import java.util.Collection;
import java.util.HashSet;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.seroperson.mediator.screen.list.VisualList;
import com.seroperson.mediator.tori.stuff.Player;

public class Adder extends ChangeHandler {

	public Adder(final VisualList screen) {
		super(screen);
	}

	@Override
	public boolean start() {
		if(!super.start())
			return isFinished();
		
		final Table main = getVisualList();

		for(final Player player : getList()) {
			getVisualList().updatePlayer(player);
			getVisualList().updateServer(player);
			main.add(getVisualList().getLabelMap().get(player)).align(Align.left);
			main.row();
		}
		
		getList().clear();

		return isFinished();
	}

	@Override
	protected Collection<Player> initList() {
		return new HashSet<Player>();
	}

	@Override
	protected boolean isFinished() {
		return super.isFinished() && !getVisualList().isInAction();
	}

}
