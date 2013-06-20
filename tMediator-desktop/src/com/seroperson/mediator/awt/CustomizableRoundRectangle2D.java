package com.seroperson.mediator.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.NoSuchElementException;

/**
 * 
 * Fast implementation of customizable round rectangle
 * 
 * */

public class CustomizableRoundRectangle2D extends RectangularShape {

	private double lwidth, rwidth;
	private double width, height;
	private double uheight, dheight;
	private double x, y;
	
	public CustomizableRoundRectangle2D(float x, float y, float width, float height, float uheight, float rwidth, float dheight, float lwidth) { 
		setRoundRect(x, y, width, height, uheight, rwidth, dheight, lwidth);
	}
	
    public void setRoundRect(float x, float y, float width, float height, float uheight, float rwidth, float dheight, float lwidth) { 
    	setLocation(x, y);
    	this.lwidth = lwidth;
    	this.rwidth = rwidth;
    	this.width = width;
    	this.height = height;
    	this.uheight = uheight;
    	this.dheight = dheight;
    }
    
    public void setLocation(double x, double y) { 
    	this.x = x;
    	this.y = y;
    }
	
	public double getArcLeftWidth() {
        return lwidth;
    }
	
	public double getArcRightWidth() {
        return rwidth;
    }
	
	public double getArcUpHeight() {
        return uheight;
    }
	
	public double getArcDownHeight() {
        return dheight;
    }
	
	public PathIterator getPathIterator(AffineTransform at) {
		return new CustomizableRoundRectIterator(this, at);
	}
		
	@Override
	public Rectangle2D getBounds2D() { return null; } // TODO (?)
	
	@Override
	public boolean contains(double x, double y) { return false; } // TODO (?)
	
	@Override
	public boolean intersects(double x, double y, double w, double h) { return false; } // TODO (?)
		
	@Override
	public boolean contains(double x, double y, double w, double h) { return false;	} // TODO (?)

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}
	
	@Override
	public double getWidth() {
		return width;
	}
	
	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public boolean isEmpty() { return false; } // TODO (?)
	
	@Override
	public void setFrame(double x, double y, double w, double h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}
	
	/** 
	 * 
	 * fast implementation of iterator
	 * 
	 * used some code from {@link RoundRectIterator} 
	 *  
	 *  @see {@link RoundRectIterator} 
	 *  
	 *  */
	
	static class CustomizableRoundRectIterator implements PathIterator {
	    double x, y, w, h, lw, rw, uh, dh;
	    AffineTransform affine;
	    int index;
	    int part;

	    CustomizableRoundRectIterator(CustomizableRoundRectangle2D rr, AffineTransform at) {
	        this.x = rr.getX();
	        this.y = rr.getY();
	        this.w = rr.getWidth();
	        this.h = rr.getHeight();
	        this.lw = rr.getArcLeftWidth();
	        this.rw = rr.getArcRightWidth();
	        this.uh = rr.getArcUpHeight();
	        this.dh = rr.getArcDownHeight();
	        this.affine = at;
	        /*if (aw < 0 || ah < 0) {
	            // Don't draw anything...
	            index = ctrlpts.length;
	        }*/
	    }

	    public int getWindingRule() {
	        return WIND_NON_ZERO;
	    }

	    public boolean isDone() {
	        return index >= ctrlpts.length;
	    }

	    public void next() {
	        index++;
	    }

	    private static final double angle = Math.PI / 5;
	    private static final double a = 1.0 - Math.cos(angle);
	    private static final double b = Math.tan(angle);
	    private static final double c = Math.sqrt(1.0 + b * b) - 1 + a;
	    private static final double cv = 4.0 / 3.0 * a * b / c;
	    private final static double acv = (1.0 - cv) / 2.0;

	    private static double ctrlpts[][] = {
	        {  0.0,  0.0,  0.0,  0.5 },
	        {  0.0,  0.0,  1.0, -0.5 },
	        {  0.0,  0.0,  1.0, -acv,
	           0.0,  acv,  1.0,  0.0,
	           0.0,  0.5,  1.0,  0.0 },
	        {  1.0, -0.5,  1.0,  0.0 },
	        {  1.0, -acv,  1.0,  0.0,
	           1.0,  0.0,  1.0, -acv,
	           1.0,  0.0,  1.0, -0.5 },
	        {  1.0,  0.0,  0.0,  0.5 },
	        {  1.0,  0.0,  0.0,  acv,
	           1.0, -acv,  0.0,  0.0,
	           1.0, -0.5,  0.0,  0.0 },
	        {  0.0,  0.5,  0.0,  0.0 },
	        {  0.0,  acv,  0.0,  0.0,
	           0.0,  0.0,  0.0,  acv,
	           0.0,  0.0,  0.0,  0.5 },
	        {},
	    };
	    private static int types[] = {
	        SEG_MOVETO,
	        SEG_LINETO, SEG_CUBICTO,
	        SEG_LINETO, SEG_CUBICTO,
	        SEG_LINETO, SEG_CUBICTO,
	        SEG_LINETO, SEG_CUBICTO,
	        SEG_CLOSE,
	    };

	    public int currentSegment(float[] coords) {
	        if (isDone()) {
	            throw new NoSuchElementException("roundrect iterator out of bounds");
	        }
	        double ctrls[] = ctrlpts[index];
	        int nc = 0;
	        int parts = part;
	        for (int i = 0; i < ctrls.length; i += 4) {
	        	double[] d = getPart(parts);
	        	double aw = d[0];
	        	double ah = d[1];
	            coords[nc++] = (float) (x + ctrls[i + 0] * w + ctrls[i + 1] * aw);
	            coords[nc++] = (float) (y + ctrls[i + 2] * h + ctrls[i + 3] * ah);
	        }
	        if (affine != null) {
	            affine.transform(coords, 0, coords, 0, nc / 2);
	        }
	        if(index == 2 || index == 4 || index == 6 || index == 8)
	        	part++;
	        return types[index];
	    }

	    public int currentSegment(double[] coords) {
	        if (isDone()) {
	            throw new NoSuchElementException("roundrect iterator out of bounds");
	        }
	        double ctrls[] = ctrlpts[index];
	        int nc = 0;
	        int parts = 0;
	        for (int i = 0; i < ctrls.length; i += 4, parts++) {
	        	double[] d = getPart(parts);
	        	double aw = d[0];
	        	double ah = d[1];
	            coords[nc++] = (x + ctrls[i + 0] * w + ctrls[i + 1] * aw);
	            coords[nc++] = (y + ctrls[i + 2] * h + ctrls[i + 3] * ah);
	        }
	        if (affine != null) {
	            affine.transform(coords, 0, coords, 0, nc / 2);
	        }
	        return types[index];
	    }
	    
	    private double[] getPart(int part) { 
	    	double[] d = new double[2];
	    	switch(part) { 
        		case 0:
        			d[0] = lw;
        			d[1] = dh;
        			break;
        		case 1:
        			d[0] = rw;
        			d[1] = dh;
        			break;
        		case 2:
        			d[0] = rw;
        			d[1] = uh;
        			break;
        		case 3:
        			d[0] = lw;
        			d[1] = uh;
        			break;
        	}
	    	return d;
	    }
	    
	}	
	    
}
