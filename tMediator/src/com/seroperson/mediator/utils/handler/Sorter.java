package com.seroperson.mediator.utils.handler;

import static com.seroperson.mediator.Mediator.getSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.tablelayout.Cell;
import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.screen.list.VisualList;
import com.seroperson.mediator.tori.stuff.Player;

public class Sorter extends ChangeHandler {

	private static Comparator<Player> playercomparator = new Comparator<Player>() {
		@Override
		public int compare(final Player o1, final Player o2) {
			int result = 1;
			switch(getSettings().getSortingType()) {
				case 0:
					result = o1.getNameForSorting().compareToIgnoreCase(o2.getNameForSorting());
					break;
				case 1:
					result = o1.getName().compareToIgnoreCase(o2.getName());
					break;
				case 2:
					result = o1.getServer().getRoom().compareToIgnoreCase(o2.getServer().getRoom());
					break;
			}
			return result == 0 ? -1 : result;
		}
	};
	
	private boolean sort = false;

	public Sorter(final VisualList list) {
		super(list);
	}

	@Override
	public boolean start() {
		if(!super.start())
			return isFinished();
		
//		if(sort)
			sort();
		
//		sort = false;
		
		return isFinished();
	}

	public boolean needSort() { 
		return sort;
	}
	
	public void setSort(boolean sort) { 
		this.sort = sort;
	}
	
	public void sort() {
		
		final Map<Player, Table> labels = getVisualList().getLabelMap();
		
		if(labels.size() <= 1)
			return;
		
		final ObjectMap<Player, Integer> sorted = new ObjectMap<Player, Integer>();
		final List<Player> needed = new ArrayList<Player>(getVisualList().getLabelMap().keySet());
		Collections.sort(needed, getPlayerComparator());
		
		final float tableHeight = getVisualList().getHeight();
		final float pad = getSettings().getPadBottom()*2;
		final Iterator<Entry<Player, Table>> iterator = labels.entrySet().iterator();
		while(iterator.hasNext()) {
			final Entry<Player, Table> entry = iterator.next();
			sorted.put(entry.getKey(), (int) ((tableHeight-entry.getValue().getY()-pad)/15));
		}
		List<Player> counter = new ArrayList<Player>(needed);
		replace(sorted.findKey(0, true), sorted, needed, counter);
	}
		
	public static Comparator<Player> getPlayerComparator() {
		return playercomparator;
	}
	
	private void replace(final Player player, final ObjectMap<Player, Integer> oldorder, final List<Player> neworder, final List<Player> counter) {
		int cindex = oldorder.get(player);
		int nindex = neworder.indexOf(player);
		if(!counter.remove(player) || cindex == nindex) {
			if(counter.size() != 0) 
				replace(oldorder.findKey(cindex+1, true), oldorder, neworder, counter);
			return;
		}
		
		Actor actor = getVisualList().getLabelMap().get(player);	
		push(cindex, nindex, actor);
		
		Player next = oldorder.findKey(nindex, true);
		setCell(getVisualList().getLabelMap().get(player), getVisualList().getCell(getVisualList().getLabelMap().get(next)));
		replace(next, oldorder, neworder, counter);
		
	}
	
	private void push(int cindex, int nindex, Actor actor) {  

		float amount = 0;
		
		if(cindex < nindex)
			amount = -actor.getHeight()*(nindex-cindex);
		else
			if(cindex > nindex)
				amount = actor.getHeight()*(cindex-nindex);
		
		actor.addAction(Actions.sequence(Actions.moveBy(0, amount, /*getVisualList().getSpeed()*/.5f, Mediator.getInterpolation())));
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setCell(Actor actor, Cell cell) { 
		cell.setWidget(actor);
		cell.setWidgetHeight(actor.getHeight());
		cell.setWidgetWidth(actor.getWidth());
		cell.setWidgetX(actor.getX());
		cell.setWidgetY(actor.getY());
	}
	
	@Override
	protected boolean isFinished() {
		return !getVisualList().isInAction();
	}

}
