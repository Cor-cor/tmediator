package com.seroperson.mediator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class MediatorCrashReporter extends JFrame {

	// TODO submit to my email ?
	
	public MediatorCrashReporter(Throwable t) throws Throwable {
		setTitle("tMediator crash report");
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setSize(460, 460/2/2);
		setLocation(d.width/2-460/2, d.height/2-460/2/2/2);
		
		StringBuilder text = null;
		ByteArrayOutputStream byteStream = null;
		PrintStream writer = null;
		try {
			StringBuilder info = new StringBuilder("Java heap: ");
			info.append(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
			info.append("\n");
			info.append("Application uptime: ");
			info.append(ManagementFactory.getRuntimeMXBean().getUptime());
			info.append("\n");
			info.append("OC: ");
			info.append(System.getProperty("os.name"));
			info.append("\n");
			byteStream = new ByteArrayOutputStream();
			writer = new PrintStream(byteStream);
			t.printStackTrace(writer);
			text = new StringBuilder();
			text.append(byteStream.toString("UTF-8"));
			text.append("\n");
			text.append(info);		
			byteStream.flush();
		}
		
		finally { 	
			if(byteStream != null)
				byteStream.close();
			if(writer != null)
				writer.close();
		}
		
		final String intext = text.toString();
		
		JPanel panel = new JPanel();
		JScrollPane scrollpane = new JScrollPane(panel);
		JTextArea area = new JTextArea(intext);
		JButton button = new JButton("Copy to clipboard");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				 StringSelection selection = new StringSelection(intext);
				 Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				 clipboard.setContents(selection, selection);		
			} 
			
		});
		area.setEditable(false);
		panel.setLayout(new BorderLayout());
		scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		panel.add(button, BorderLayout.NORTH);
		panel.add(area, BorderLayout.CENTER);
		getContentPane().add(scrollpane);
		getContentPane().revalidate();
	}
	
}
