package de.pb92.snapshotmeter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;

@SuppressLint("SimpleDateFormat")
@SuppressWarnings("deprecation")
public class CameraSnapshot extends Activity {
	
	public static final String EXTRA_METER_VALUE = "de.pb92.snapshotmeter.METER_VALUE";
	
	private Camera _camera = null;
	private CameraPreview _cameraPreview;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private String meterID;
	
	@Override
	protected void onDestroy() {
		releaseCamera();
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.removeView(_cameraPreview);
		releaseCamera();
		super.onPause();
	}
	
	private void releaseCamera() {
		if(_camera != null) {
			_camera.release();
			_camera = null;
		}
	}
	
    private Camera.PictureCallback _picture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        	
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d("tag", "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (java.io.FileNotFoundException e) {
                Log.d("tag", "File not found: " + e.getMessage());
            } catch (java.io.IOException e) {
                Log.d("tag", "Error accessing file: " + e.getMessage());
            }
            
            Bitmap bmp = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
            Bitmap value = Bitmap.createBitmap(bmp, (bmp.getWidth() - 350)/2, (bmp.getHeight() - 100)/2, 350, 100);
            Bitmap argbFormat = value.copy(Bitmap.Config.ARGB_8888, true);
            
            TessBaseAPI tess = new TessBaseAPI();
            tess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789");
            tess.setImage(argbFormat);
            tess.setPageSegMode(8);
            tess.init(SnapshotMeter.getTessPath(), "deu");
            String tessValue = tess.getUTF8Text();
            tessValue.replace(" ", "");
            tess.end();
            
            if(meterID != null) {
            	Intent intent = new Intent(getBaseContext(), MeterReading.class);
            	intent.putExtra(EXTRA_METER_VALUE, tessValue);
            	intent.putExtra(MainMenu.EXTRA_METER_ID, meterID);
            	startActivity(intent);
            } else {
            	Intent intent = new Intent(getBaseContext(), MeterReadingQuick.class);
            	intent.putExtra(EXTRA_METER_VALUE, tessValue);
            	startActivity(intent);
            }
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_snapshot);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		Intent request = getIntent();
		meterID = request.getStringExtra(MeterReadingOverview.EXTRA_METER_ID);

        // Create an instance of Camera
        _camera = getCameraInstance();
        
        if(_camera == null || !checkCameraHardware(getBaseContext()) 
        				   || SnapshotMeter.getTessPath() == null) {
        	Intent intent;
        	if(meterID == null) {
        		intent = new Intent(getBaseContext(), MeterReadingQuick.class);
        	} else {
        		intent = new Intent(getBaseContext(), MeterReading.class);
        		intent.putExtra(MeterReadingOverview.EXTRA_METER_ID, meterID);
        	}
        	startActivity(intent);
        }

        // Create our Preview view and set it as the content of our activity.
        //_previewLayer = new PreviewLayer(this);
        _cameraPreview= new CameraPreview(this, _camera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(_cameraPreview);
        //preview.addView(_previewLayer);
        Button captureButton = (Button) findViewById(R.id.button_capture);
        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        //_previewLayer.bringToFront();
        imageView.bringToFront();
        captureButton.bringToFront();
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        _camera.takePicture(null, null, _picture);
                    }
                }
        );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera_snapshot, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private boolean checkCameraHardware(Context context) {
		return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}
	
	private static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(0);
		} catch(Exception e) {
			
		}
		return c;
	}
	

	/** Create a file Uri for saving an image or video */
	@SuppressWarnings("unused")
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "Snapshot Meter");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "Ablesung_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
}
