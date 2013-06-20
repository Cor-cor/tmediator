package com.seroperson.mediator.tori.stuff;

import java.util.Date;

public class Global {

	private final String message;
	private final String server;
	private final String player;
	private final Date date;

	public Global(final String message, final Date date, final String server, final String player) {
		this.message = message;
		this.date = date;
		this.server = server;
		this.player = player;
	}

	public String getMessage() {
		return message;
	}

	public String getServer() {
		return server;
	}

	public String getPlayer() {
		return player;
	}

	public Date getDate() {
		return date;
	}

}
