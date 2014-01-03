package com.seroperson.mediator.utils.handler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.seroperson.mediator.screen.list.VisualList;
import com.seroperson.mediator.tori.stuff.Player;

public class Colored extends ChangeHandler {

	public Colored(final VisualList screen) {
		super(screen);
	}

	@Override
	public boolean start() {
		
		Iterator<Player> iterator = getList().iterator();
		
		while(iterator.hasNext()) {  // TODO as actions
			Player player = iterator.next();
			Table table = getVisualList().getLabelMap().get(player);
			if(table == null) {
				iterator.remove();
				continue;
			}
			for(Actor actor : table.getChildren()) { 
				if(actor.getColor().g > 0)
					actor.getColor().sub(0, 0.0009f, 0, 0);
				if(actor.getColor().g <= 0) {
					actor.getColor().set(Color.BLACK);
					iterator.remove();
					break;
				}
			}
		} 
		
		return isFinished();
	}
	
	@Override 
	public void reset() { 
	}
	
	@Override
	protected Collection<Player> initList() {
		return new HashSet<Player>();
	}

}