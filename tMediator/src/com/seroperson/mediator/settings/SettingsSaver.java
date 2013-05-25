package com.seroperson.mediator.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.text.JTextComponent;

import com.seroperson.mediator.Mediator;

public class SettingsSaver extends JFrame {

	// TODO refactoring (?)

	private final List<JTextComponent> clans = new ArrayList<JTextComponent>();
	private final List<JTextComponent> players = new ArrayList<JTextComponent>();
	private static final int w = 360;
	private static final int h = 360;
	private static final long serialVersionUID = -3675851769151978641L;
	private static final Dimension d = new Dimension(w, h/14);

	public SettingsSaver(final Settings settings) {
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setTitle("Settings");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setLocation(d.width/2, d.height/2);
		setSize(w, h);

		final Component clans = createEditorPanel(settings.getClans(), this.clans);
		final Component players = createEditorPanel(settings.getNames(), this.players);

		final Container c = getContentPane();
		c.setLayout(new BorderLayout());

		final JPanel savepanel = new JPanel();
		final JButton savebutton = new JButton("Save");
		savebutton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				save();
				setVisible(false);
				dispose();
			}

		});
		savepanel.add(savebutton);

		final JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, clans, players);
		jsp.setContinuousLayout(true);
		final JSplitPane save = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jsp, savepanel);

		c.add(jsp, BorderLayout.CENTER);
		c.add(save, BorderLayout.SOUTH);
	}

	private Component createEditorPanel(final String[] arr, final List<JTextComponent> list) {
		final Box compbox = Box.createVerticalBox();
		final Box innerbox = Box.createVerticalBox();

		final JButton add = new JButton("+");
		compbox.add(add);

		for(final String str : arr)
			createTextField(str, innerbox, list);
		createTextField("", innerbox, list);

		compbox.add(innerbox);

		final JScrollPane scrpane = new JScrollPane(compbox);
		scrpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrpane.setWheelScrollingEnabled(true);

		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				createTextField("", innerbox, list);
//				scrpane.getVerticalScrollBar().setValue(scrpane.getVerticalScrollBar().getMaximum());
//				scrpane.revalidate();
				innerbox.revalidate();
			}
		});

		return scrpane;
	}

	private JTextField createTextField(final String text, final Box box, final List<JTextComponent> list) {
		final Box boxfield = Box.createHorizontalBox();
		final JTextField field = new JTextField(text, 10);
		field.setMaximumSize(d);
		field.setCaretPosition(0);
		final JButton delete = new JButton("-");
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if(list.contains(field))
					list.remove(field);
				box.remove(boxfield);
				box.revalidate();
			}
		});
		boxfield.add(field);
		boxfield.add(delete);
		list.add(field);
		box.add(boxfield);
		return field;
	}

	private List<String> fillArray(final List<JTextComponent> list) {
		final List<String> strlist = new ArrayList<String>();
		for(final JTextComponent str : list)
			if(!str.getText().trim().equals(""))
				strlist.add(str.getText().trim());
		return strlist;
	}

	private void save() {
		final List<String> clans = fillArray(this.clans);
		final List<String> players = fillArray(this.players);
		final SettingsDef def = new SettingsDef();
		def.clans = clans.toArray(new String[clans.size()]);
		def.names = players.toArray(new String[players.size()]);
		final Settings settings = new Settings(def);
		try {
			SettingsLoader.writeSettingsToFile(settings, SettingsLoader.getSettingsFile());
		} catch (final IOException e) {
			e.printStackTrace();
		}
		Mediator.setSettings(settings);
	}

}
