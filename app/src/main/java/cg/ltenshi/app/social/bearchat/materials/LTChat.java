package cg.ltenshi.app.social.bearchat.materials;

import android.app.*;

public class LTChat{
	private String ID;
	private int avatar;
	private String sender;
	private String content;
	
	private boolean received;
	
	private String created;
	
	public LTChat(int avat, String chatID, String sendeR, String prev, String time) {
		this.ID = chatID;
		this.avatar = avat;
		this.sender = sendeR;
		this.content = prev;
		this.created =time;
	}
	
	public void modify(String kontent){
		this.content = kontent;
		this.created += "\n modified";
	}
	
	public void setReceived(boolean rec){ this.received = rec; }
	
	public String getChat_ID(){ return ID; }
	public String getSender() { return sender; }
	public int getProfilePic() { return avatar; }
	public String getContent() { return content; }
	public String dateCreated(){ return created; }
	public boolean isReceived(){ return received; }
	
	public String toString() { return ID + "|" + sender + "|" + content + "|" + avatar+"|" + created; }
	public static LTChat fromString(String noteString) {
		String[] parts = noteString.split("\\|");
		if (parts.length == 5) {
			String chatIdFrom = parts[0];
			String contactFrom = parts[1];
			String contentFrom = parts[2];
			int avatarIdFrom = Integer.parseInt(parts[3]);
			String createdFrom = parts[4];
			
			LTChat noteFrom = new LTChat(avatarIdFrom, chatIdFrom, contactFrom, contentFrom, createdFrom);
			return noteFrom;
		}
		return null;
	}
}