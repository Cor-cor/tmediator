package com.seroperson.mediator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.FloatArray;

/**
 * 
 * Fast implementation of selectable label
 * 
 * used some code from libgdx library; {@link TextField}
 * 
 * @see {@link TextField}
 * 
 * */

public class SelectableLabel extends Label {
	
	public static final SelectableLabelGroup default_group = new SelectableLabelGroup();

	private final Clipboard clipboard;
	private final Drawable selection;
	private final ClickListener defaultListener;
	private final FloatArray glyphPositions = new FloatArray();
	private final FloatArray glyphAdvances = new FloatArray();
	private SelectableLabelGroup group;
	private boolean hasSelection;
	private int selectionStart;
	private int cursor;
	private float selectionX, selectionWidth;
	
	public SelectableLabel (CharSequence text, Skin skin, String styleName, SelectableLabelGroup group) {
		this(text, skin.get(styleName, SelectableLabelStyle.class), group);
	}

	public SelectableLabel (CharSequence text, Skin skin, String fontName, Color color, SelectableLabelGroup group) {
		this(text, new SelectableLabelStyle(skin.getFont(fontName), color), group);
	}

	public SelectableLabel (CharSequence text, Skin skin, String fontName, String colorName, SelectableLabelGroup group) {
		this(text, new SelectableLabelStyle(skin.getFont(fontName), skin.getColor(colorName)), group);
	}
	
	public SelectableLabel (CharSequence text, Skin skin) {
		this(text, skin.get(SelectableLabelStyle.class), default_group);
	}

	public SelectableLabel (CharSequence text, Skin skin, SelectableLabelGroup group) {
		this(text, skin.get(SelectableLabelStyle.class), group);
	}
	
	public SelectableLabel(CharSequence text, SelectableLabelStyle style, SelectableLabelGroup group) {
		super(text, style);
		selection = style.selection;
		clipboard = Gdx.app.getClipboard();
		setGroup(group);
		addListener(defaultListener = new ClickListener() {

			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				if (!super.touchDown(event, x, y, pointer, button)) return false;
				if (pointer == 0 && button != 0) return false;
				if(getStage() != null)
					getStage().setKeyboardFocus(SelectableLabel.this);
				clearSelection();
				setCursorPosition(x);
				selectionStart = cursor;
				return true;
			}

			@Override
			public void touchDragged(final InputEvent event, final float x, final float y, final int pointer) {
				super.touchDragged(event, x, y, pointer);
				setCursorPosition(x);
			}

			@Override
			public boolean keyDown(final InputEvent event, final int keycode) {
				if(!hasSelection)
					return false;
				final boolean ctrl = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT);
				if(ctrl) {
					if(keycode == Keys.C || keycode == Keys.INSERT) {
						copy();
						return true;
					}
					if(keycode == Keys.LEFT) {
						setCursorPosition(cursor-1);
						return true;
					}	
					if(keycode == Keys.RIGHT) {
						setCursorPosition(cursor+1);
						return true;
					}
				}
				return false;
			}

		});
	}
				
	public String getSelectionText() {
		if(!hasSelection)
			return "";
		
		final int minIndex = Math.min(cursor, selectionStart);
		final int maxIndex = Math.max(cursor, selectionStart);
		
		return getText().toString().substring(minIndex, maxIndex);	
	}
	
	public void copy() {
		String text = getSelectionText();
		if(text.length() > 0)
			clipboard.setContents(text);
	}

	public void clearSelection() {
		hasSelection = false;
	}

	@Override
	public void draw(final SpriteBatch batch, final float parentAlpha) {

		if(hasSelection && selection != null) {
		
			float bgLeftWidth = 0;
			float textY = getTextBounds().height / 2 + getStyle().font.getDescent();
	
			if(getStyle().background != null) {
				bgLeftWidth = getStyle().background.getLeftWidth();
				final float bottom = getStyle().background.getBottomHeight();
				textY = (int) (textY + (getHeight() - getStyle().background.getTopHeight() - bottom) / 2 + bottom);
			}
			else
				textY = (int) (textY + getHeight() / 2 - 2f);

			final int minIndex = Math.min(cursor, selectionStart);
			final int maxIndex = Math.max(cursor, selectionStart);
			final float minX = Math.max(glyphPositions.get(minIndex), 0);
			final float maxX = Math.min(glyphPositions.get(maxIndex), glyphPositions.get(glyphPositions.size - 1));
			selectionX = minX;
			selectionWidth = maxX - minX;

			selection.draw(batch, getX() + selectionX + bgLeftWidth + 0, getY() + textY - getTextBounds().height - getStyle().font.getDescent(), selectionWidth, getTextBounds().height + getStyle().font.getDescent() / 2 + 2f);
			
		}

		super.draw(batch, parentAlpha);

	}
		
	public int getStartSelectionPosition() {
		return selectionStart;
	}

	public int getLastSelectionPosition() {
		return cursor;
	}

	@Override
	public void setText(final CharSequence newText) {
		super.setText(newText);
		getStyle().font.computeGlyphAdvancesAndPositions(newText, glyphAdvances, glyphPositions);
	}
	
	public boolean isTextSelected() {
		return hasSelection;
	}
	
	public ClickListener getDefaultClickListener() {
		return defaultListener;
	}
	
	@Override
	public String getText() {
		return super.getText().toString();
	}
	
	public void setSelection(int from, int to) {
		if(to == from) {
			clearSelection();
			return;
		}
		if(to < from) {
			int temp = to;
			to = from;
			from = temp;
		}
		verifyPosition(from);
		selectionStart = from;
		setCursorPosition(to);
	}
	
	public SelectableLabelGroup getGroup() {
		return group;
	}
	
	public void setGroup(SelectableLabelGroup group) {
		this.group = group;
		if(group != null)
			if(!group.getArray().contains(this, true))
				group.add(this);
	}
	
	private void verifyPosition(final int position) {
		if(position <= getText().length() && position >= 0) 
			return;
		throw new IllegalArgumentException("Position must be >= 0 && <= text.size ");	
	}
		
	private void setCursorPosition(final int position) {
		verifyPosition(position);
		if(group != null)
			group.update();
		cursor = position;		
		hasSelection = true;
	}
	
	private void setCursorPosition(final float x) {
		int i = 0;
		for(; i < glyphPositions.size; i++) {
			if(glyphPositions.items[i] > x) {
				break;
			}
		}
		setCursorPosition(Math.max(0, i-1));
	}
	
	static public class SelectableLabelStyle extends LabelStyle {
		
		/** Optional */
		public Drawable selection;
		
		public SelectableLabelStyle () {
			super();
		}

		public SelectableLabelStyle (BitmapFont font, Color fontColor) {
			super(font, fontColor);
		}
		
		public SelectableLabelStyle (BitmapFont font, Color fontColor, Drawable selection) {
			this(font, fontColor);
			this.selection = selection;
		}

		public SelectableLabelStyle (SelectableLabelStyle style) {
			this(style.font, style.fontColor, style.selection);
		}
		
	}

}
