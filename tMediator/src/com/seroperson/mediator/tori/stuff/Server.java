package com.seroperson.mediator.tori.stuff;

public class Server {

	private final String desc;
	private final String room;
	private final String mod; // TODO todo
	private final Player[] players;
	private final String host;
	private final int port;

	public Server(final String desc, final String room, final String mod, final Player[] players, final String host, final int port) {
		this.desc = desc;
		this.room = room;
		this.mod = mod;
		this.players = players;
		this.host = host;
		this.port = port;
	}

	public String getDesc() {
		return desc;
	}

	public String getMod() {
		return mod;
	}

	public Player[] getPlayers() {
		return players;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getRoom() {
		return room;
	}

	public String getAdress() {
		return new StringBuilder(host).append(":").append(String.valueOf(port)).toString();
	}

}
