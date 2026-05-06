package cg.ltenshi.app.social.bearchat.activities;

import android.app.*;
import android.os.*;

import android.widget.*;
import android.view.*;
import android.view.View.*;
import android.content.Intent;

import cg.ltenshi.app.social.bearchat.*;
import cg.ltenshi.app.social.bearchat.net.*;
import cg.ltenshi.app.social.bearchat.utils.*;
public class VerificationActivity extends Activity{
	
	//private LTCentral central;
	
	private Button nextButton;
	private EditText codeEditText;
	
	private TextView codeNumber;
	private TextView linkResendMsg;
	
	private LTenshiTools tools;
	
	@Override
	public void onBackPressed(){
		Intent intent = new Intent(VerificationActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verification);
		
		tools = new LTenshiTools(VerificationActivity.this);
		
		codeNumber = findViewById(R.id.verification_codeNumber);
		nextButton = findViewById(R.id.verification_next_button);
		codeEditText = findViewById(R.id.verification_codeEdit);
		
		Boolean extra = getIntent().getExtras().getBoolean("exist");
		String num = getIntent().getExtras().getString("number");
		
		//tools.ltenshiToast("Extra: " + extra);
		
		LinearLayout codeLay = findViewById(R.id.verification_codeLay);
		LinearLayout passLay = findViewById(R.id.verification_passLay);
		
		// Password Mode
		if(extra){
			if(codeLay.getVisibility() == View.VISIBLE){
				codeLay.setVisibility(View.GONE);
				passLay.setVisibility(View.VISIBLE);
			}else{ passLay.setVisibility(View.VISIBLE); }
			
			TextView subt = findViewById(R.id.verification_text1);
			subt.setText("Let's bear friend !!");
			
			nextButton.setOnClickListener( new View.OnClickListener(){
					@Override
					public void onClick(View view){
						String user_pass = codeEditText.getText().toString();
						if (user_pass.equals("admin")){
							goHome();
						}else{
							tools.ltenshiToast("Pass incorrect");
						}
					}
				});
		
		// Code Mode
		}else{
			if(passLay.getVisibility() == View.VISIBLE){
				codeLay.setVisibility(View.VISIBLE);
				passLay.setVisibility(View.GONE);
			}else{ codeLay.setVisibility(View.VISIBLE); }
			
			TextView subt = findViewById(R.id.verification_text1);
			subt.setText("");
			codeNumber.setText(num);
			nextButton.setOnClickListener( new View.OnClickListener(){
					@Override
					public void onClick(View view){
						String user_code = codeEditText.getText().toString().trim();
						if (user_code.equals("0000")){
							goHome();
						}else{
							tools.ltenshiToast("Pass incorrect");
						}
					}
			});
		}
		
	}
	
	private void goHome(){
		Intent intent = new Intent(VerificationActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();
	}
}