package cg.ltenshi.app.social.bearchat.utils;

import android.app.*;
import android.content.*;
import cg.ltenshi.app.social.bearchat.R;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.io.*;

public class LTenshiTools{
	private Activity activity;
	public LTenshiTools(Activity activityExt){ this.activity = activityExt;}
	
	public void ltenshiNotification(String notif_title, String notif_content, int NOTIFICATION_ID){
		Intent enableIntent = new Intent(activity, Notification.class);
		enableIntent.setAction("ACTION_ENABLE");
		PendingIntent enablePendingIntent = PendingIntent.getBroadcast(activity, 0, enableIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
		
		Notification.Builder builder = new Notification.Builder(activity)
			.setSmallIcon(R.drawable.ic_logo)
			.setContentTitle(notif_title)
			.setContentText(notif_content)
			.setPriority(Notification.PRIORITY_DEFAULT)
			.addAction(R.drawable.ic_launcher, "Activer", enablePendingIntent);
		
		NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_ID, builder.build());
	}
	
	public void ltenshiToast(String message){
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast, null);
		ImageView icon = layout.findViewById(R.id.toast_icon);
		TextView text = layout.findViewById(R.id.toast_text);
		icon.setImageResource(R.drawable.ic_launcher);
		text.setText(message);
		Toast toast = new Toast(activity.getApplicationContext());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}
	
	public String loadJSONFromAssets(Context context){
		String json = null;
		try{
			InputStream is = context.getAssets().open("notes/notes.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, "UTF-8");
		} catch(IOException e){ e.printStackTrace(); }
		
		return json;
	}
	
	public String getTime(){
		Calendar calendar = Calendar.getInstance();
		
		// Récupérer les éléments de la date
		int year = calendar.get(Calendar.YEAR);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int mins = calendar.get(Calendar.MINUTE);
		String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
		String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
		String time = String.format("%02d:%02d", hour, mins);
		
		String date_time = dayOfWeek + " " + dayOfMonth + " " + time;
		
		return date_time;
	}
}