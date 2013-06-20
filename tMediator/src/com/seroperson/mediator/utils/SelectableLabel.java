package com.seroperson.mediator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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

	private final Clipboard clipboard;
	private final Drawable selection;
	private final ClickListener defaultListener;
	private final FloatArray glyphPositions = new FloatArray();
	private final FloatArray glyphAdvances = new FloatArray();
	private boolean hasSelection;
	private int selectionStart;
	private int cursor;
	private float selectionX, selectionWidth;

	public SelectableLabel(final CharSequence text, final Skin skin) {
		super(text, skin);
		selection = skin.getDrawable("selection");
		clipboard = Gdx.app.getClipboard();
		addListener(defaultListener = new ClickListener() {

			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				if(!super.touchDown(event, x, y, pointer, button))
					return false;
				if(pointer == 0 && button != 0)
					return false;
				if(getStage() != null)
					getStage().setKeyboardFocus(SelectableLabel.this);
				setCursorPosition(x);
				selectionStart = cursor;
				return true;
			}

			@Override
			public void touchDragged(final InputEvent event, final float x, final float y, final int pointer) {
				super.touchDragged(event, x, y, pointer);
				setCursorPosition(x);
				hasSelection = true;
			}

			private void setCursorPosition(final float x) {
				for(int i = 0; i < glyphPositions.size; i++) {
					if(glyphPositions.items[i] > x) {
						cursor = Math.max(0, i - 1);
						return;
					}
				}
				cursor = Math.max(0, glyphPositions.size - 1);
			}

			@Override
			public boolean keyDown(final InputEvent event, final int keycode) {
				final boolean ctrl = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT);
				if(ctrl) {
					if(keycode == Keys.C || keycode == Keys.INSERT) {
						copy();
						return true;
					}
				}
				return false;
			}

		});
	}

	public void copy() {
		if(hasSelection) {
			final int minIndex = Math.min(cursor, selectionStart);
			final int maxIndex = Math.max(cursor, selectionStart);
			clipboard.setContents(getText().toString().substring(minIndex, maxIndex));
		}
	}

	public void clearSelection() {
		hasSelection = false;
	}

	@Override
	public void draw(final SpriteBatch batch, final float parentAlpha) {

		final Drawable selection = this.selection;
		float bgLeftWidth = 0;
		float textY = getTextBounds().height / 2 + getStyle().font.getDescent();

		if(getStyle().background != null) {
			bgLeftWidth = getStyle().background.getLeftWidth();
			final float bottom = getStyle().background.getBottomHeight();
			textY = (int) (textY + (getHeight() - getStyle().background.getTopHeight() - bottom) / 2 + bottom);
		}
		else
			textY = (int) (textY + getHeight() / 2 - 2f);

		if(hasSelection && selection != null) {
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

	public ClickListener getDefaultListener() {
		return defaultListener;
	}

	@Override
	public void setText(final CharSequence newText) {
		super.setText(newText);
		getStyle().font.computeGlyphAdvancesAndPositions(newText, glyphAdvances, glyphPositions);
	}

}
