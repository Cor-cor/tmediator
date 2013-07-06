package com.seroperson.mediator.utils.handler;

import java.util.Iterator;
import java.util.Map.Entry;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.screen.OnlineList;
import com.seroperson.mediator.tori.stuff.Player;

public class Remover extends ChangeHandler {

	public Remover(final OnlineList screen) {
		super(screen);
	}

	@Override
	public void reset() {
		super.reset();
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
			final Table remLabels = getScreen().getLabelMap().remove(player);

			final Runnable runnable = new Runnable() {

				@Override
				public void run() {
					getList().remove(player);
				}
			};

			if(!getScreen().isAnimated()) {
				iterator.remove();
				remLabels.remove();
			}
			else
				remLabels.addAction(Actions.sequence(Actions.fadeOut(getScreen().getSpeed()), Actions.run(runnable), Actions.removeActor()));

			if(!getScreen().isAnimated())
				continue;

			final Iterator<Entry<Player, Table>> inner = getScreen().getLabelMap().entrySet().iterator();

			while(inner.hasNext()) {
				final Entry<Player, Table> entry = inner.next();
				final Table labels = entry.getValue();
				if(getList().contains(entry.getKey()))
					continue;
				if(labels.getY() < remLabels.getY())
					labels.addAction(Actions.moveBy(0, labels.getHeight(), getScreen().getSpeed(), Mediator.getInterpolation()));
			}
		}

		return isFinished();
	}

}
