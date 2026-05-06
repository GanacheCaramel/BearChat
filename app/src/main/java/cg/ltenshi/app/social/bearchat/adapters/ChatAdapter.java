package cg.ltenshi.app.social.bearchat.adapters;

import android.widget.*;
import android.content.*;
import android.view.*;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.widget.LinearLayout;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Activity;

import java.util.List;
import java.util.ArrayList;
import android.util.SparseBooleanArray;
import android.graphics.Color;

import cg.ltenshi.app.social.bearchat.R;
import cg.ltenshi.app.social.bearchat.materials.LTChat;

import android.util.*;
import android.content.res.*;

public class ChatAdapter extends ArrayAdapter<LTChat> {
	private Context context;
	private List<LTChat> messages;
	private static final int PREVIEW_DIVIDER = 15;
	private final String focus_color = "#B1C7D1";
	
	private SparseBooleanArray selectedItems;
	private boolean selectionMode = false;
	
	public ChatAdapter(Context context, List<LTChat> messages) {
		super(context, 0, messages);
		this.context = context;
		this.messages = messages;
		this.selectedItems = new SparseBooleanArray();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.chat_layout, parent, false);
		}
		
		// Récupérer les vues de l'item
		ImageView user_pic = convertView.findViewById(R.id.chat_pic);
		
		TextView nameTextView = convertView.findViewById(R.id.messageTitle);
		TextView messageTextView = convertView.findViewById(R.id.messageContent);
		TextView dateNote = convertView.findViewById(R.id.messageTime);
		
		// Récupérer l'élément Message actuel
		LTChat currentNote = getItem(position);
		
		String message = currentNote.getContent();
		if (message.length() > PREVIEW_DIVIDER) {
			message = message.substring(0, PREVIEW_DIVIDER) + "...";
		}

		String date_note = currentNote.dateCreated();
		
		// Remplir les vues avec les données de l'élément
		nameTextView.setText(currentNote.getSender());
		messageTextView.setText(message);
		dateNote.setText(date_note);
		
		if (selectedItems.get(position, false)) { convertView.setBackgroundColor( Color.parseColor(focus_color));
		}else{ convertView.setBackgroundColor(Color.TRANSPARENT); }
		
		return convertView;
	}
	
	// Méthodes pour gérer la sélection
	public void toggleSelection(int position) {
		if (selectedItems.get(position, false)) { selectedItems.delete(position); }
		else { selectedItems.put(position, true); }
		
		selectionMode = selectedItems.size() > 0;
		notifyDataSetChanged();
	}
	
	public void clearSelection() {
		selectedItems.clear();
		selectionMode = false;
		notifyDataSetChanged();
	}
	
	public int getSelectedCount() { return selectedItems.size(); }
	public SparseBooleanArray getSelectedItems() { return selectedItems; }
	
	public List<Integer> getSelectedPositions() {
		List<Integer> positions = new ArrayList<>();
		for (int i = 0; i < selectedItems.size(); i++) {
			positions.add(selectedItems.keyAt(i));
		}
		return positions;
	}
	
	public List<LTChat> getSelectedChats() {
		List<LTChat> selectedMessages = new ArrayList<>();
		for (int i = 0; i < selectedItems.size(); i++) {
			int position = selectedItems.keyAt(i);
			selectedMessages.add(getItem(position));
		}
		return selectedMessages;
	}
	
	public boolean isSelectionMode() { return selectionMode; }
}