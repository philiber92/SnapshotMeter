package de.pb92.snapshotmeter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
	
	private SurfaceHolder _holder;
	private Camera _camera;
	
	public CameraPreview(Context context, Camera camera) {
		super(context);
		_camera = camera;
		_holder = getHolder();
		_holder.addCallback(this);
		_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
        try {
            _camera.setPreviewDisplay(holder);
            _camera.startPreview();
            if(_holder.getSurface().isValid()) {
                Canvas canvas = _holder.lockCanvas();
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);

                int w = canvas.getWidth();
                int h = canvas.getHeight();
                canvas.drawPoint(0, 0, paint);
                _holder.unlockCanvasAndPost(canvas);
            }
        } catch (java.io.IOException e) {
        	
        }
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

        if (_holder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        try {
            _camera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        try {
            _camera.setPreviewDisplay(_holder);
            _camera.startPreview();

        } catch (Exception e){
        	
        }
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

}
