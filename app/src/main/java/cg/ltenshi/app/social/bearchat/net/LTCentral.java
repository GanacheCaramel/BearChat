package cg.ltenshi.app.social.bearchat.net;

import android.widget.Toast;
import android.app.Activity;

import android.util.Log;
import java.util.ArrayList;

import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.net.HttpURLConnection;
import java.net.URL;

import cg.ltenshi.app.social.bearchat.utils.LTenshiTools;
import cg.ltenshi.app.social.bearchat.singleton.Conserve;
import cg.ltenshi.app.social.bearchat.materials.LTMessage;
import cg.ltenshi.app.social.bearchat.*;
import java.io.*;
import cg.ltenshi.app.social.bearchat.singleton.*;

public class LTCentral{
	private final static String LTXSVR_BOX ="http://192.168.1.67:5000";
	private final static String LTXSVR_SELF="http://192.168.43.117:5000";
	private String TAG = "LTXLG";
	
	private Activity activity;
	private LTenshiTools tools;
	
	private ArrayList<LTMessage> messages;
	private String chatId_sample = "LTID001";
	
	public LTCentral(Activity act){
		this.activity = act; tools = new LTenshiTools( activity );
		this.messages = new ArrayList<>();
	}
	
	public ArrayList<LTMessage>  getSampleMessage(){
		String[] users_pattern = {"Admin", "Admin"};
		String[] msgs = {
			"Bienvenue cher(e) utilisateur(trice) dans BearChat🐻, votre nouvel espace de communication privilégié 100% Africaine conçu pour connecter vos mondes avec simplicité, rapidité et sécurité ; que vos échanges soient personnels ou professionnels, profitez d\'une expérience fluide et intuitive, de fonctionnalités innovantes comme le chiffrement de bout en bout, et d'une communauté bienveillante, le tout dans un écrin épuré qui respecte votre vie privée — nous sommes ravis de vous accompagner dans cette aventure et vous souhaitons d\'excellentes conversations !",
		};
		boolean[] stats = {false, false};
		
		for(int x =0; x < msgs.length; x++){
			messages.add(new LTMessage(
						 chatId_sample,
						 users_pattern[x],
						 msgs[x],
						 R.drawable.ic_launcher,
						 "12:00", stats[x]+""
					 ));
		}
		return messages;
	}
	
	public void sendGetRequest(final String endpoint) {
		// Create a new thread for network operation
		new Thread(new Runnable() {
				@Override
				public void run() {
					HttpURLConnection urlConnection = null;
					BufferedReader reader = null;
					
					try {
						URL url = new URL(LTXSVR_BOX + endpoint);
						urlConnection = (HttpURLConnection) url.openConnection();
						urlConnection.setRequestMethod("GET");
						urlConnection.setConnectTimeout(5000);
						urlConnection.setReadTimeout(5000);
						urlConnection.connect();
						
						// Read the response
						InputStreamReader inputStream = new InputStreamReader(urlConnection.getInputStream());
						reader = new BufferedReader(inputStream);
						final StringBuilder response = new StringBuilder();
						String line;
						
						while ((line = reader.readLine()) != null) {
							response.append(line);
						}
						
						// Switch back to UI thread to use the result
						activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									tools.ltenshiToast("[LTCentral] [GET] Server replied\n\n" + response.toString());
									
									Return ltxreturn = Return.getInstance();
									ltxreturn.set(response.toString());
								}
							});
					} catch (final Exception e) {
						activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									tools.ltenshiToast("[ERROR] [LTCentral] [GET]\n" + e);
									
									Return ltxreturn = Return.getInstance();
									ltxreturn.set("failed");
								}
							});
						Log.e(TAG, "Error during GET request", e);
					} finally {
						if (urlConnection != null) {
							urlConnection.disconnect();
						}
						try {
							if (reader != null) {
								reader.close();
							}
						} catch (Exception e) {
							Log.e(TAG, "Error closing stream", e);
						}
					}
				}
			}).start();
			
	}
	
	public void sendPostRequest(final String endpoint, final String jsonData) {
		new Thread(new Runnable() {
				@Override
				public void run() {
					HttpURLConnection connection = null;
					BufferedReader reader = null;
					final StringBuilder response = new StringBuilder();

					try {
						// Créer l'URL et ouvrir la connexion
						URL url = new URL(LTXSVR_BOX + endpoint);
						connection = (HttpURLConnection) url.openConnection();

						// Configurer la connexion pour POST
						connection.setRequestMethod("POST");
						connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
						connection.setRequestProperty("Accept", "application/json");
						connection.setDoOutput(true);
						connection.setDoInput(true);

						// Désactiver la mise en cache
						connection.setUseCaches(false);

						// Timeouts
						connection.setConnectTimeout(15000); // 15 secondes
						connection.setReadTimeout(15000);    // 15 secondes

						// Envoyer les données JSON
						try (OutputStream os = connection.getOutputStream()) {
							byte[] input = jsonData.getBytes("utf-8");
							os.write(input, 0, input.length);
							os.flush();
						}

						// Lire la réponse
						int responseCode = connection.getResponseCode();

						if (responseCode == HttpURLConnection.HTTP_OK || 
							responseCode == HttpURLConnection.HTTP_CREATED) {

							// Lecture de la réponse en succès
							InputStream inputStream = connection.getInputStream();
							reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

							String line;
							while ((line = reader.readLine()) != null) {
								response.append(line.trim());
							}
							activity.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										tools.ltenshiToast("[LTCentral] [POST] Server Replied");
										
										Return ltxreturn = Return.getInstance();
										ltxreturn.set(response.toString());
									}
								});
						} else {
							// Lecture de l'erreur
							InputStream errorStream = connection.getErrorStream();
							if (errorStream != null) {
								reader = new BufferedReader(new InputStreamReader(errorStream, "utf-8"));
								String line;
								while ((line = reader.readLine()) != null) {
									response.append(line.trim());
								}
							}
						}
					} catch (final Exception e) {
						activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									tools.ltenshiToast("[ERROR] [LTCentral] [POST]\n\n" + e);
									
									Return ltxreturn = Return.getInstance();
									ltxreturn.set("failed");
								}
							});
						e.printStackTrace();
					} finally {
						// Fermer les ressources
						if (reader != null) {
							try {
								reader.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (connection != null) {
							connection.disconnect();
						}
					}
				}
			}).start();
    }
}