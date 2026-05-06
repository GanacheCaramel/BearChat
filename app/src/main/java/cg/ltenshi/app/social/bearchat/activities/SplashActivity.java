package cg.ltenshi.app.social.bearchat.activities;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

import cg.ltenshi.app.social.bearchat.*;

import java.util.*;

public class SplashActivity extends Activity{
	
	private Handler handler = new Handler();
	private int duration = 5000;
	
	private Random rnd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		rnd = new Random();
		
		acTransition();
	}
	
	private void acTransition(){
		handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(SplashActivity.this, SplashEndActivity.class);
					startActivity(intent);
					finish();
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			}, duration);
		
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}