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
		
		System.out.println(getList().toString());

		Iterator<Player> iterator = getList().iterator();
		
		while(iterator.hasNext()) { 
			Player player = iterator.next();
			Table table = getVisualList().getLabelMap().get(player);
			System.out.println("Handling "+player.getName());
			for(Actor actor : table.getChildren()) { 
				System.out.println("Color: "+actor.getColor());
				if(actor.getColor().g > 0)
					actor.getColor().sub(0, 0.002f, 0, 0);
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
	protected Collection<Player> initList() {
		return new HashSet<Player>();
	}

}