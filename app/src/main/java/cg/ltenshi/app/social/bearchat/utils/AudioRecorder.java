package cg.ltenshi.app.social.bearchat.utils;

import android.media.MediaRecorder;
import android.os.Environment;
import java.io.File;
import java.io.IOException;

public class AudioRecorder{
    private MediaRecorder mediaRecorder;
    private String currentFilePath;
    private boolean isRecording = false;

    public void startRecording() {
        try {
            // Créer le fichier audio
            String fileName = "audio_" + System.currentTimeMillis() + ".3gp";
            File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC
            );
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            currentFilePath = storageDir.getAbsolutePath() + "/" + fileName;

            // Configurer MediaRecorder
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(currentFilePath);

            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        if (mediaRecorder != null && isRecording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        }
    }

    public String getCurrentFilePath() {
        return currentFilePath;
    }

    public boolean isRecording() {
        return isRecording;
    }
}