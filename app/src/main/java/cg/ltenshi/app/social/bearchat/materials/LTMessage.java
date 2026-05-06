package cg.ltenshi.app.social.bearchat.materials;

import android.app.*;
import android.graphics.*;

public class LTMessage{
	private String ID;
	private int avatar;
	private String sender;
	private String content;
	private String type;
	
	private boolean received= false;
	private boolean read = false;
	private boolean sent= false;
	
	private String created;
	
	public LTMessage( String chatID, String sendeR, String contenT, int avat, String time, String type) {
		this.ID = chatID;
		this.avatar = avat;
		this.sender = sendeR;
		this.content = contenT;
		this.created =time;
		this.type = type;
	}
	
	public void modify(String kontent){
		this.content = kontent;
		this.created += "\n modified";
	}
	
	public void setStatus(boolean stat1, boolean stat2, boolean stat3){
		this.sent = stat1;
		this.received = stat2;
		this.read = stat3;
	}
	
	public boolean isSent(){ return sent; }
	public boolean isReceived(){ return received; }
	public boolean isRead(){ return read; }
	
	public int getProfilePic() { return avatar; }
	public String getChat_ID(){ return ID; }
	public String getSender() { return sender; }
	public String getContent() { return content; }
	public String getType(){ return type; }
	
	public String dateCreated(){ return created; }
	
	public String toString() { return ID + "|" + sender + "|" + content + "|" + avatar+"|" + created + "|" + type; }
	public static LTMessage fromString(String noteString) {
		String[] parts = noteString.split("\\|");
		if (parts.length == 5) {
			String chatIdFrom = parts[0];
			String contactFrom = parts[1];
			String contentFrom = parts[2];
			int avatarIdFrom = Integer.parseInt(parts[3]);
			String createdFrom = parts[4];
			String typeM = parts[5];
			
			LTMessage noteFrom = new LTMessage(chatIdFrom, contactFrom, contentFrom, avatarIdFrom, createdFrom, typeM);
			return noteFrom;
		}
		return null;
	}
}