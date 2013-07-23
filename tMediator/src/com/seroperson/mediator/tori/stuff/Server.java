package com.seroperson.mediator.tori.stuff;

public class Server {

	private final String desc;
	private final String room;
	private final String mod;
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

	public String getAddress() {
		return new StringBuilder(host).append(":").append(String.valueOf(port)).toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		final Server other = (Server) obj;
		if(room == null) {
			if(other.room != null)
				return false;
		}
		else if(!room.equalsIgnoreCase(other.room))
			return false;
		return true;
	}

}
