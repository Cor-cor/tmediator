package com.seroperson.mediator.awt;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;

import javax.swing.border.AbstractBorder;

@SuppressWarnings("serial")
public class ShapeStrokeBorder extends AbstractBorder {
	
	private final Shape stroke;
	private final Stroke str;
	private final Paint paint;
		
	public ShapeStrokeBorder(Stroke str, Shape stroke, Paint paint) {
		this.stroke = stroke;
		this.str = str;
		this.paint = paint;
	}
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		g = g.create();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(str);
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setPaint(paint == null ? c == null ? null : c.getForeground() : paint);
		g2d.draw(stroke);
		g.dispose();
	}

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(6, 6, 0, 0);
        return insets;
    }
	
}
