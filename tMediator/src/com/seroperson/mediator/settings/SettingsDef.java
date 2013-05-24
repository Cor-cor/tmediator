package com.seroperson.mediator.settings;

import com.badlogic.gdx.Gdx;

public class SettingsDef {

	public int period = 30000;
	public int port = 22000;
	public String server = "176.9.64.22";

	public String[] names;
	public String[] clans;

	public float padLeft = Gdx.graphics.getWidth()/12;
	public float padTop = Gdx.graphics.getHeight()/20;
	public float padDown = Gdx.graphics.getHeight()/32;

}
