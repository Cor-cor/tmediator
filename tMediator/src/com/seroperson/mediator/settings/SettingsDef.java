package com.seroperson.mediator.settings;

import com.badlogic.gdx.math.Vector2;

public class SettingsDef {

	public int period = 30000;
	public int port = 22000;
	public String server = "144.76.163.135";
	public String broadcastURL = "http://forum.toribash.com/tori_broadcast.php?format=json&per_page=1";

	public String[] names;
	public String[] clans;
	public String[] rooms;

	public float padLeft = 15/2;
	public float padBottom = 5;
	public int sort = 0;

	public Boolean showlogo = false;
	public Boolean globals = true;
	public Boolean unminimizeonnewplayer = true;
	public Float[] round = new Float[] { 15f, 0f, 0f, 15f };

	public Vector2 position = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
	
}
