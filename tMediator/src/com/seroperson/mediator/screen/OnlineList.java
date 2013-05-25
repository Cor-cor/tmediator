package com.seroperson.mediator.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.esotericsoftware.tablelayout.Cell;
import com.seroperson.mediator.Mediator;
import com.seroperson.mediator.ServerViewer;
import com.seroperson.mediator.tori.stuff.Player;
import static com.seroperson.mediator.Mediator.getSettings;

public class OnlineList extends ScreenAdapter {

	private Player[] inList = new Player[1];

	private final Table winbuttons;
	private final Stage stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	private final Skin skin = new Skin(Gdx.files.internal("skin/skin.json"));

	private final Table main;

	private final ObjectMap<String, Label[]> labels = new ObjectMap<String, Label[]>();
	private ServerViewer serverviewer;

	private final float speed = 0.5f;
	private final Mediator game;

	public OnlineList(final Mediator game) {
		this.game = game;
		main = new Table();

		winbuttons = new Table();
		winbuttons.setFillParent(true);
		winbuttons.top().right();

		final Button toTray = new Button(skin);
		toTray.add(new Image(Mediator.getRegion("minimize")));
		toTray.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.minimize(game);
			}
		});

		final Button close = new Button(skin);
		close.add(new Image(Mediator.getRegion("close")));
		close.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
//				if(siv != null)
//					siv.dispose();
//				Mediator.getRegion("minimize").getTexture().dispose();
//				dispose();
				Gdx.app.exit();
			}
		});

		winbuttons.add(toTray).padTop(5f).padRight(2f);
		winbuttons.add(close).padTop(5f).padRight(5f);
		winbuttons.addListener(new InputListener() {
			@Override
			public void enter (final InputEvent event, final float x, final float y, final int pointer, final Actor fromActor) {
				winbuttons.addAction(Actions.fadeIn(getSpeed()/2));
			}
			@Override
			public void exit (final InputEvent event, final float x, final float y, final int pointer, final Actor toActor) {
				winbuttons.addAction(Actions.fadeOut(getSpeed()/2));
			}

		});
		winbuttons.getColor().a = 0;

		main.padLeft(getSettings().getPadLeft());
		main.padTop(getSettings().getPadBottom());
		main.padRight(getSettings().getPadLeft());
		main.padBottom(getSettings().getPadBottom());
		main.align(Align.left);
		main.left();
		main.top();

		final ScrollPane scrp = new ScrollPane(main, skin);
		scrp.setScrollingDisabled(false, false);
		scrp.setFillParent(true);
		stage.addActor(scrp);
	}

	@Override
	public synchronized void show() {
		main.addAction(Actions.fadeIn(speed));
		stage.addActor(winbuttons);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public synchronized void render(final float delta) {
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	public Game getGame() {
		return game;
	}

	public Player[] getPlayersInList() {
		return inList;
	}

	public ServerViewer getServerViewer() {
		return serverviewer;
	}

	public synchronized void refresh(final Player[] players) {
		final Player[] toList = players;
		final IntMap<Integer> indexes = new IntMap<Integer>();
		final int[] cases = new int[toList.length];
		int i = 0;
		for(final Player player : toList) {
			int index = -1;
			int i2 = 0;
			for(final Player playerold : inList) {
				if(playerold != null && playerold.equals(player)) {
					index = 1;
					indexes.put(i, i2);
					break;
				}
				i2++;
			}
			cases[i++] = index;
		}
		i = 0;
		for(final int index : cases) {
			final Player player = toList[i];
			switch(index) {
				case 1:
					if(!player.getServer().equals(inList[indexes.get(i)].getServer()))
						updateServer(player, getSpeed()/2);
					break;
				case -1:
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
				if(player.equals(playernew)) {
					cont = true;
					break;
				}
			}
			if(!cont)
				removePlayer(player, main);
		}

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

	private void removePlayer(final Player player, final Table table) {
		final Label[] remLabels = labels.get(player.getName());
		for(int i = 0; i < 2; i++) {
			final Label toremove = remLabels[i];
			toremove.addAction(Actions.sequence(Actions.fadeOut(getSpeed()), Actions.run(new Runnable() {
		        @Override
				public void run () {
		        	final Cell<?> cell = table.getCell(toremove);
		        	if (cell != null)
		        		cell.setWidget(null);
					table.removeActor(toremove);
					table.layout();
					table.invalidate();
		        }
			})));
		}
		final Values<Label[]> values = labels.values();
		while(values.hasNext) {
			final Label[] label = values.next();
			if(label[0].getY() < remLabels[0].getY())
				for(int i = 0; i < 2; i++) {
					final Label upordown = label[i];
					upordown.addAction(Actions.moveBy(0, upordown.getHeight(), getSpeed()));
				}
		}
		labels.remove(player.getName());
	}

	private void addNewPlayer(final Player player, final Table table) {
		final Label[] arr = new Label[] { updatePlayer(player), updateServer(player) };
		for(int i = 0; i < 2; i++)
			table.add(arr[i]).align(Align.left);
		table.row();
		labels.put(player.getName(), arr);
	}

	private Label updateServer(final Player player) {
		return updateServer(player, getSpeed());
	}

	private Label updateServer(final Player player, final float speed) {
		return updateLabel(player, 1, new StringBuilder().append(" on ").append(player.getServer().getRoom()).toString(), speed);
	}

	private Label updatePlayer(final Player player) {
		return updateLabel(player, 0, player.getNameWithClanTag(), getSpeed());
	}

	private Label updateLabel(final Player player, final int index, final String text, final float speed) {
		final Label label;

		if(!labels.containsKey(player.getName()))
			label = new Label("", skin);
		else
			label = labels.get(player.getName())[index];

		label.clearListeners();
		label.addListener(getListener(player));
		label.addAction(Actions.sequence(Actions.fadeOut(speed), Actions.run(new Runnable() {
			@Override
			public void run() {
				label.setText(text);
			}
		}), Actions.fadeIn(speed)));

		return label;
	}

	private InputListener getListener(final Player player) {
		final OnlineList ol = this;
		return new InputListener() {
			@Override
			public boolean touchDown (final InputEvent event, final float x, final float y, final int pointer, final int button) {
				if(button == Buttons.RIGHT)
					main.addAction(Actions.run(new Runnable() {
						@Override
						public void run() {
							if(serverviewer == null)
								serverviewer = new ServerViewer(ol);
							serverviewer.add(player.getServer(), player.getServer().getRoom(), true);
						}
					}));
				return false;
			}
		};
	}

	private float getSpeed() {
		return speed;
	}

}
