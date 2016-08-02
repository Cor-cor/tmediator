package com.seroperson.mediator.utils.handler;

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
			switch(Mediator.getMediator().getSettings().getSortingType()) {
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
	public ObjectMap<Actor, Integer> initial = new ObjectMap<Actor, Integer>();
	private boolean sort = false;

	public Sorter(final VisualList list) {
		super(list);
	}

	@Override
	public void reset() { 
		super.reset();
		initial.clear();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean start() {
		if(!super.start())
			return isFinished();
		
		if(sort) {
			int index = 0;
			for(Cell<Actor> c : getVisualList().getCells()) {
				if(c.getWidget() != null) // TODO libgdx's bug
					initial.put(c.getWidget(), index);
				index++;
			}
			sort();
			sort = false;
		}
		
		return isFinished();
	}

	public boolean needSort() { 
		return sort;
	}
	
	public void setSort(boolean sort) { 
		this.sort = sort;
	}
	
	private void sort() {
		
		final Map<Player, Table> labels = getVisualList().getLabelMap();
		
		if(labels.size() <= 1)
			return;
		
		final ObjectMap<Player, Integer> sorted = new ObjectMap<Player, Integer>();
		final List<Player> needed = new ArrayList<Player>(getVisualList().getLabelMap().keySet());
		Collections.sort(needed, getPlayerComparator());
		
		final float tableHeight = getVisualList().getHeight();
		final float pad = Mediator.getMediator().getSettings().getPadBottom()*2;
		final Iterator<Entry<Player, Table>> iterator = labels.entrySet().iterator();
		while(iterator.hasNext()) {
			final Entry<Player, Table> entry = iterator.next();
			sorted.put(entry.getKey(), (int) ((tableHeight-entry.getValue().getY()-pad*2f)/entry.getValue().getHeight()));
		}
		
		replace(0, sorted, needed);
		
	}
		
	public static Comparator<Player> getPlayerComparator() {
		return playercomparator;
	}
	
	private void replace(int cindex, final ObjectMap<Player, Integer> oldorder, final List<Player> neworder) {
		if(cindex == neworder.size())
			return;
		
		Player cplayer = oldorder.findKey(cindex, true);
		int nindex = neworder.indexOf(cplayer);
		
		if (nindex != cindex) {
			Actor actor = getVisualList().getLabelMap().get(cplayer);	
			push(cindex, nindex, actor, getVisualList().getLabelMap().get(oldorder.findKey(nindex, true)));
		}
		
		replace(cindex+1, oldorder, neworder);
		
	}
	
	private void push(int cindex, int nindex, final Actor actor, final Actor nactor) {  
		
		int multi = Math.abs(nindex-cindex);
		float amount = actor.getHeight()*multi; //ToDo: investigate why does it throw a NullPointerException
		
		if(cindex < nindex)
			amount *= -1;
				
		actor.addAction(Actions.sequence(
				Actions.moveBy(0, amount, /*getVisualList().getSpeed()*/.5f, Mediator.getMediator().getInterpolation()),
				Actions.run(new Runnable() { 
					public void run() { 
						setCell(actor, getCell(nactor));
					}
				})
			)
		);
		
	}
	
	@SuppressWarnings("unchecked")
	private Cell<Actor> getCell (Actor widget) {
		return getVisualList().getCells().get(initial.get(widget));
	}

	private void setCell(Actor actor, Cell<Actor> cell) { 
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
