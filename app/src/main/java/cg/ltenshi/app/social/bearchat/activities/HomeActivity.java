package cg.ltenshi.app.social.bearchat.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import android.content.*;
import android.view.*;
import android.widget.*;

import android.widget.SearchView.*;
import android.util.*;
import android.hardware.*;

import org.json.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.time.*;

import cg.ltenshi.app.social.bearchat.R;
import cg.ltenshi.app.social.bearchat.adapters.ChatAdapter;
import cg.ltenshi.app.social.bearchat.materials.LTChat;
import cg.ltenshi.app.social.bearchat.utils.LTenshiTools;
import cg.ltenshi.app.social.bearchat.utils.StorageUtils;
import cg.ltenshi.app.social.bearchat.net.LTCentral;
import cg.ltenshi.app.social.bearchat.singleton.Return;
import cg.ltenshi.app.social.bearchat.storage.LocalStorage;
import cg.ltenshi.app.social.bearchat.net.background.*;

public class HomeActivity extends Activity{
	
	// Chats vars
	public ListView chatList;
	private ChatAdapter adapter;
	public static List<LTChat> chats = new ArrayList<>();
	private boolean selectionMode = false;
	
	// Tools vars
	private LTenshiTools tools;
	
	// Shake Vars
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private SensorEventListener sensorListner;
	private static final int SHAKE_THRESHOLD = 500;
	private long lastUpdate = 0;
	private float last_x;
	private float last_y;
	
	// Storage Vars
	private StorageUtils storage;
	protected LocalStorage store;
	
	//Preferences vars
	private String USER_INFOS = "[USER]";
	private String APP_DATA = "[APP]";
	
	// Server Vars
	private Handler checkLoop;
	
	@Override public void onBackPressed(){
		if (selectionMode){ exitSelectionMode(); updateActionMode(selectionMode); }
		else{ super.onBackPressed(); }
	}
	
	@Override protected void onResume(){
		super.onResume();
		sensorManager.registerListener(sensorListner, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override protected void onPause(){
		super.onPause();
		sensorManager.unregisterListener(sensorListner);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		if(storage.isExternalStorageWritable()){
			storage.createFolderStructure(HomeActivity.this);
		}
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		sensorListner = new SensorEventListener(){
			@Override public void onSensorChanged(SensorEvent event){
				long curTime = System.currentTimeMillis();
				
				if(curTime - lastUpdate >200){
					long diffTime = (curTime - lastUpdate);
					lastUpdate = curTime;
					
					float x = event.values[0];
					float y = event.values[1];
					
					float speed = Math.abs(x - last_x) / diffTime * 10000;
					
					if(speed > SHAKE_THRESHOLD && Math.abs(x - last_x) > -Math.abs(x - last_x)  ){
						if(!selectionMode){
							tools.ltenshiToast("Secousses détecté !!");
						}else {
							tools.ltenshiToast("Selection Dynamique !!");
						}
					}
					
					last_x = x;
					last_y = y;
				}
			}
			@Override public void onAccuracyChanged(Sensor p1, int p2){}
		};
		
		tools = new LTenshiTools(HomeActivity.this);
		
		chatList = findViewById(R.id.home_conversations_list);
		chatList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		adapter = new ChatAdapter(this, chats);
		chatList.setAdapter(adapter);
		
		chats.add(new LTChat(R.drawable.ic_logo, "ID3456", "Admin", "Here we go...", "") );
		chats.add(new LTChat(R.drawable.ic_logo, "ID3457", "Admin 2", "Here we go again...", "") );
		chats.add(new LTChat(R.drawable.ic_logo, "ID3457", "Mom", "Don't forget your med...", "") );
		adapter.notifyDataSetChanged();
		
		findViewById(R.id.home_camera_btn).setOnClickListener( new View.OnClickListener(){
				@Override public void onClick(View view){
					Intent intent = new Intent(HomeActivity.this, CameraActivity.class);
					startActivity(intent);
				}
		});
		
		chatList.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> p1, View view, int position, long id){
					if (!selectionMode){
						selectionMode = true;
						adapter.toggleSelection(position);
						updateActionMode(true);
					}
					return true;
				}
			});
			
        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					
					if(selectionMode){
						adapter.toggleSelection(position);
						updateActionMode(true);
						if(adapter.getSelectedCount() == 0){
							selectionMode = false;
							updateActionMode(false);
						}
					}else{
						Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
						intent.putExtra("contact", chats.get(position).getSender());
						intent.putExtra("chatId", chats.get(position).getChat_ID() );
						startActivity(intent);
					}
				}
			});
			
			findViewById(R.id.home_newChat_btn).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View view){}
			});
			
		checkLoop = new Handler();
		checkLoop.postDelayed( new Runnable(){
				@Override public void run(){
					//checkMessage();
					checkLoop.postDelayed(this, 2225000);
				}
			}, 2225000);
	}
	
	/************
	 *	LOAD MESSSAGE
	 *********************/
	protected List<LTChat> loadMessage(){
		List<LTChat> loaded_chats = new ArrayList<>();
		/*
		LocalStorage storage = new LocalStorage();
		loaded_chats = storage.getAllChats();
		*/
		return loaded_chats;
	}
	
	/******************
	*	SELECTION LOGIC
	****************************/
	private void updateActionMode(boolean SelectionMode){
		
		if(!SelectionMode){
			FrameLayout lay = findViewById(R.id.chat_banner_select);
			FrameLayout layt = findViewById(R.id.home_banner_);
			
			lay.setVisibility(View.GONE);
			layt.setVisibility(View.VISIBLE);
		}else{
			FrameLayout lay = findViewById(R.id.chat_banner_select);
			FrameLayout layt = findViewById(R.id.home_banner_);
			
			lay.setVisibility(View.VISIBLE);
			layt.setVisibility(View.GONE);
			
			TextView selection_nbr = findViewById(R.id.selection_number);
			selection_nbr.setText(getSelectedCount()+"");
			
			ImageButton exitModeBtn = findViewById(R.id.selection_btn_close);
			exitModeBtn.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View view){
						exitSelectionMode();
						updateActionMode(selectionMode);
					}
				});
		}
	}
	
	private void exitSelectionMode(){
		selectionMode = false;
		adapter.clearSelection();
	}
	
	private int getSelectedCount(){ return adapter.getSelectedCount(); }

	@Override protected void onDestroy(){
		if(checkLoop != null){ checkLoop.removeCallbacksAndMessages(null); }
		super.onDestroy();
	}
}
