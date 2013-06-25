package com.seroperson.mediator.screen;

import static com.seroperson.mediator.Mediator.getSettings;

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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.esotericsoftware.tablelayout.Cell;
import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.tori.stuff.Player;
import com.seroperson.mediator.tori.stuff.Server;
import com.seroperson.mediator.utils.SelectableLabel;
import com.seroperson.mediator.viewer.ServerViewer;

public class OnlineList extends ScreenAdapter {

	private Player[] inList = new Player[1];

	private final Table main;
	private final Table winbuttons;
	private final Stage stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	private final Skin skin = new Skin(Gdx.files.internal("skin/skin.json"));

	private final ObjectMap<String, SelectableLabel[]> labels = new ObjectMap<String, SelectableLabel[]>();
	private final Array<SelectableLabel> colored = new Array<SelectableLabel>();
	private ServerViewer serverviewer;

	private final float speed = 0.5f;
	private final float colorSpeed = 0.0001f;
	private final Mediator game;
	
	private boolean animation = false;
	
	public OnlineList(final Mediator game) {
		this.game = game;
		main = new Table();

		winbuttons = new Table();
		winbuttons.setFillParent(true);
		winbuttons.top().right();

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
				// if(siv != null)
				// siv.dispose();
				// Mediator.getRegion("minimize").getTexture().dispose();
				// dispose();
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

		final Table bottomRight = new Table();
		bottomRight.setFillParent(true);
		final Table bottomLeft = new Table();
		bottomLeft.setFillParent(true);
		bottomLeft.bottom().right();
		bottomRight.bottom().left();
		bottomRight.add(right).padLeft(getSettings().getPadLeft() / 2).padBottom(getSettings().getPadBottom() / 2);
		bottomLeft.add(left).padRight(getSettings().getPadLeft() / 2).padBottom(getSettings().getPadBottom() / 2);
		bottomRight.addListener(getFadeTableListener(bottomRight));
		bottomLeft.addListener(getFadeTableListener(bottomLeft));

		winbuttons.add(toTray).padTop(5f).padRight(2f);
		winbuttons.add(close).padTop(5f).padRight(5f);
		winbuttons.addListener(getFadeTableListener(winbuttons));

		main.padLeft(getSettings().getPadLeft());
		main.padTop(getSettings().getPadBottom());
		main.padRight(getSettings().getPadLeft());
		main.padBottom(getSettings().getPadBottom());
		main.align(Align.left);
		main.left();
		main.top();

		main.addListener(new InputListener() {

			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				final Values<SelectableLabel[]> label = labels.values();
				for(final SelectableLabel[] l : label)
					for(final SelectableLabel lab : l)
						lab.clearSelection();
				return false;
			}

		});

		scrp.setScrollingDisabled(false, false);
		scrp.setFillParent(true);
		scrp.setFlickScroll(false);

		stage.addActor(scrp);
		stage.addActor(bottomLeft);
		stage.addActor(bottomRight);
	}

	@Override
	public synchronized void show() {
		main.addAction(Actions.fadeIn(getSpeed()));
		stage.addActor(winbuttons);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public synchronized void render(final float delta) {
		try { // TODO any alternative 
			if(Mediator.isMinimized()) {
				Thread.sleep(50);
				return;
			}
			if(getActionsSize(stage.getRoot(), 0) == 0 && colored.size == 0) { 
				Thread.sleep(50);
			}
		}
		catch (InterruptedException e) {
			getGame().handleThrow(e);
		}
		
		stage.act(delta);
		stage.draw();

		for(int i = 0; i < colored.size; i++) {
			final SelectableLabel label = colored.get(i);
			if(label.getColor().g > 0) {
				label.getColor().sub(0, colorSpeed, 0, 0);
			}
			else {
				colored.removeValue(label, true);
				i--;
			}
		}

	}
	
	@Override
	public void dispose() {
		stage.dispose();
	}

	public Mediator getGame() {
		return game;
	}

	public Player[] getPlayersInList() {
		return inList;
	}

	public Server[] getServers() {
		return getGame().getServers();
	}

	public ServerViewer getServerViewer() {
		return serverviewer;
	}

	public synchronized void refresh(final Player[] players) {
		final Player[] toList = players;	// TODO collections
		final IntMap<Integer> indexes = new IntMap<Integer>();
		final int[] cases = new int[toList.length];
		boolean mark = false;
		int i = 0;
		
		animation = !Mediator.isMinimized();

		for(final Player player : toList) {
			int index = -1;
			int i2 = 0;
			for(final Player playerold : inList) {
				if(playerold != null && playerold.getName().equalsIgnoreCase(player.getName())) {
					index = 1;
					indexes.put(i, i2);
					break;
				}
				i2++;
			}
			cases[i++] = index;
			if(index == -1 && Mediator.getSettings().isMinimizeAction())
				animation = true;
		}
		i = 0;
		for(final int index : cases) {
			final Player player = toList[i];
			switch(index) {
				case 1:
					if(!player.getServer().getRoom().equalsIgnoreCase(inList[indexes.get(i)].getServer().getRoom()))
						updateServer(player, getSpeed() / 2);
					break;
				case -1:
					if(!mark) {
						mark = true;
						if(Mediator.getSettings().isMinimizeAction())
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
								game.getTimer().schedule(task, 10000); // TODO settings?
	
							}
						}
					addNewPlayer(player, main);
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
				removePlayer(player, main);
		}

		animation = false;
		
		inList = toList;
	}

	public Skin getSkin() {
		return skin;
	}

	public Table getWindowButtons() {
		return winbuttons;
	}

	public void setServerViewer(final ServerViewer serverviewer) {
		this.serverviewer = serverviewer;
	}

	/* incorrect implementation, but it works here */
	private int getActionsSize(Group group, int count) { 
		for(Actor actor : group.getChildren()) { 
			count += actor.getActions().size;
			if(actor instanceof Group) {
				count += getActionsSize((Group) actor, count);
			} 
			if(count > 0)
				return count;
		}
		return count;
	}
	
	private void removePlayer(final Player player, final Table table) {
		final SelectableLabel[] remLabels = labels.get(player.getName());
		labels.remove(player.getName());
		if(remLabels == null)
			return;
		for(int i = 0; i < remLabels.length; i++) {
			
			final SelectableLabel toremove = remLabels[i];
			
			if(toremove == null) {
				continue;
			}
			
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					final Cell<?> cell = table.getCell(toremove);
					if(cell != null)
						cell.setWidget(null);
					table.removeActor(toremove);
					table.layout();
					table.invalidate();
				}
			};
			
			if(!animation)
				runnable.run();
			else
				toremove.addAction(Actions.sequence(Actions.fadeOut(getSpeed()), Actions.run(runnable)));
		}
		final Values<SelectableLabel[]> values = labels.values();
		if(animation) {
			while(values.hasNext) {
				final SelectableLabel[] label = values.next();
				if(label[0].getY() < remLabels[0].getY())
					for(int i = 0; i < 2; i++) {
						final SelectableLabel upordown = label[i];
						upordown.addAction(Actions.moveBy(0, upordown.getHeight(), getSpeed()));
					}
			}
		}
	}

	private void addNewPlayer(final Player player, final Table table) {
		final SelectableLabel[] arr = new SelectableLabel[] { updatePlayer(player), updateServer(player) };
		for(int i = 0; i < 2; i++)
			table.add(arr[i]).align(Align.left);
		table.row();
		labels.put(player.getName(), arr);
	}

	private SelectableLabel updateServer(final Player player) {
		return updateServer(player, getSpeed());
	}

	private SelectableLabel updateServer(final Player player, final float speed) {
		return updateLabel(player, 1, new StringBuilder().append(" on ").append(player.getServer().getRoom()).toString(), speed);
	}

	private SelectableLabel updatePlayer(final Player player) {
		return updateLabel(player, 0, player.getNameWithClanTag(), getSpeed());
	}

	private SelectableLabel updateLabel(final Player player, final int index, final String text, final float speed) {
		final SelectableLabel field;

		if(!labels.containsKey(player.getName())) {
			field = new SelectableLabel("", skin);
			if(animation) {
				field.setColor(Color.GREEN);
				field.getColor().g = 0.7f;
				colored.add(field);
			}
			else
				field.setColor(Color.BLACK);
		}
		else
			field = labels.get(player.getName())[index];

		field.clearListeners();
		field.addListener(field.getDefaultListener());
		field.addListener(getListener(player));
		
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				field.setText(text);
			}
		};
		
		if(animation)
			field.addAction(Actions.sequence(Actions.fadeOut(getSpeed()), Actions.run(runnable), Actions.fadeIn(getSpeed())));
		else
			runnable.run();

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

	private float getSpeed() {
		return speed;
	}

}
