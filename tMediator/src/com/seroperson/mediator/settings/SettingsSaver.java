package com.seroperson.mediator.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.text.JTextComponent;

import com.seroperson.mediator.Mediator;

@SuppressWarnings("serial")
public class SettingsSaver extends JFrame {

	// TODO refactoring ?

	private final List<JTextComponent> clans = new ArrayList<JTextComponent>();
	private final List<JTextComponent> players = new ArrayList<JTextComponent>();
	private boolean globals;
	private boolean minimizeAct;	
	private final int w = 360;
	private final int h = 180;

	public SettingsSaver(final Settings settings) {
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setTitle("Settings");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setSize(w, h);
		setLocation(d.width / 2 - getSize().width / 2, d.height / 2 - getSize().height / 2);

		final Settings current = Mediator.getSettings();
		
		globals = current.isGlobalsTracking();
		minimizeAct = current.isMinimizeAction();
		
		final Component clans = createEditorPanel(settings.getClans(), this.clans);
		final Component players = createEditorPanel(settings.getNames(), this.players);
		
		final Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		final JPanel radio = new JPanel();
		final JCheckBox Cglobals = new JCheckBox("Track globals", current.isGlobalsTracking());
		final JCheckBox CminimizeAct = new JCheckBox("Notify about new players", current.isMinimizeAction());
		
		Cglobals.addItemListener(getItemListener(1));
		CminimizeAct.addItemListener(getItemListener(2));
		
		radio.add(Cglobals);
		radio.add(CminimizeAct);
		
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
		c.add(radio, BorderLayout.NORTH);
		c.add(save, BorderLayout.SOUTH);
	}

	private ItemListener getItemListener(final int index) { 
		return new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				switch(index) { 
					case 1:
						globals = !(e.getItemSelectable().getSelectedObjects() == null);
						break;
					case 2: 
						minimizeAct = !(e.getItemSelectable().getSelectedObjects() == null);
						break;
				}		
			} 
			
		};
	}
	
	private Component createEditorPanel(final String[] arr, final List<JTextComponent> list) {
		final Box compbox = Box.createVerticalBox();
		final Box innerbox = Box.createVerticalBox();

		final Box addBox = Box.createHorizontalBox();
		final JButton add = new JButton("+");
		addBox.add(add);
		compbox.add(Box.createHorizontalGlue());
		compbox.add(addBox);

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
				// scrpane.getVerticalScrollBar().setValue(scrpane.getVerticalScrollBar().getMaximum());
				// scrpane.revalidate();
				innerbox.revalidate();
			}
		});

		return scrpane;
	}

	private JTextField createTextField(final String text, final Box box, final List<JTextComponent> list) {
		final Box boxfield = Box.createHorizontalBox();
		final JTextField field = new JTextField(text, 10);
//		field.setMaximumSize(d);
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
		final Settings current = Mediator.getSettings();
		final List<String> clans = fillArray(this.clans);
		final List<String> players = fillArray(this.players);
		final SettingsDef def = new SettingsDef();
		def.clans = clans.toArray(new String[clans.size()]);
		def.names = players.toArray(new String[players.size()]);
		def.unminimizeonnewplayer = minimizeAct;
		def.globals = globals;
		def.padBottom = current.getPadBottom();
		def.padLeft = current.getPadLeft();
		def.padTop = current.getPadTop();
		def.period = current.getPeriod();
		def.server = current.getServer();
		def.port = current.getPort();
		def.uri = current.getForumURI();
		final Settings settings = new Settings(def);
		try {
			SettingsLoader.writeSettingsToFile(settings, SettingsLoader.getSettingsFile());
		}
		catch (final IOException e) {
			e.printStackTrace(); // TODO handle
		}
		Mediator.setSettings(settings);
	}

}
