package com.seroperson.mediator.utils.handler;

import java.util.Iterator;
import java.util.Map.Entry;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.screen.list.VisualList;
import com.seroperson.mediator.tori.stuff.Player;

public class Remover extends ChangeHandler {

	public Remover(final VisualList list) {
		super(list);
	}

	@Override
	public boolean start() {
		if(!super.start())
			return isFinished();

		if(getList().size() == 0)
			return true;

		final Iterator<Player> iterator = getList().iterator();
		while(iterator.hasNext()) {
			final Player player = iterator.next();
			final Table remLabels = getVisualList().getLabelMap().remove(player);

			final Runnable runnable = new Runnable() {

				@Override
				public void run() {
					getList().remove(player);
				}
			};

			remLabels.addAction(Actions.sequence(Actions.fadeOut(/*getVisualList().getSpeed()*/.5f), Actions.run(runnable), Actions.removeActor()));

			final Iterator<Entry<Player, Table>> inner = getVisualList().getLabelMap().entrySet().iterator();

			while(inner.hasNext()) {
				final Entry<Player, Table> entry = inner.next();
				final Table labels = entry.getValue();
				if(getList().contains(entry.getKey()))
					continue;
				if(labels.getY() < remLabels.getY())
					labels.addAction(Actions.moveBy(0, labels.getHeight(), /*getVisualList().getSpeed()*/.5f, Mediator.getInterpolation()));
			}
		}

		return isFinished();
	}

	@Override
	protected boolean isFinished() {
		return super.isFinished() && !getVisualList().isInAction();
	}
	
}
