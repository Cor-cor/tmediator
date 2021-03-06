package com.seroperson.mediator.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.IntMap;
import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.refresh.RefreshHandler;
import com.seroperson.mediator.refresh.Refresher;
import com.seroperson.mediator.refresh.ServerHandler;
import com.seroperson.mediator.screen.list.AnimatedList;
import com.seroperson.mediator.settings.Settings;
import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.utils.handler.Adder;
import com.seroperson.mediator.utils.handler.ChangeHandler;
import com.seroperson.mediator.utils.handler.Colored;
import com.seroperson.mediator.utils.handler.Remover;
import com.seroperson.mediator.utils.handler.Sorter;
import com.seroperson.mediator.utils.handler.Updater;
import com.seroperson.mediator.viewer.ServerViewer;
import com.seroperson.mediator.viewer.ServerViewerContainer;

public class OnlineList extends MainScreen implements RefreshHandler, ServerViewerContainer {

	private final float speed = 0.5f; // TODO to settings

	private ServerViewer serverviewer;
	private final Stage stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	private final AnimatedList list = new AnimatedList(this);
	private final Sorter sorter = new Sorter(list);
	private final ChangeHandler[] handlers = new ChangeHandler[] { new Remover(list), new Updater(list), new Adder(list), new Colored(list), sorter };
	private final Mediator game = Mediator.getMediator();
	private final Settings settings = game.getSettings();
	private int state = handlers.length;
	private int previousSortingFlag = Integer.MIN_VALUE;
	
	public OnlineList() {
		
		final Skin skin = game.getSkin();
		final ScrollPane scrp = new ScrollPane(list, skin);

		final Button toTray = new Button(skin);
		toTray.add(new Image(game.getRegion("minimize")));
		toTray.addListener(new ChangeListener() {

			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.minimize();
			}
			
		});

		final Button close = new Button(skin);
		close.add(new Image(game.getRegion("close")));
		close.addListener(new ChangeListener() {

			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				Gdx.app.exit();
			}
			
		});

		final TextureRegion rightR = game.getRegion("back");
		final TextureRegion leftR = new TextureRegion(game.getRegion("back"));
		leftR.flip(true, false);

		final Button right = new Button(skin);
		right.add(new Image(rightR));
		right.addListener(new ChangeListener() {

			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				scrp.setScrollX(scrp.getScrollX() - 5f);
			}

		});

		final Button left = new Button(skin);
		left.add(new Image(leftR));
		left.addListener(new ChangeListener() {

			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				scrp.setScrollX(scrp.getScrollX() + 5f);
			}

		});
		
		final Table winbuttons = new Table();
		winbuttons.setFillParent(true);
		final Table bottomRight = new Table();
		bottomRight.setFillParent(true);
		final Table bottomLeft = new Table();
		bottomLeft.setFillParent(true);
		bottomLeft.bottom().right();
		bottomLeft.setName("Bottom left");
		bottomRight.bottom().left();
		bottomRight.add(right).padLeft(settings.getPadLeft() / 2).padBottom(settings.getPadBottom() / 2);
		bottomLeft.add(left).padRight(settings.getPadLeft() / 2).padBottom(settings.getPadBottom() / 2);
		bottomRight.addListener(getFadeTableListener(bottomRight));
		bottomLeft.addListener(getFadeTableListener(bottomLeft));
		bottomRight.setName("Bottom right");

		winbuttons.top().right();
		winbuttons.add(toTray).padTop(5f).padRight(2f);
		winbuttons.add(close).padTop(5f).padRight(5f);
		winbuttons.addListener(getFadeTableListener(winbuttons));
		winbuttons.setName("Window buttons");
						
		scrp.setScrollingDisabled(false, false);
		scrp.setFillParent(true);
		scrp.setFlickScroll(false);
		
		stage.addActor(scrp);
		stage.addActor(bottomLeft);
		stage.addActor(bottomRight);
		stage.addActor(winbuttons);		
	}

	@Override
	public void show() {
		list.addAction(Actions.fadeIn(speed));
	}
	
	@Override
	public ServerHandler initServerHandler() {
		Refresher r = null;
		try {
			r = new Refresher(this, this);
			game.getTimer().schedule(r, 0, settings.getPeriod());
		}
		catch(Throwable e) {
			game.handleThrow(e);
		}
		return r;
	}
	
	@Override
	public InputProcessor getInputProcessor() {
		return new InputMultiplexer(stage, this);
	}

	@Override
	public synchronized void render(final float delta) {
		
		if(state != handlers.length) {
			while(state != handlers.length) {
				if(handlers[state].start()) 
					state++;
				else
					break;
			}
		}
		else {
			try {
				Thread.sleep(20); // TODO to settings
			}
			catch (final InterruptedException e) {
				game.handleThrow(e);
			}
		}
		
		stage.act(delta);
		
		if(game.isMinimized())
			return;		
		
		stage.draw();
		
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	public synchronized void setAnimation(boolean a) { 
		// set speed to 0;
	}
	
	public ServerViewer getServerViewer() {
		return serverviewer;
	}
		
	public void setServerViewer(final ServerViewer serverviewer) {
		this.serverviewer = serverviewer;
	}

	public synchronized void refresh(final List<Player> players) {

		for(final ChangeHandler handler : handlers)
			handler.reset();

		final List<Player> inList = new ArrayList<Player>(list.getLabelMap().keySet());
		final List<Player> toList = new ArrayList<Player>(players);
		final IntMap<Integer> indexes = new IntMap<Integer>();
		final int[] cases = new int[toList.size()];
		boolean mark = false;
		int i = 0;

		sorter.setSort(!(previousSortingFlag == settings.getSortingType()));

		previousSortingFlag = settings.getSortingType();
		
		for(final Player player : toList) {
			int index = -1;
			int i2 = 0;

			for(final Player playerold : inList) {
				if(playerold.getName().equalsIgnoreCase(player.getName())) {
					index = 1;
					indexes.put(i, i2); // TODO refactoring
					break;
				}
				i2++;
			}
			cases[i++] = index;
			if(index == -1) {
				if(settings.getSortingType() < 3)
					sorter.setSort(true);
			}
		}
		i = 0;
		for(final int index : cases) {
			final Player player = toList.get(i);
			switch(index) {
				case 1:
					if(!player.getServer().equals(inList.get(indexes.get(i)).getServer()))
						handlers[State.UPDATING.getIndex()].add(player);
					break;
				case -1:
					if(!mark) {
						mark = true;
						if(settings.isMinimizeAction())
							if(game.isMinimized()) {
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {
										game.unMinimize();
									}
								});

								final TimerTask task = new TimerTask() {

									@Override
									public void run() {
										SwingUtilities.invokeLater(new Runnable() {
											@Override
											public void run() {
												game.minimize();
											} // FIXME
										});

									}
								};

								game.getTimer().schedule(task, 10000); // TODO to settings
							}
					}
					handlers[State.ADDING.getIndex()].add(player);
					handlers[State.COLOR.getIndex()].add(player);
					break;
			}
			i++;
		}

		for(final Player player : inList) {
			if(player == null)
				break;
			boolean cont = false;
			for(final Player playernew : toList) {
				if(player.getName().equalsIgnoreCase(playernew.getName())) {
					cont = true;
					break;
				}
			}
			if(!cont)
				handlers[State.REMOVING.getIndex()].add(player);
		}
		
		state = State.REMOVING.getIndex();

	}

	private InputListener getFadeTableListener(final Table table) {
		table.getColor().a = 0;
		return new InputListener() {

			@Override
			public void enter(final InputEvent event, final float x, final float y, final int pointer, final Actor fromActor) {
				table.addAction(Actions.fadeIn(speed / 2));
			}

			@Override
			public void exit(final InputEvent event, final float x, final float y, final int pointer, final Actor toActor) {
				table.addAction(Actions.fadeOut(speed / 2));
			}

		};
	}	
	
	private enum State {

		REMOVING(0), UPDATING(1), ADDING(2), COLOR(3), SORTING(4);

		int index;

		State(final int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

	}
	
}
