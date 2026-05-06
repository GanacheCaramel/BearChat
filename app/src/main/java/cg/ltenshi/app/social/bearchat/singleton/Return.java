package cg.ltenshi.app.social.bearchat.singleton;

public class Return{
	private static Return instance;
	private String content;
	private boolean isReturn= true;
	
	private Return() {}
	public static synchronized Return getInstance() {
		if (instance == null) { instance = new Return(); }
		return instance;
	}
	
	public String getReturn() {
		this.isReturn= false;
		return content;
	}
	
	public boolean isReturn(){ return this.isReturn; }
	public void reset(){ this.isReturn = false; this.content =""; }
	
	public void set(String newReturn) {
		this.content = newReturn;
		this.isReturn = true;
	}
}