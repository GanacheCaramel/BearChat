package cg.ltenshi.app.social.bearchat.activities;

import android.os.*;
import android.app.*;
import android.content.*;
import android.widget.*;
import android.view.*;
import android.text.*;

import java.util.*;

import org.json.*;
import android.content.pm.*;

import cg.ltenshi.app.social.bearchat.R;
import cg.ltenshi.app.social.bearchat.net.LTCentral;
import cg.ltenshi.app.social.bearchat.materials.LTMessage;
import cg.ltenshi.app.social.bearchat.adapters.MessageAdapter;
import cg.ltenshi.app.social.bearchat.utils.LTenshiTools;
import cg.ltenshi.app.social.bearchat.utils.LTSwipeListener;
import cg.ltenshi.app.social.bearchat.utils.*;
import cg.ltenshi.app.social.bearchat.singleton.*;

public class ChatActivity extends Activity{
	private List<LTMessage> messages = new ArrayList<>();
	private String chatId;
	private String contactName;
	
	private AudioRecorder audioRecorder;
    private AudioPlayer audioPlayer;

    private Button btnRecord, btnPlay;
    private TextView tvStatus, tvTimer;
    private View audioVisualizer;

    private Handler timerHandler = new Handler();
    private int recordingTime = 0;
    private String currentAudioPath;
	
	private LTSwipeListener lTSwipe;
	private LinearLayout messageContainer;
	private ScrollView messageScroll;
	
	private EditText messageInput;
	private ImageButton send_btn;
	
	private LTCentral server;
	private LTenshiTools tools;
	
	private boolean isRecord = false;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		tools = new LTenshiTools(this);
		server = new LTCentral(ChatActivity.this);

		messageContainer = findViewById(R.id.chat_messages_list);
		messageScroll = findViewById(R.id.chat_message_scroll);
		messageInput = findViewById(R.id.chat_message_input);
		send_btn = findViewById(R.id.chat_send_button);
		
		send_btn.setImageResource(R.drawable.ic_speaker);
		send_btn.setTag(R.drawable.ic_speaker);
		
		// Récupérer les données de l'intent
		Intent intent = getIntent();
		contactName = intent.getStringExtra("contact");
		chatId = intent.getStringExtra("chatId");

		// Configurer la vue du contact
		TextView contactView = findViewById(R.id.chat_name);
		contactView.setText(contactName);
		
		loadMessages();
		
		updateButtonState("");
		
		findViewById(R.id.chat_btn_back).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v){ ChatActivity.super.onBackPressed(); } });
		messageInput.addTextChangedListener(new TextWatcher(){
				@Override public void beforeTextChanged(CharSequence text, int p2, int p3, int p4){}
				@Override public void afterTextChanged(Editable p1){}
				@Override public void onTextChanged(CharSequence text, int p2, int p3, int p4){
					updateButtonState(text.toString());
				}
		});
		
		scrollDown();
		setupManagers();
	}
	
	private void updateButtonState(String text){
		String trimmedText = text.trim();
		boolean hasText = !trimmedText.isEmpty();
		
		if(hasText){
			send_btn.setImageDrawable(getDrawable(R.drawable.ic_send));
			findViewById(R.id.chat_send_button).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v){
						sendMessage();
					} });
		}
		
		if( !hasText){
			send_btn.setImageDrawable(getDrawable(R.drawable.ic_speaker));
			findViewById(R.id.chat_send_button).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v){
					if(isRecord){
						send_btn.setImageResource(R.drawable.ic_speaker);
						isRecord = false;
					}else{
						send_btn.setImageResource(R.drawable.ic_camera);
						isRecord = true;
					}
			} });
			
			findViewById(R.id.chat_send_button).setOnLongClickListener(new View.OnLongClickListener(){
				@Override public boolean onLongClick(View view){
					if (isRecord){
						startVoicing();
					}else{
						startRecording();
					}
					return true;
				}
				
				private void startRecording(){
					tools.ltenshiToast("Voice recording...");
				}
				
				private void startVoicing(){
					tools.ltenshiToast("Video note message...");
				}
/*
				private void startRecording(){
					tools.ltenshiToast("Voicing message...");
					RelativeLayout lay_out = findViewById(R.id.chat_message_block);
					LinearLayout lay_in = findViewById(R.id.chat_record_block_unlock);
					
					lay_in.setVisibility(View.VISIBLE);
					lay_out.setVisibility(View.GONE);
				}*/
			});
		}
	}

	private void loadMessages(){
		messages = server.getSampleMessage();
		
		for (LTMessage msg : messages){
			if (msg.getType().equals("true")){
				InflateSentMessages(msg);
			}

			if (msg.getType().equals("false")){
				InflateReceivedMessages(msg);
			}
		}
	}

	private void sendMessage(){
		EditText input = findViewById(R.id.chat_message_input);
		String messageContent = input.getText().toString().trim();
		
		if (!TextUtils.isEmpty(messageContent)){
			
			LTMessage newMessage = new LTMessage(
				chatId,
				"Vous",
				messageContent,
				R.drawable.ic_launcher, 
				tools.getTime(), "sent"
			);
			
			JSONObject jsonObject = new JSONObject();
			try{
				jsonObject.put("sender", "tadashi");
				jsonObject.put("receiver", "admin");
				jsonObject.put("content", messageContent);
				jsonObject.put("time", tools.getTime());
				jsonObject.put("isReceived", false);
				jsonObject.put("isReply", "none");
				jsonObject.put("isRead", false);
			}catch (JSONException e){}
			
			String jsonData = jsonObject.toString();
			server.sendPostRequest("/send_message", jsonData);
			
			Return ltreturn = Return.getInstance();
			String msg_infoJSON = ltreturn.getReturn();
			
			tools.ltenshiToast("[INFOS] [Chat]\n\n" + msg_infoJSON);
			
			// Parser la réponse JSON
			try{ JSONArray array = new JSONArray(msg_infoJSON);
				for(int x = 0; x < array.length(); x++){
					JSONObject item = array.getJSONObject(x);
					boolean msgReceived = item.getBoolean("isReceived");
					boolean msgRead = item.getBoolean("isRead");
					
					newMessage.setStatus(true, msgReceived, msgRead);
				}
			}catch (Exception e){
				tools.ltenshiToast("[ERROR] [HOME]\n" + e);
			}
			
			// Ajouter le message à la liste
			messages.add(newMessage);
			InflateSentMessages(newMessage);
			
			// Effacer le champ de saisie
			input.setText("");
			scrollDown();
		}
	}
	
	private void scrollDown(){
		messageScroll.post(new Runnable(){
				@Override
				public void run(){
					View child = messageScroll.getChildAt(0);
					if (child != null){
						int bottom = child.getBottom() + messageScroll.getPaddingBottom();
						int sy = messageScroll.getScrollY();
						int sh = messageScroll.getHeight();
						int delta = bottom - (sy + sh);

						messageScroll.smoothScrollBy(0, delta);
					}
				}
			});
	}
	
	private void InflateSentMessages(LTMessage msg){

		LayoutInflater inflater = LayoutInflater.from(this);
		View customMessage = inflater.inflate(R.layout.item_message_sent, messageContainer, false);

		TextView msg_content = customMessage.findViewById(R.id.sent_message_content);
		TextView msg_time = customMessage.findViewById(R.id.sent_message_time);
		
		ImageView check0 = customMessage.findViewById(R.id.sent_message_check0);
		ImageView check1 =customMessage.findViewById(R.id.sent_message_check1);
		ImageView check2 = customMessage.findViewById(R.id.sent_message_check2);
		
		if (msg.isSent()){
			check0.setVisibility(View.GONE);
			check1.setVisibility(View.VISIBLE);
			check2.setVisibility(View.GONE);
		}
		
		if(msg.isReceived()){
			check0.setVisibility(View.GONE);
			check1.setVisibility(View.VISIBLE);
			check2.setVisibility(View.VISIBLE);
		}
		
		if(msg.isRead()){
			check0.setVisibility(View.GONE);
			check1.setVisibility(View.VISIBLE);
			check2.setVisibility(View.VISIBLE);
			//check1
			//check2
		}
		
		msg_content.setText(msg.getContent());
		msg_time.setText(msg.dateCreated());
		
		customMessage.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View view){
					PopupMenu popupMenu = new PopupMenu(ChatActivity.this, view);
					MenuInflater inflater = getMenuInflater();
					inflater.inflate(R.menu.msg_menu, popupMenu.getMenu());
					popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem item){
								switch (item.getItemId()) {
									case R.id.msg_menu_op1:
										return true;
									default:
										return false;
								}
							}
						});
					popupMenu.show();
				}
		});
		messageContainer.addView(customMessage);
	}
	
	private void InflateReceivedMessages(LTMessage msg){

		LayoutInflater inflater = LayoutInflater.from(this);
		View customMessage = inflater.inflate(R.layout.item_message_received, messageContainer, false);

		TextView msg_content = customMessage.findViewById(R.id.received_message_content);
		TextView msg_time = customMessage.findViewById(R.id.received_message_time);

		msg_content.setText(msg.getContent());
		msg_time.setText(tools.getTime());
		
		customMessage.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View view){
					PopupMenu popupMenu = new PopupMenu(ChatActivity.this, view);
					MenuInflater inflater = getMenuInflater();
					inflater.inflate(R.menu.msg_menu, popupMenu.getMenu());
					popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem item){
								switch (item.getItemId()) {
									case R.id.msg_menu_op1:
										return true;
									default:
										return false;
								}
							}
						});
					popupMenu.show();
				}
		});
		messageContainer.addView(customMessage);
	}
	
	private void replyToMessage(int position){
		Toast.makeText(this, "Réponse au message " + position, Toast.LENGTH_SHORT).show();
		// Ici tu peux ouvrir un champ de texte pour répondre
		// ou directement envoyer une réponse prédéfinie
	}
	
    private void setupManagers() {
        audioRecorder = new AudioRecorder();
        audioPlayer = new AudioPlayer();
    }

    private void setupClickListeners() {
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecording();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayback();
            }
        });
    }

    private void toggleRecording() {
        if (!audioRecorder.isRecording()) {
            // Démarrer l'enregistrement
            if (checkAudioPermission()) {
                startRecord();
            } else {
                requestAudioPermission();
            }
        } else {
            // Arrêter l'enregistrement
            stopRecording();
        }
    }

    private void startRecord() {
        audioRecorder.startRecording();
        btnRecord.setBackgroundResource(R.drawable.button_play_selector);
        tvStatus.setText("Enregistrement en cours...");
        tvStatus.setTextColor(0xFFFF0000);
        
        // Démarrer le timer
        recordingTime = 0;
        timerHandler.postDelayed(timerRunnable, 1000);
        
        btnPlay.setEnabled(false);
    }

    private void stopRecording() {
        audioRecorder.stopRecording();
        currentAudioPath = audioRecorder.getCurrentFilePath();
        
        btnRecord.setBackgroundResource(R.drawable.button_record_selector);
        tvStatus.setText("Enregistrement terminé");
        tvStatus.setTextColor(0xFF4CAF50);
        
        // Arrêter le timer
        timerHandler.removeCallbacks(timerRunnable);
        tvTimer.setText("00:00");
        
        btnPlay.setEnabled(true);
    }

    private void togglePlayback() {
        if (currentAudioPath != null && !audioPlayer.isPlaying()) {
            audioPlayer.playAudio(currentAudioPath);
            btnPlay.setBackgroundResource(R.drawable.button_play_selector);
            tvStatus.setText("Lecture en cours");
        } else {
            audioPlayer.stopAudio();
            btnPlay.setBackgroundResource(R.drawable.button_play_selector);
            tvStatus.setText("Prêt à lire");
        }
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            recordingTime++;
            int minutes = recordingTime / 60;
            int seconds = recordingTime % 60;
            tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 1000);
        }
    };

    private boolean checkAudioPermission() {
        return checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) 
            == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAudioPermission() {
        requestPermissions(
            new String[]{android.Manifest.permission.RECORD_AUDIO}, 
            123
        );
    }
	/*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioRecorder.isRecording()) {
            audioRecorder.stopRecording();
        }
        audioPlayer.stopAudio();
        timerHandler.removeCallbacks(timerRunnable);
    }*/
}
