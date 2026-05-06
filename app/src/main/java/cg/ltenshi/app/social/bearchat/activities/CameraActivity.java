package cg.ltenshi.app.social.bearchat.activities;

import android.app.*;
import android.os.*;
import android.content.*;
import android.widget.*;
import android.view.*;
import android.hardware.*;
import android.content.pm.*;

import java.util.*;
import java.io.*;
import java.text.*;

import cg.ltenshi.app.social.bearchat.utils.LTenshiTools;
import cg.ltenshi.app.social.bearchat.R;
import android.media.*;

public class CameraActivity extends Activity implements SurfaceHolder.Callback {
    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ImageButton btnCapture, btnBack;
	
    private boolean isCameraRunning = false;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
	
	private GestureDetector gestureDetector;

	@Override
	public void onBackPressed(){
		super.onBackPressed();
	}
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
		
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		//checkCameraPermission();
		
		gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
				@Override
				public boolean onDoubleTap(MotionEvent e) {
					handleDoubleTapOnCamera(e.getX(), e.getY());
					return true;
				}
			});
			
			
        initViews();
    }
	
    private void initViews() {
        surfaceView = findViewById(R.id.camera_surfaceView);
        btnCapture = findViewById(R.id.camera_btnCapture);
		btnBack = findViewById(R.id.camera_btnBack);
		
		surfaceView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					gestureDetector.onTouchEvent(event);
					return true;
				}
			});
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
		
		btnBack.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});
			
        btnCapture.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					takePicture();
				}
			});
    }
	
	private boolean checkCameraPermission() {
		if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 100);
			return false;
		}

		return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			openCamera();
		}
	}
	
	private void switchCamera(){
		
		if(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
			camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
		}else{ camera.open(Camera.CameraInfo.CAMERA_FACING_BACK); }
	}
	
    private void openCamera() {
        try {
            if (camera != null) {
                camera.release();
                camera = null;
            }
            camera = Camera.open(cameraId);
            setCameraDisplayOrientation();
            setupCameraParameters();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur d'ouverture de la caméra", Toast.LENGTH_SHORT).show();
        }
    }
	
	private void handleDoubleTapOnCamera(float x, float y) {
		Toast.makeText(this, "Double-tap sur la caméra", Toast.LENGTH_SHORT).show();
		switchCamera();
		
		// Exemple: zoom ou changement de focus
		if (camera != null) {
			// Implémenter la logique de zoom/focus
		}
	}
	
    private void setCameraDisplayOrientation() {
        if (camera == null) return;
		
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
		
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
		
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
		
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // Compensation miroir
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
	
	private void setupCameraParameters() {
		if (camera == null) return;
		try {
			Camera.Parameters parameters = camera.getParameters();
            parameters.setRotation(90);
            Camera.Size bestSize = getBestPreviewSize(parameters);
            if (bestSize != null) {
                parameters.setPreviewSize(bestSize.width, bestSize.height);
            }
			
            // Trouver la meilleure taille de picture pour portrait
            Camera.Size bestPictureSize = getBestPictureSize(parameters);
            if (bestPictureSize != null) {
                parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);
            }
			
            // Configurer le focus
            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
			
            camera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    private Camera.Size getBestPreviewSize(Camera.Parameters parameters) {
        // Préférer les ratios 4:3 ou 16:9 pour portrait
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size bestSize = null;
		
        for (Camera.Size size : sizes) {
            // En portrait, on veut que la hauteur > largeur
            if (size.height >= size.width) {
                if (bestSize == null) {
                    bestSize = size;
                } else {
                    // Préférer les tailles avec ratio proche de 3:4
                    float currentRatio = (float) bestSize.width / bestSize.height;
                    float newRatio = (float) size.width / size.height;
					
                    if (Math.abs(newRatio - 0.75) < Math.abs(currentRatio - 0.75)) {
                        bestSize = size;
                    }
                }
            }
        }
		
        // Si pas de taille portrait, prendre la première disponible
        if (bestSize == null && !sizes.isEmpty()) {
            bestSize = sizes.get(0);
        }
        return bestSize;
    }
	
    private Camera.Size getBestPictureSize(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        Camera.Size bestSize = null;
		
        for (Camera.Size size : sizes) {
            // Préférer les tailles avec hauteur > largeur pour portrait
            if (size.height >= size.width) {
                if (bestSize == null) {
                    bestSize = size;
                } else {
                    // Préférer les plus grandes résolutions
                    if (size.height * size.width > bestSize.height * bestSize.width) {
                        bestSize = size;
                    }
                }
            }
        }
		
        // Si pas de taille portrait, prendre la première disponible
        if (bestSize == null && !sizes.isEmpty()) {
            bestSize = sizes.get(0);
        }
		
        return bestSize;
    }
	
    // SurfaceHolder.Callback methods
    @Override public void surfaceCreated(SurfaceHolder holder) { openCamera(); }
	@Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { startCameraPreview(); }
	@Override public void surfaceDestroyed(SurfaceHolder holder) { stopCamera(); }
	
    private void startCameraPreview() {
        if (camera == null) return;
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            isCameraRunning = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
    private void stopCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            isCameraRunning = false;
        }
    }
	
    private void takePicture() {
        if (camera == null || !isCameraRunning) {
            Toast.makeText(this, "Caméra non prête", Toast.LENGTH_SHORT).show();
            return;
        }
		
        camera.takePicture(null, null, new Camera.PictureCallback() {
				@Override
				public void onPictureTaken(byte[] data, Camera camera) {
					savePicture(data);
					camera.startPreview();
				}
			});
    }
	
	private void savePicture(byte[] data) {
		try {
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
			String fileName = "BearChat_IMG_" + timeStamp + ".jpg";
			
			File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			File imageFile = new File(storageDir, fileName);
			
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(data);
            fos.close();
			
			// Notifier la galerie
			MediaScannerConnection.scanFile(this, new String[]{imageFile.getAbsolutePath()}, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Erreur sauvegarde photo", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override protected void onPause() { super.onPause();
		stopCamera();
	}
	
	@Override protected void onResume() { super.onResume();
		if (surfaceHolder != null && surfaceHolder.getSurface().isValid()) {
			openCamera();
		}
	}
}