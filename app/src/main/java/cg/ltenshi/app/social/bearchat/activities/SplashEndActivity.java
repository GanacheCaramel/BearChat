package cg.ltenshi.app.social.bearchat.activities;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import cg.ltenshi.app.social.bearchat.*;
import java.util.*;

public class SplashEndActivity extends Activity{
	
	private Handler handler = new Handler();
	
	private Spinner spin;
	private Button lunchBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_end);
		
		spin = findViewById(R.id.splashend_spinner_lang);
		lunchBtn = findViewById(R.id.splashend_lunch_btn);
		
		String [] items ={"Français", "Anglais", "Russe"};
		
		ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(
			this,
			R.layout.number_spinner_index,
			items
		);
		
		spin_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(spin_adapter);
		spin.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
				@Override public void onNothingSelected(AdapterView<?> p1){}
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
					// Here !!
				}
			});
		
		lunchBtn.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View view){
					acTransition();
					lunchBtn.setEnabled(false);
				}
		});
	}
	
	private void acTransition(){
		handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(SplashEndActivity.this, LoginActivity.class);
					startActivity(intent);
					lunchBtn.setEnabled(true);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					finish();
				}
			}, 500);
		
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}