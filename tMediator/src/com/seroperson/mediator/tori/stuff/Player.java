package com.seroperson.mediator.tori.stuff;

import com.seroperson.mediator.parsing.Parser;

public class Player {

	private final String name;
	private final String clan;
	private final char[] clanlimiters;
	private String namewithclantag;
	private String nameforsorting;
	private Server server;

	public Player(final String name, final String clan, final Server server, char[] clanlimiters) {
		this.name = name.trim();
		this.clan = clan.trim();
		this.server = server;
		this.clanlimiters = clanlimiters;
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
		
	public char[] getClanLimiters() {
		return clanlimiters;
	}
	
	public String getClan() {
		return clan;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getNameWithClanTag() {
		if(namewithclantag != null)
			return namewithclantag;
		final char[] climiters = getClanLimiters();
		final StringBuilder pl = new StringBuilder();

		if(getClan() == Parser.CNONE)
			pl.append(getName());
		else
			pl.append(climiters[0]).append(getClan()).append(climiters[1]).append(' ').append(getName());
		
		return namewithclantag = pl.toString();
	}
	
	public String getNameForSorting() { 
		if(nameforsorting != null)
			return nameforsorting;
		return nameforsorting = new StringBuilder(getClan() == Parser.CNONE ? String.valueOf(Character.MAX_VALUE) : getClan()).append(' ').append(getName()).toString();
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
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		final Player other = (Player) obj;
		if(name == null) {
			if(other.name != null)
				return false;
		}
		else if(!name.equalsIgnoreCase(other.name))
			return false;
		return true;
	}

}
