package com.seroperson.mediator.screen.list;

import static com.seroperson.mediator.Mediator.getSettings;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.screen.ServerInputListener;
import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.utils.SelectableLabel;
import com.seroperson.mediator.viewer.ServerViewerContainer;

public class AnimatedList extends VisualList {
	
	private final ServerViewerContainer container;
	
	public AnimatedList(ServerViewerContainer container) { 
		this.container = container;
		padLeft(getSettings().getPadLeft());
		padTop(getSettings().getPadBottom());
		padRight(getSettings().getPadLeft());
		padBottom(getSettings().getPadBottom());
		align(Align.left);
		left().top();
	}

	@Override
	protected Label updateLabel(Player player, Type index, final String text) {
		super.updateLabel(player);
		final Map<Player, Table> labels = getLabelMap();
		final Label label = (Label)labels.get(player).getChildren().get(index.type);
		final EventListener first = label.getListeners().first();
		
		label.clearListeners();
		label.addListener(first);
		
		if(index == Type.PLAYER)
			label.addListener(getListener(player));

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				label.setText(text);
			}
		};

		final SequenceAction sequence = Actions.sequence(Actions.fadeOut(getSpeed()), Actions.run(runnable), Actions.fadeIn(getSpeed()));
		label.addAction(sequence);
		
		return label;
	}
	
	public boolean isInAction() { 
		return isInAction(getStage().getRoot());
	}
		
	private boolean isInAction(final Group group) {
		for(final Actor actor : group.getChildren()) {
			if(actor.getActions().size > 0)
				return true;
			if(actor instanceof Group) 
				return isInAction((Group) actor);
		}
		return false;
	}
	
	@Override
	protected Label initLabel() {
		Label label = new SelectableLabel("", Mediator.getSkin());
		label.setColor(Color.BLACK);
		label.getColor().g = .7f;
		return label;
	}

	protected float getSpeed() {
		return 0.5f; // TODO to settings ( also see OnlineList )
	}

	@Override
	protected Map<Player, Table> initMap() {
		return new HashMap<Player, Table>();
	}

	public InputListener getListener(Player player) {
		return new ServerInputListener(container, player);
	}

}
