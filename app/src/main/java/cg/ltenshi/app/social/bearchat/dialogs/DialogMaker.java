package cg.ltenshi.app.social.bearchat.dialogs;

import android.app.*;
import android.content.*;

import cg.ltenshi.app.social.bearchat.*;

public class DialogMaker{
	private Activity activity;
	private AlertDialog.Builder builder;
	
	private AlertDialog dialog;
	
	public DialogMaker(Activity act){
		activity = act;
		builder = new AlertDialog.Builder(activity);
	}
	
	public void init(){
		builder
			.setIcon(R.drawable.ic_logo)
			.setTitle("Alert !!")
			.setMessage("Alert Dialog App !!")
			.setCancelable(false);
	}
	
	public void init(int iconID, String title, String message, boolean cancelBool){
		builder
			.setIcon(iconID)
			.setTitle(title)
			.setMessage(message)
			.setCancelable(cancelBool);
	}
	
	public AlertDialog.Builder getBuilder(){ return builder; }
	
	public void end(){
		dialog = builder.create();
		dialog.show();
	}
	
}