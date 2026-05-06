package cg.ltenshi.app.social.bearchat.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import android.view.*;
import android.widget.*;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;

import cg.ltenshi.app.social.bearchat.*;
import cg.ltenshi.app.social.bearchat.dialogs.*;
import cg.ltenshi.app.social.bearchat.widgets.*;
import cg.ltenshi.app.social.bearchat.utils.*;

import java.util.List;
import java.util.ArrayList;

public class LoginActivity extends Activity {
    
	private EditText numberEditTextIndex;
	private EditText numberEditText;
	private Button nextButton;
	private Spinner spin;
	
	private String complete_number_split;
	
	private LTenshiTools tools;
	private List<String> users;
	
	private Boolean extraResult= false;
	
	@Override
	public void onBackPressed(){
		Intent intent = new Intent(LoginActivity.this, SplashEndActivity.class);
		startActivity(intent);
		finish();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
        
		tools = new LTenshiTools(LoginActivity.this);
		users = new ArrayList<>();
		
		// Initialiser les vues
		numberEditTextIndex = findViewById(R.id.number_usernum_index);
		numberEditText = findViewById(R.id.number_usernum);
		nextButton = findViewById(R.id.next_button);
		spin = findViewById(R.id.number_spinner_index);
		
		users.add("+242069815711");
		users.add("+237652026653");
		
		final String[] countries = new String[]{"Cameroon", "Congo, Republic of", "Bénin"};
		final String[] phone_patterns = new String[]{"0 00 00 00 00", "00 000 00 00", "00 00 00 00"};
		
		ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(
			this,
			R.layout.number_spinner_index,
			countries
		);
		
		numberEditText.setOnClickListener( new View.OnClickListener(){
				@Override
				public void onClick(View view){
					
				}
		});
		
		numberEditText.setOnTouchListener( new View.OnTouchListener(){
				@Override
				public boolean onTouch(View p1, MotionEvent p2){
					numberEditText.setHint(phone_patterns[spin.getSelectedItemPosition()]);
					return false;
				}
		});
		
		spin_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(spin_adapter);
		spin.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
				@Override public void onNothingSelected(AdapterView<?> p1){}
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
					numberEditTextIndex.setText("");
					
					switch(position){
						case 0: numberEditTextIndex.setText("+237"); numberEditText.setHint(phone_patterns[position]); nextButton.setTag( phone_patterns[position]); break;
						case 1: numberEditTextIndex.setText("+242"); numberEditText.setHint(phone_patterns[position]); nextButton.setTag( phone_patterns[position]);break;
						case 2: numberEditTextIndex.setText("+229"); numberEditText.setHint(phone_patterns[position]); nextButton.setTag( phone_patterns[position]); break;
					}
				}
			});
			
			//spin.setSelection(0);

        // Gestionnaire de clic pour le bouton de connexion
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = numberEditText.getText().toString();
                String complete_number = numberEditTextIndex.getText().toString() + number;
					complete_number_split = numberEditTextIndex.getText().toString() + " " + number;
				Boolean num_valid = number.length() == (nextButton.getTag().toString().replace(" ", "").length());
				
				if (number.isEmpty()) {
                    Toast.makeText(LoginActivity.this, 
                        "Veuillez remplir tous les champs", 
                        Toast.LENGTH_SHORT).show();
				}else if ( !num_valid ){
					tools.ltenshiToast("Numéro Invalide");
					
                } else {
                    // Simuler la connexion
                    nextButton.setText("CHARGEMENT...");
                    nextButton.setEnabled(false);
                    
                    // Ici, vous ajouteriez votre logique de connexion réelle
                    // Par exemple, une requête API
					
					
					for (String num : users){
						if(num.equals( complete_number )){ extraResult = true; break; }
					}
					// Réactiver le bouton après un délai
					nextButton.postDelayed(new Runnable() {
							@Override
							public void run() {
								nextButton.setText("SUIVANT");
								nextButton.setEnabled(true);
							
								Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
								intent.putExtra("exist", extraResult);
								intent.putExtra("number", complete_number_split);
								extraResult = false;
								startActivity(intent);
								finish();
						}
					}, 2000);
				}
			}
		});
	}
}