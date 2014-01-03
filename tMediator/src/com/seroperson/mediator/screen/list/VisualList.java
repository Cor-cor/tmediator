package com.seroperson.mediator.screen.list;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.esotericsoftware.tablelayout.Cell;
import com.seroperson.mediator.tori.stuff.Player;

public abstract class VisualList extends BaseList {
		
	public Label updateServer(final Player player) {
		return updateLabel(player, Type.SERVER, new StringBuilder().append(" on ").append(player.getServer().getRoom()).toString());
	}

	public Label updatePlayer(final Player player) {
		return updateLabel(player, Type.PLAYER, player.getNameWithClanTag());
	}
	
	/*@Override
	public void invalidate() {
		super.invalidate();
		for(int index = 0; index < getCells().size();) { 
			Cell<?> cell = getCells().get(index);
			if(cell.getWidget() == null) {
				getCells().remove(index);
				continue;
			}
			index++;
		}
	}*/
	
	public abstract boolean isInAction();
	
	protected abstract Label updateLabel(final Player player, final Type index, final String text);
	
	@Override
	protected Cell<?> handleCell(Cell<?> cell) { 
		return cell.align(Align.left);
	}
			
	public enum Type {
		
		PLAYER(0), SERVER(1);
				
		int type;
		
		Type(int type) { 
			this.type = type;
		}
		
	}
	
}
