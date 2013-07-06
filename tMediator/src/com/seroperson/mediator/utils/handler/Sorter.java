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
import com.seroperson.mediator.screen.OnlineList;
import com.seroperson.mediator.tori.stuff.Player;

public class Sorter extends ChangeHandler {

	private static Comparator<Player> playercomparator = new Comparator<Player>() {
		@Override
		public int compare(final Player o1, final Player o2) {
			int result = 1;
			switch(getSettings().getSortingType()) {
				case 0:
					result = o1.getNameWithClanTag().compareToIgnoreCase(o2.getNameWithClanTag());
					return result == 0 ? -1 : result;
				case 1:
					result = o1.getName().compareToIgnoreCase(o2.getName());
					return result == 0 ? -1 : result;
				case 2:
					final int l1 = o1.getNameWithClanTag().length();
					final int l2 = o2.getNameWithClanTag().length();
					return l1 < l2 ? 1 : l1 > l2 ? -1 : 1;
			}
			return 0;
		}
	};

	public Sorter(final OnlineList screen) {
		super(screen);
	}

	@Override
	public boolean start() {
		if(!super.start())
			return isFinished();

		if(getSettings().getSortingType() < 3)
			sort();

		return isFinished();
	}

	public void sort() {
		// TODO refactoring
		final Map<Player, Table> labels = getScreen().getLabelMap();
		final ObjectMap<Player, Integer> sorted = new ObjectMap<Player, Integer>();
		final List<Player> needed = getScreen().getPlayersInList();
		Collections.sort(needed, getPlayerComparator());
		
		final float tableHeight = getScreen().getMainTable().getHeight();
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
			if(counter.size() != 0) {
				replace(oldorder.findKey(cindex+1, true), oldorder, neworder, counter);
			}
			return;
		}
		
		Actor actor = getScreen().getLabelMap().get(player);	
		push(cindex, nindex, actor);
		
		Player next = oldorder.findKey(nindex, true);
		setCell(getScreen().getLabelMap().get(player), getScreen().getMainTable().getCell(getScreen().getLabelMap().get(next)));
		replace(next, oldorder, neworder, counter);
		
	}
	
	private boolean push(int cindex, int nindex, Actor actor) {  

		float amount = 0;
		
		if(cindex < nindex)
			amount = -actor.getHeight()*(nindex-cindex);
		else
			if(cindex > nindex)
				amount = actor.getHeight()*(cindex-nindex);
		
		if(getScreen().isAnimated()) 
			actor.addAction(Actions.sequence(Actions.moveBy(0, amount, getScreen().getSpeed(), Mediator.getInterpolation())));
		else 
			actor.translate(0, amount);
		
		return true;
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
		return getScreen().getActionsSize() == 0;
	}

}
