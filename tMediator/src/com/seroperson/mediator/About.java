package com.seroperson.mediator;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.WindowConstants;


@SuppressWarnings("serial")
public class About extends JFrame {
	
	private final static Image im = Toolkit.getDefaultToolkit().getImage(About.class.getResource("/skin/logotype.png"));
	private final int w = 360;
	private final int h = 260;

	public About() { 
		
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		
		setTitle("About");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
		setSize(w, h);
		setLocation(d.width / 2 - getSize().width / 2, d.height / 2 - getSize().height / 2);
		
		Container c = getContentPane();
//		c.add(new JLabel(im));
	}
	
}
