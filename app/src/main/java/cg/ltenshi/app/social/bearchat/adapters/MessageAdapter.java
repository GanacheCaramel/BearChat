package cg.ltenshi.app.social.bearchat.adapters;

import android.widget.*;
import android.content.*;
import android.view.*;

import java.util.*;

import cg.ltenshi.app.social.bearchat.R;
import cg.ltenshi.app.social.bearchat.materials.LTMessage;

public class MessageAdapter extends ArrayAdapter<LTMessage> {
	private Context context;
	private List<LTMessage> notes;
	
	private static final int PREVIEW_DIVIDER= 15;
	
	public MessageAdapter(Context context, List<LTMessage> notes) {
		super(context, 0, notes);
		this.context = context;
		this.notes = notes;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.item_message_sent, parent, false);
		}
		
		// Récupérer les vues de l'item
		TextView messageTextView = convertView.findViewById(R.id.sent_message_content);
		TextView dateMessage = convertView.findViewById(R.id.sent_message_time);
		
		// Récupérer l'élément Message actuel
		LTMessage currentMessage = getItem(position);
		String message_cntnt = currentMessage.getContent();
		String date_message = currentMessage.dateCreated();
		
		// Remplir les vues avec les données de l'élément
		messageTextView.setText(message_cntnt);
		dateMessage.setText(date_message);
		
		return convertView;
	}
}
