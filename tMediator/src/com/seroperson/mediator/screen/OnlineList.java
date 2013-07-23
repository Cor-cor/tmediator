package com.seroperson.mediator.screen;

import static com.seroperson.mediator.Mediator.getSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.tori.stuff.Server;
import com.seroperson.mediator.utils.SelectableLabel;
import com.seroperson.mediator.utils.SelectableLabelGroup;
import com.seroperson.mediator.utils.handler.Adder;
import com.seroperson.mediator.utils.handler.ChangeHandler;
import com.seroperson.mediator.utils.handler.Remover;
import com.seroperson.mediator.utils.handler.Sorter;
import com.seroperson.mediator.utils.handler.Updater;
import com.seroperson.mediator.viewer.ServerViewer;

public class OnlineList extends ScreenAdapter {

	private List<Player> inList = new ArrayList<Player>();

	private final float speed = 0.5f;
	private final float colorSpeed = Mediator.isDebug() ? 1 : 0.15f;

	private final Table main;
	private final Mediator game;
	private final Stage stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	private final Skin skin = new Skin(Gdx.files.internal("skin/skin.json"));
	private final Map<Player, Table> labels;
	private final Array<SelectableLabel> colored = new Array<SelectableLabel>();
	private final SelectableLabelGroup group = new SelectableLabelGroup();
	private ServerViewer serverviewer;

	private final ChangeHandler[] handlers = new ChangeHandler[] { new Remover(this), new Updater(this), new Adder(this), new Sorter(this) };
	private int state = handlers.length;

	private boolean animation = false;
	private boolean sort = false;
	
	public OnlineList(final Mediator game) {
		this.game = game;

		labels = new HashMap<Player, Table>();
		main = new Table();
		
		final ScrollPane scrp = new ScrollPane(main, skin);

		final Button toTray = new Button(skin);
		toTray.add(new Image(Mediator.getRegion("minimize")));
		toTray.addListener(new ChangeListener() {

			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.minimize();
			}
		});

		final Button close = new Button(skin);
		close.add(new Image(Mediator.getRegion("close")));
		close.addListener(new ChangeListener() {

			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				Gdx.app.exit();
			}
		});

		final TextureRegion rightR = Mediator.getRegion("back");
		final TextureRegion leftR = new TextureRegion(Mediator.getRegion("back"));
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
		bottomRight.add(right).padLeft(getSettings().getPadLeft() / 2).padBottom(getSettings().getPadBottom() / 2);
		bottomLeft.add(left).padRight(getSettings().getPadLeft() / 2).padBottom(getSettings().getPadBottom() / 2);
		bottomRight.addListener(getFadeTableListener(bottomRight));
		bottomLeft.addListener(getFadeTableListener(bottomLeft));
		bottomRight.setName("Bottom right");

		winbuttons.top().right();
		winbuttons.add(toTray).padTop(5f).padRight(2f);
		winbuttons.add(close).padTop(5f).padRight(5f);
		winbuttons.addListener(getFadeTableListener(winbuttons));
		winbuttons.setName("Window buttons");
		
		main.padLeft(getSettings().getPadLeft());
		main.padTop(getSettings().getPadBottom());
		main.padRight(getSettings().getPadLeft());
		main.padBottom(getSettings().getPadBottom());
		main.align(Align.left);
		main.left();
		main.top();
		
		main.setName("Main table");
		
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
		main.addAction(Actions.fadeIn(getSpeed()));
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public synchronized void render(final float delta) {
		
		/* TODO as actions? */
		for(int i = 0; i < colored.size; i++) {
			final SelectableLabel label = colored.get(i);
			if(label.getColor().g > 0) 
				label.getColor().sub(0, delta*colorSpeed, 0, 0);
			
			if(label.getColor().g <= 0) {
				label.getColor().set(Color.BLACK);
				colored.removeValue(label, true);
				i--;
			}
		}
		
		if(state != handlers.length) {
			while(state <= handlers.length-1) 
				if(handlers[state].start()) {
					state++;
					main.layout();
				}
				else
					break;
		}
		else {
			try {
				Thread.sleep(20);
			}
			catch (final InterruptedException e) {
				getGame().handleThrow(e);
			}
		}
		
		stage.act(delta);
		
		if(Mediator.isMinimized())
			return;		
		
		stage.draw();
		
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	public int getColoredLabelsSize() {
		return colored.size;
	}
	
	public int getState() {
		return state;
	}

	public int getActionsSize() {
		return getActionsSize(stage.getRoot(), 0);
	}
	
	public boolean isAnimated() {
		return animation;
	}

	public synchronized void setAnimation(boolean a) { 
		animation = a;
	}
	
	public boolean needToSort() { 
		return sort;
	}
	
	public Map<Player, Table> getLabelMap() {
		return labels;
	}

	public float getSpeed() {
		return speed;
	}
	
	public Mediator getGame() {
		return game;
	}

	public List<Player> getPlayersInList() {
		return inList;
	}

	public Server[] getServers() {
		return getGame().getServers();
	}

	public ServerViewer getServerViewer() {
		return serverviewer;
	}
	
	public Table getMainTable() {
		return main;
	}
	
	public void setServerViewer(final ServerViewer serverviewer) {
		this.serverviewer = serverviewer;
	}

	public synchronized void refresh(final List<Player> players) {

		for(final ChangeHandler handler : handlers)
			handler.reset();

		final List<Player> toList = new ArrayList<Player>(players);
		final IntMap<Integer> indexes = new IntMap<Integer>();
		final int[] cases = new int[toList.size()];
		boolean mark = false;
		int i = 0;

		sort = false;
		animation = !Mediator.isMinimized();

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
				if(getSettings().getSortingType() < 3)
					sort = true;
				if(getSettings().isMinimizeAction())
					animation = true;
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
						if(getSettings().isMinimizeAction())
							if(Mediator.isMinimized()) {

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
											} // FIXME it really need here?
										});

									}
								};

								game.getTimer().schedule(task, 10000);

							}
					}
					handlers[State.ADDING.getIndex()].add(player);
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

		inList = toList;
	}

	public SelectableLabel updateServer(final Player player) {
		return updateLabel(player, 1, new StringBuilder().append(" on ").append(player.getServer().getRoom()).toString(), getSpeed(), null);
	}

	public SelectableLabel updatePlayer(final Player player) {
		return updateLabel(player, 0, player.getNameWithClanTag(), getSpeed(), null);
	}

	private SelectableLabel updateLabel(final Player player, final int index, final String text, final float speed, final Runnable finish) {
		final SelectableLabel field;

		if(!labels.containsKey(player)) {
			final Table current = new Table();
			current.setName(player.getName()); 
			labels.put(player, current);

			for(int i = 0; i < 2; i++) {
				final SelectableLabel label = new SelectableLabel("", skin, group);
				if(animation) {
					label.setColor(Color.GREEN);
					label.getColor().g = 0.7f;
					colored.add(label);
				}
				else
					label.setColor(Color.BLACK);
				current.add(label).align(Align.left);
			}
		}

		field = (SelectableLabel)labels.get(player).getChildren().get(index);

		field.clearListeners();
		field.addListener(field.getDefaultClickListener());
		if(index == 1)
			field.addListener(getListener(player));

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				field.setText(text);
			}
		};

		if(!animation) {
			runnable.run();
			if(finish != null)
				finish.run();
		}
		else {
			final SequenceAction sequence = Actions.sequence(Actions.fadeOut(getSpeed()), Actions.run(runnable), Actions.fadeIn(getSpeed()));
			if(finish != null)
				sequence.addAction(Actions.run(finish));
			field.addAction(sequence);
		}

		return field;
	}

	private InputListener getListener(final Player player) {
		return new ServerInputListener(this, player);
	}

	private InputListener getFadeTableListener(final Table table) {
		table.getColor().a = 0;
		return new InputListener() {

			@Override
			public void enter(final InputEvent event, final float x, final float y, final int pointer, final Actor fromActor) {
				table.addAction(Actions.fadeIn(getSpeed() / 2));
			}

			@Override
			public void exit(final InputEvent event, final float x, final float y, final int pointer, final Actor toActor) {
				table.addAction(Actions.fadeOut(getSpeed() / 2));
			}

		};
	}

	/* incorrect implementation, but it works here */
	private int getActionsSize(final Group group, int count) {
		for(final Actor actor : group.getChildren()) {
			count += actor.getActions().size;
			if(actor instanceof Group) {
				count += getActionsSize((Group) actor, count);
			}
			if(count > 0)
				return count;
		}
		return count;
	}
	
	private enum State {

		REMOVING(0), UPDATING(1), ADDING(2), SORTING(3), NOTHING(4);

		int index;

		State(final int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

	}

}
