package com.seroperson.mediator.viewer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.seroperson.mediator.screen.OnlineList;
import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.tori.stuff.Server;

public class ServerViewer extends JFrame {

	private static final long serialVersionUID = 2725980927196323525L;
	private final OnlineList list;
	private final ObjectMap<String, JLabel> labels = new ObjectMap<String, JLabel>(3);
	private final ObjectMap<String, Server> servers = new ObjectMap<String, Server>(10);
	private final IntMap<String> indexmap = new IntMap<String>(10);
	private final Box players = Box.createVerticalBox();
	private final JTabbedPane tabbedpane;
	private final JSplitPane jsp;
	private final JScrollPane jsps;

	public ServerViewer(final OnlineList list) {
		setTitle(this.getClass().getSimpleName());
		this.list = list;
		final String[][] str = new String[][] { new String[] { "Desc: ", "Room: ", "Mod: ", "Address: " }, new String[] { "desc", "room", "mod", "address" } };
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setLocation(d.width / 2-480/2/2, d.height / 2-480/2/2);
		setSize(480 / 2, 480 / 2);
		addWindowListener(getWindowListener());

		final Container c = getContentPane();
		c.setLayout(new BorderLayout());

		final Box infoPanel = Box.createVerticalBox();
		for(int i = 0; i < str[0].length; i++) {
			final Box infoDesc = Box.createHorizontalBox();
			final JLabel label = new JLabel(str[1][i]);
			infoDesc.add(new JLabel(str[0][i]));
			infoDesc.add(Box.createHorizontalGlue());
			infoDesc.add(label);
			infoPanel.add(infoDesc);
			labels.put(str[1][i], label);
		}

		tabbedpane = new JTabbedPane();
		tabbedpane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedpane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent e) {
				if(indexmap.containsKey(tabbedpane.getSelectedIndex()))
					update(tabbedpane.getSelectedIndex());
			}
		});

		jsps = new JScrollPane(players);
		jsps.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsps.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		jsps.setWheelScrollingEnabled(true);

		jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, infoPanel, jsps);
		jsp.setContinuousLayout(false);

		final JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if(tabbedpane.getTabCount() <= 1) {
					list.setServerViewer(null);
					setVisible(false);
					dispose();
					return;
				}
				final int index = tabbedpane.getSelectedIndex();
				servers.remove(indexmap.get(index));
				indexmap.clear();
				tabbedpane.removeTabAt(index);
				for(int i = 0; i < tabbedpane.getTabCount(); i++)
					indexmap.put(i, tabbedpane.getTitleAt(i));
				update(tabbedpane.getSelectedIndex());
			}
		});

		c.add(jsp, BorderLayout.CENTER);
		c.add(tabbedpane, BorderLayout.NORTH);
		c.add(close, BorderLayout.SOUTH);

	}

	public List<Server> getServers() {
		final List<Server> s = new ArrayList<Server>(servers.size);
		final Values<Server> values = servers.values();
		while(values.hasNext) {
			final Server server = values.next();
			if(server != null)
				s.add(server);
		}
		return s;
	}

	public void add(final Server server, final String room, final boolean open) {
		int index = 0;
		if(servers.containsKey(room)) {
			servers.put(room, server);
			index = indexmap.findKey(room, false, 0);
		}
		else {
			indexmap.put(indexmap.size, room);
			servers.put(room, server);
			tabbedpane.addTab(room, null);
			index = indexmap.size - 1;
		}
		if(open)
			tabbedpane.setSelectedIndex(index);
	}

	public void update() {
		update(tabbedpane.getSelectedIndex());
	}

	private void update(final int index) {
		update(indexmap.get(index), index);
	}

	private void update(final String room, final int index) {
		if(tabbedpane.getSelectedIndex() != index)
			return;

		final Server server = servers.get(room);
		jsps.setViewportView(null);
		players.removeAll();

		if(server == null) {
			players.removeAll();
			players.add(new JLabel("Server is no longer exist"));
			labels.get("desc").setText(" - ");
			labels.get("room").setText(" - ");
			labels.get("mod").setText(" - ");
			labels.get("address").setText(" - ");
			jsps.setViewportView(players);
			jsp.revalidate();
			return;
		}

		labels.get("desc").setText(server.getDesc());
		labels.get("room").setText(server.getRoom());
		labels.get("mod").setText(server.getMod());
		labels.get("address").setText(server.getAddress());

		for(final Player player : server.getPlayers()) {
			final Box playerBox = Box.createHorizontalBox();
			playerBox.add(new JLabel(new StringBuilder("-").append(player.getNameWithClanTag()).toString()));
			players.add(playerBox);
		}

		jsps.setViewportView(players);
		jsp.revalidate();
	}

	private WindowAdapter getWindowListener() {
		return new WindowAdapter() {

			@Override
			public void windowClosed(final WindowEvent e) {
				list.setServerViewer(null);
			}
		};
	}

}
