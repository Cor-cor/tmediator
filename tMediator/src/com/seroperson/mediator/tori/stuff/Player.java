package com.seroperson.mediator.tori.stuff;

import com.seroperson.mediator.parsing.Parser;

public class Player {

	private final String name;
	private final String clan;
	private Server server;

	public Player(final String name, final String clan, final Server server) {
		this.name = name.trim();
		this.clan = clan.trim();
		this.server = server;
	}

	public String getName() {
		return name;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(final Server server) {
		this.server = server;
	}

	public String getClan() {
		return clan;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
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
		final Player other = (Player) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		return true;
	}

	public static String getNameWithClanTag(final Player player) {
		final StringBuilder pl = new StringBuilder();

		if(player.getClan().equals(Parser.CNONE))
			pl.append(player.getName().trim());
		else
			pl.append("[").append(player.getClan().trim()).append("]").append(player.getName());

		return pl.toString();
	}

}
