package cg.ltenshi.app.social.bearchat.singleton;

public class Conserve{
	private static Conserve instance;
	private String content;
	private boolean isFull= true;
	
	private Conserve() {}
	public static synchronized Conserve getInstance() {
		if (instance == null) { instance = new Conserve(); }
		return instance;
	}
	
	public String get() {
		this.isFull= false;
		return content;
	}
	
	public boolean getStat(){ return this.isFull; }
	public void resetStat(){ this.isFull = true; }
	
	public void put(String newContent) {
		this.content = newContent;
		this.isFull = true;
	}
}