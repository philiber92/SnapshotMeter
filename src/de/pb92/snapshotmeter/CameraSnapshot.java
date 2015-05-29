package de.pb92.snapshotmeter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.hardware.Camera;

@SuppressWarnings("deprecation")
public class CameraSnapshot extends Activity {
	
	private Camera _camera;
	private CameraPreview _cameraPreview;
	
    private Camera.PictureCallback _picture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            /*
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d("tag", "Error creating media file, check storage permissions: " +
                        e.getMessage());
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
            */
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_snapshot);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Create an instance of Camera
        _camera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        //_previewLayer = new PreviewLayer(this);
        _cameraPreview= new CameraPreview(this, _camera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(_cameraPreview);
        //preview.addView(_previewLayer);
        Button captureButton = (Button) findViewById(R.id.button_capture);
        //_previewLayer.bringToFront();
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
			c = Camera.open();
		} catch(Exception e) {
			
		}
		return c;
	}
}
