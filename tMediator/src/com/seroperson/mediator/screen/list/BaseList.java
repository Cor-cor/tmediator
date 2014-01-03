package com.seroperson.mediator.screen.list;

import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.Cell;
import com.seroperson.mediator.tori.stuff.Player;

public abstract class BaseList extends Table {

	private final Map<Player, Table> labels = initMap();
	
	protected Table updateLabel(Player player) {
		if(!labels.containsKey(player)) {
			final Table current = new Table();
			current.setName(player.getName()); 
			labels.put(player, current);
			for(int i = 0; i < 2; i++) {
				Label label = initLabel();
				handleCell(current.add(label));
			}
		}
		return labels.get(player);	
	}
		
	protected abstract Cell<?> handleCell(Cell<?> cell);
	
	protected abstract Label initLabel();
		
	protected abstract Map<Player, Table> initMap();
		
	public Map<Player, Table> getLabelMap() {
		return labels;
	}
		
}
