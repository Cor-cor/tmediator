package com.seroperson.mediator;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.seroperson.mediator.awt.CustomizableRoundRectangle2D;
import com.seroperson.mediator.awt.ShapeStrokeBorder;
import com.seroperson.mediator.screen.OnlineList;
import com.seroperson.mediator.tori.stuff.Global;

@SuppressWarnings("serial")
public class MediatorDesktop extends JFrame {
	
	private Image icon;
	private final Point location = new Point();
	private final Mediator mediator;
	
	public MediatorDesktop() { 
		super("tMediator");
		int scale = 2;
		final int w = 360/scale;
		final int h = 360/scale;
		final CustomizableRoundRectangle2D crr = new CustomizableRoundRectangle2D(0f, 0f, w, h, 15, 0, 0, 15);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setShape(crr);
			}
		});
		
		final TrayIcon tray = initTray();
    	    
		final JFrame frame = this;
		
		mediator = new com.seroperson.mediator.Mediator(scale) {
			
			@Override
			public void unMinimize() { 
				super.unMinimize();
				setVisible(true); // TODO in listener?
				setExtendedState(Frame.NORMAL);
			}
			
			@Override
			public void minimize() {
				super.minimize();
				setExtendedState(Frame.ICONIFIED);
			}
			
			@Override
			public void handleThrow(final Throwable t) { 
				mediator.getTimer().cancel();
				frame.dispose();
				
				try {
					new MediatorCrashReporter(t);
				}
				catch (Throwable e) {	}

			}
			
			@Override
			public void handleGlobal(Global g) { 
				tray.displayMessage("tMediator | Ingame broadcast by "+g.getPlayer(), g.getMessage(), MessageType.INFO);
			}

		};		
		 
        setIconImage(icon);
	    setExtendedState(Frame.NORMAL);
	    setUndecorated(true);
	    setFocusable(false);	    
	    
	    JComponent c = (JComponent)getContentPane();  
		LwjglAWTCanvas canvas = new LwjglAWTCanvas(mediator, false);
				
		c.add(canvas.getCanvas());
		c.setBackground(new Color(.9f, .9f, .9f, 1)); // TODO move to mediator
		c.setBorder(new ShapeStrokeBorder(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.5f), crr, new Color(0.7f, 0.7f, 0.7f, 0.7f))); /* borders - brainfuck */

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowStateListener(getWindowListener());
        addWindowListener(getWindowListener(tray));
               
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		location.setLocation(d.width-w, d.height-h-getTaskBarHeight()+4f);
		setSize(w, h);
		setLocation(location);
		setVisible(true);
		setResizable(false);
		setAlwaysOnTop(true);
	}
	
	private int getTaskBarHeight() { 
		Dimension scrnSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		return winSize.y > 0 ? 0 : scrnSize.height - winSize.height;
	}
	
	private WindowAdapter getWindowListener(final TrayIcon trayIcon) { 
		return new WindowAdapter() { 
			@Override
			public void windowClosed(WindowEvent e) { 
				trayIcon.getImage().flush();
				SystemTray.getSystemTray().remove(trayIcon);
				mediator.dispose();
			}
		};
	}
	
	private WindowStateListener getWindowListener() { 
		return new WindowStateListener() {
						
			@Override
			public void windowStateChanged(WindowEvent e) { 
				switch(e.getNewState()) { 
					case(Frame.ICONIFIED): 
						if(!Mediator.isMinimized()) {
							mediator.minimize();
						}
						setVisible(false);
						break;
					case(Frame.NORMAL): 
						setLocation(location);
						if(Mediator.isMinimized()) {
							mediator.unMinimize();
							return;
						}
						break;
				}
			}
						           
        };
	}
	
	private TrayIcon initTray() { 

		icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/skin/icon.png"));
		
        PopupMenu popup = new PopupMenu();     
        
        final MenuItem open = new MenuItem("Open");
        open.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
            	mediator.unMinimize();
            }
        });
        
        final MenuItem settings = new MenuItem("Settings");
        settings.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
            	new com.seroperson.mediator.settings.SettingsSaver(Mediator.getSettings());
            }
        });
                
        final MenuItem globals = new MenuItem("Globals History");
        globals.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
           	
            	int lastIndex = mediator.getLastGlobalIndex()+1;
            	if(lastIndex <= 0) { 
            		JOptionPane.showMessageDialog(getParent(), "Globals is null!");
            		return;
            	}
            	Screen currentScreen = mediator.getScreen();
            	
            	if(!(currentScreen instanceof OnlineList))
            		return;
            	new com.seroperson.mediator.viewer.GlobalsViewer(mediator.getGlobals(), (OnlineList)currentScreen, lastIndex);            
            }
        });
                        
        final CheckboxMenuItem ontop = new CheckboxMenuItem("Over all windows", true);
        ontop.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(isAlwaysOnTop())
					setAlwaysOnTop(false);
				else
					setAlwaysOnTop(true);
			}	
        });
                
        final MenuItem about = new MenuItem("About");
        about.addActionListener(new ActionListener() { 
        	 @Override
 			public void actionPerformed(ActionEvent e) {
        		 new About();
        	}
        });
        
        final MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                Gdx.app.exit();
            }
        });
        
        popup.add(open);
        popup.add(globals);
        popup.add(settings);
        if(isAlwaysOnTopSupported())
        	popup.add(ontop);
        popup.add(about);
        popup.add(exit);
        
        TrayIcon trayIcon = new TrayIcon(icon, "tMediator", popup);
        trayIcon.addMouseListener(new MouseAdapter() { 
        	 public void mouseClicked(MouseEvent e) {
        		 if(e.getClickCount() >= 2)
        			 mediator.unMinimize();
        	 }
        });
        trayIcon.setImageAutoSize(true);
        try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
        
        return trayIcon;
	}
		
	public static void main(String[] a) { 
		new MediatorDesktop();
	}
	
}
