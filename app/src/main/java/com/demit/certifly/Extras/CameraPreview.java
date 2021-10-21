package com.demit.certifly.Extras;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceHolder holder;
    Camera camera;
    public CameraPreview(Context context, Camera camera)
    {
        super(context);
        this.camera=camera;
        this.holder=getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        try {
            Camera.Parameters parameters;
            parameters=camera.getParameters();
            parameters.set("orientation","portrait");
            camera.setParameters(parameters);
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if(holder.getSurface()==null)
        {
            return;
        }
        try {
            camera.stopPreview();
            camera.setPreviewDisplay(holder);
        }catch (IOException e){
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
