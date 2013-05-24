package com.seroperson.mediator.tori.stuff;

public class Server {

	private final String desc;
	private final String room;
	private final String mod;
	private final Player[] players;
	private final String host;
	private final int port;

	public Server(final String desc, final String room,  final String mod, final Player[] players, final String host, final int port) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (room == null ? 0 : room.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Server other = (Server) obj;
		if (room == null) {
			if (other.room != null)
				return false;
		} else if (!room.equalsIgnoreCase(other.room))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder("Description: ").append(desc).append("; Mod: ").append(mod).append("; PlayersCount: ").append(players.length).append("; \n")
				.append("On host ").append(host).append(':').append(port).toString();
	}

}
