package cg.ltenshi.app.social.bearchat.utils;

import java.io.*;
import android.content.*;
import android.os.*;

public class StorageUtils {
    public static File getAppSpecificStorage(Context context, String folderName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ : utiliser le stockage scopé
            return new File(context.getExternalFilesDir(null), folderName);
        } else {
            // Avant Android 10 : stockage externe classique
            File storageDir = new File(Environment.getExternalStorageDirectory(), 
									   "BearChat/" + folderName);
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            return storageDir;
        }
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static void createFolderStructure(Context context) {
        String[] mainFolders = {"Media", "Data", "Cache"};

        for (String folder : mainFolders) {
            File dir = getAppSpecificStorage(context, folder);
            if (!dir.exists()) {
				dir.mkdirs();
            }
			
            // Sous-dossiers pour Media
			if (folder.equals("Media")) {
                String[] mediaSubfolders = {"Images", "Videos", "Files"};
                for (String subfolder : mediaSubfolders) {
                    File subDir = new File(dir, subfolder);
                    if (!subDir.exists()) {
                        subDir.mkdirs();
                    }
                }
            }
        }
    }
}