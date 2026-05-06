package cg.ltenshi.app.social.bearchat.utils;

import android.media.MediaPlayer;
import java.io.IOException;

public class AudioPlayer {
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    public void playAudio(String filePath) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						isPlaying = false;
					}
				});

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopAudio() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}