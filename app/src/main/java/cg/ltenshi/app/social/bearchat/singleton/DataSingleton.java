package cg.ltenshi.app.social.bearchat.singleton;

import cg.ltenshi.app.social.bearchat.materials.LTChat;

public class DataSingleton {
	private static DataSingleton instance;
	private LTChat chat;
	private boolean setted= true;
	private DataSingleton() {}
	
	public static synchronized DataSingleton getInstance() {
		if (instance == null) {
			instance = new DataSingleton();
		}
		return instance;
	}
		
	public LTChat getChat() {
		this.setted= false;
		return chat;
	}
	
	public boolean getStat(){ return this.setted; }
	public void resetStat(){ this.setted = true; }
	
	public void setNote(LTChat newChat) {
		this.chat = newChat;
		this.setted = true;
	}
}