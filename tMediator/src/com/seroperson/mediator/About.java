package com.seroperson.mediator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


@SuppressWarnings("serial")
public class About extends JFrame {

	private final static String[] link = new String[] {
		"http://www.gnu.org/copyleft/gpl.txt",
		"https://github.com/seroperson/tmediator",
		"http://vk.com/seroperson",
		"http://forum.toribash.com/private.php?do=newpm&u=3029412",
		"http://forum.toribash.com/forumdisplay.php?f=446" };
	
	private final static String[] text = new String[] {
		"tMediator",
		new StringBuilder("Java version ").append(System.getProperty("java.version")).toString(),
		"Distributed under GNU General Public License",
		"Visit a github repository",
		"Visit a author page",
		"Report a error" };
	
	private final static ImageIcon im = new ImageIcon(About.class.getResource("/skin/logotype_small.png"));
	private final int w = 360;
	private final int h = 260/2;
	private final Dimension button = new Dimension(w, 15);

	public About() {

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setTitle("About");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setSize(w, h);
		setLocation(d.width / 2 - getSize().width / 2, d.height / 2 - getSize().height / 2);

		final Container c = getContentPane();
		final Box left = Box.createVerticalBox();
		final JButton rubash = createLink(new JButton(im), link[4]);
		final JPanel center = new JPanel();

		center.add(initCenterPanel(), BorderLayout.CENTER);
		left.add(Box.createVerticalStrut(h/2/2));
		left.add(rubash);
		c.add(left, BorderLayout.WEST);
		c.add(center, BorderLayout.CENTER);
	}

	private Component initCenterPanel() {
		final Box panel = Box.createVerticalBox();
		final Font font = new Font("Arial", Font.BOLD, 10);
		final boolean[] isbutton = new boolean[] { false, false, true, true, true, true };
		int buttonindex = 0;
		for(int index = 0; index < text.length; index++) {
			Component label;
			if(isbutton[index]) {
				label = createLink(new JButton(text[index]), link[buttonindex++]);
				label.setPreferredSize(button);
				label.setFont(font);
			}
			else {
				label = new JLabel(text[index]);
			}
			final Box box = Box.createHorizontalBox();
			box.add(label);

			panel.add(box);
		}
		return panel;
	}

	public static String[] getLinks() {
		return link;
	}
	
	public static JButton createLink(final JButton label, final String uri) {
		label.setText(new StringBuilder("<html><u>").append(label.getText()).toString());
		label.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if(Desktop.isDesktopSupported()) {
					try {
						try {
							Desktop.getDesktop().browse(new URI(uri));
						}
						catch (final URISyntaxException uexc) {
							uexc.printStackTrace();
						}
					}
					catch (final IOException ioexc) {
						ioexc.printStackTrace();
					}
				}
			}
		});

		label.setBorderPainted(false);
		label.setOpaque(false);
		label.setBackground(Color.WHITE);

		return label;
	}

}