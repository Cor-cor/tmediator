package com.seroperson.mediator.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.screen.OnlineList;
import com.seroperson.mediator.tori.stuff.Global;
import com.seroperson.mediator.tori.stuff.Server;

@SuppressWarnings("serial")
public class GlobalsViewer extends JFrame {
	
	// TODO refactoring (?)

	private Global[] globals;
	private final int factor = 360;

	public GlobalsViewer(final Global[] globals, final OnlineList list, final int endIndex) {
		setTitle("Globals history");

		this.globals = globals;

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		final Container c = getContentPane();
		final JTable table = new JTable(new TModel(this.globals, endIndex));
		final JScrollPane scroll = new JScrollPane(table);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setSize(factor * 2, factor / 2);
		setLocation(d.width / 2 - getSize().width / 2, d.height / 2 - getSize().height / 2);

		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if(e.getButton() != MouseEvent.BUTTON2)
					return;
				final java.awt.Point p = e.getPoint();
				final int rowIndex = table.rowAtPoint(p);
				final int colIndex = table.columnAtPoint(p);
				final int realColumnIndex = table.convertColumnIndexToModel(colIndex);
				if(realColumnIndex == 2) {
					final String room = (String) table.getValueAt(rowIndex, colIndex);
					final Server server = Mediator.getServerByRoom(room, list.getServers());
					if(server == null) {
						JOptionPane.showMessageDialog(getContentPane(), "Server not found");
						return;
					}
					ServerViewer sv;
					if(list.getServerViewer() == null) {
						sv = new ServerViewer(list);
						list.setServerViewer(sv);
					}
					else
						sv = list.getServerViewer();
					sv.add(server, server.getRoom(), true);
				}
			}

		});
		table.getTableHeader().setReorderingAllowed(false);
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(true);
		table.getColumnModel().getColumn(0).setMinWidth(factor / 2);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(Object.class, new AnotherColor());
		c.setLayout(new BorderLayout());

		c.add(scroll, BorderLayout.CENTER);
	}

	public class AnotherColor extends DefaultTableCellRenderer.UIResource {

		private final Color gray = new Color(0.7f, 0.7f, 0.7f, 0.2f);
		private Font fontMod;

		{
			fontMod = getFont().deriveFont(Font.BOLD);
			setOpaque(true);
			setHorizontalAlignment(SwingConstants.CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			final Object valueAtCell = table.getValueAt(row, column);

			if(column == 0) {
				setFont(fontMod);
				setToolTipText((String) valueAtCell);
			}

			if(column == 2)
				if(valueAtCell != null)
					setToolTipText("Click for details (wheel button)");

			if(!isSelected)
				if(row % 2 == 0)
					setBackground(gray);
				else
					setBackground((Color) value);

			return this;
		}

	}

	public class TModel extends AbstractTableModel {

		Global[] globals;
		String[] columns = new String[] { "Message", "Player", "Server", "Date" };
		Object[][] table;
		int index;

		public TModel(final Global[] globals, final int index) {
			this.globals = globals;
			final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			int dindex = index;

			if(dindex < 10)
				dindex = 10;

			this.index = dindex;

			table = new Object[dindex][4];

			for(int i = 0; i < index; i++) {
				final Global g = globals[i];
				final Object[] o = table[i];
				o[0] = g.getMessage();
				o[1] = g.getPlayer();
				o[2] = g.getServer();
				o[3] = sdf.format(g.getDate());
			}

		}

		@Override
		public int getRowCount() {
			return index;
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public String getColumnName(final int index) {
			return columns[index];
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			return table[rowIndex][columnIndex];
		}

	}

}
