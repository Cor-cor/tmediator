package com.seroperson.mediator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class SelectableLabelGroup {

	private final Array<SelectableLabel> labels = new Array<SelectableLabel>();
	private SelectableLabel last;
	
	public SelectableLabelGroup() { 

	}
	
	public SelectableLabelGroup(final SelectableLabel... labels) { 
		add(labels);
	}
		
	public void add(SelectableLabel selectable) {
		verifyValue(selectable);
		labels.add(selectable);
		update();
	}
	
	public void add(SelectableLabel... selectable) {
		for(SelectableLabel s : selectable) 
			verifyValue(s);
		labels.addAll(selectable);
		update();
	}
	
	public void update() {
		for(SelectableLabel selectable : labels) {
			if(selectable.isTextSelected() && selectable != last) {
				if(last != null)
					last.clearSelection();
				last = selectable;
				return;
			}
		}
	}
	
	public void clearSelection() {
		for(SelectableLabel selectable : labels)
			selectable.clearSelection();
	}
	
	public void copyAll() {
		StringBuilder builder = new StringBuilder();
		for(SelectableLabel selectable : labels) {
			builder.append(selectable);
		}
		Gdx.app.getClipboard().setContents(builder.toString());
	}
	
	public Array<SelectableLabel> getArray() {
		return labels;
	}
	
	private void verifyValue(SelectableLabel s) {
		if(s == null) throw new IllegalArgumentException("element cannot be null");
	}
	
}
