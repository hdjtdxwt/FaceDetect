package com.epsit.ihealth.robot.util;


import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import dou.utils.DLog;
import dou.utils.DisplayUtil;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class CameraHelper implements PreviewCallback {
    private Camera camera = null;
    private SurfaceHolder surfaceHolder = null;
    private SurfaceView surfaceView;
    private SurfaceTexture mSurfaceTexture;
    private CameraHelper.PreviewFrameListener previewFrameListener;
    private Context context;
    private Size previewSize;
    private int cameraFacing = 1;
    private int sw;
    private int sh;
    private int camera_max_width = 0;
    private int camera_max_height = 0;
    private int rotate = 0;
    private int rotate_front = 0;
    private int pre_rate = 0;
    private MediaRecorder mediaRecorder;

    public CameraHelper(Context context, CameraParams params) {
        this.context = context;

        assert params != null;

        this.surfaceView = params.surfaceView;
        this.camera_max_width = params.preview_width;
        this.camera_max_height = params.preview_height;
        this.previewFrameListener = params.previewFrameListener;
        this.cameraFacing = params.firstCameraId;
        this.rotate = params.camera_ori;
        this.rotate_front = params.camera_ori_front;
        this.pre_rate = params.pre_rate;
        this.sw = DisplayUtil.getScreenWidthPixels(context);
        this.sh = DisplayUtil.getScreenHeightPixels(context);
        if(this.surfaceView != null) {
            this.surfaceHolder = this.surfaceView.getHolder();
            this.surfaceHolder.addCallback(new Callback() {
                public void surfaceCreated(SurfaceHolder holder) {
                    DLog.d("surfaceCreated****");
                    CameraHelper.this.initCamera();
                }

                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    DLog.d("surfaceChanged****");
                    CameraHelper.this.openCamera();
                }

                public void surfaceDestroyed(SurfaceHolder holder) {
                    DLog.d("surfaceDestroyed****");
                    CameraHelper.this.stopCamera();
                }
            });
        } else {
            this.initCamera();
            this.openCamera();
        }

    }

    private void initCamera() {
        if(this.camera != null) {
            this.stopCamera();
        }

        try {
            this.camera = Camera.open(this.cameraFacing);
        } catch (Exception var2) {
            DLog.d("摄像头" + this.cameraFacing + "开启失败，正在尝试开启另一个摄像头");
            this.cameraFacing = this.cameraFacing == 1?0:1;
            if(this.camera == null) {
                this.camera = Camera.open(this.cameraFacing);
            }
        }

        if(this.camera == null) {
            DLog.d("摄像头开启失败");
        }

    }

    private void openCamera() {
        if(null != this.camera) {
            try {
                if(this.surfaceView != null) {
                    this.camera.setPreviewDisplay(this.surfaceHolder);
                } else {
                    if(this.mSurfaceTexture == null) {
                        this.mSurfaceTexture = new SurfaceTexture(-1);
                    }

                    this.camera.setPreviewTexture(this.mSurfaceTexture);
                }
            } catch (IOException var31) {
                var31.printStackTrace();
                if(null != this.camera) {
                    this.camera.release();
                    this.camera = null;
                }
            }

            try {
                this.setCameraDisplayOrientation((Activity)this.context, this.getCameraId(), this.camera);
                Parameters var3 = this.camera.getParameters();
                this.setOptimalPreviewSize(var3, this.camera_max_width);
                this.camera.setParameters(var3);
                this.startPreview();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }

    }

    public void startPreview() {
        this.camera.startPreview();
        this.camera.setPreviewCallbackWithBuffer(this);
        this.camera.addCallbackBuffer(new byte[this.previewSize.width * this.previewSize.height * ImageFormat.getBitsPerPixel(17) / 8]);
    }

    private void setOptimalPreviewSize(Parameters cameraParams, int targetWidth) {
        List supportedPreviewSizes = cameraParams.getSupportedPreviewSizes();
        if(null != supportedPreviewSizes) {
            Size optimalSize = null;
            double preview_width;
            Iterator previewRatio;
            Size surface_width;
            if(targetWidth == -1) {
                preview_width = 1.7976931348623157E308D;
                previewRatio = supportedPreviewSizes.iterator();

                label78:
                while(true) {
                    do {
                        do {
                            if(!previewRatio.hasNext()) {
                                break label78;
                            }

                            surface_width = (Size)previewRatio.next();
                        } while((double)Math.abs(surface_width.width - 1000) > preview_width);

                        preview_width = (double)Math.abs(surface_width.width - 1000);
                    } while(optimalSize != null && (this.sh < this.sw && optimalSize.width * this.sh == optimalSize.height * this.sw || this.sh > this.sw && optimalSize.width * this.sw == optimalSize.height * this.sh));

                    optimalSize = surface_width;
                }
            }

            if(optimalSize == null) {
                targetWidth = targetWidth == -1?640:targetWidth;
                preview_width = 1.7976931348623157E308D;
                previewRatio = supportedPreviewSizes.iterator();

                while(previewRatio.hasNext()) {
                    surface_width = (Size)previewRatio.next();
                    if(surface_width.width == this.camera_max_width && surface_width.height == this.camera_max_height) {
                        optimalSize = surface_width;
                        break;
                    }

                    if((double)Math.abs(surface_width.width - targetWidth) <= preview_width) {
                        preview_width = (double)Math.abs(surface_width.width - targetWidth);
                        optimalSize = surface_width;
                    }
                }
            }

            this.setPreviewSize(optimalSize);
            float preview_width1 = (float)optimalSize.width;
            float preview_height = (float)optimalSize.height;
            float previewRatio1 = preview_width1 / preview_height;
            if(this.surfaceView != null) {
                boolean surface_width1 = false;
                boolean surface_height = false;
                if(this.sw > this.sh) {
                    ;
                }

                int surface_width2;
                int surface_height1;
                if(this.sw > this.sh) {
                    surface_width2 = (int)((float)this.sh * previewRatio1);
                    surface_height1 = this.sh;
                } else {
                    surface_width2 = this.sw;
                    surface_height1 = (int)((float)this.sw * previewRatio1);
                }

                this.surfaceView.getLayoutParams().width = surface_width2;
                this.surfaceView.getLayoutParams().height = surface_height1;
                this.surfaceView.requestLayout();
                Log.d("DLog", preview_width1 + ":" + preview_height + ":" + surface_width2 + ":" + surface_height1 + ":" + this.sw + ":" + this.sh);
            }

            cameraParams.setPreviewSize((int)preview_width1, (int)preview_height);
        }

    }

    public Size getPreviewSize() {
        return this.previewSize;
    }

    public void stopCamera() {
        if(null != this.camera) {
            if(this.mSurfaceTexture != null) {
                this.mSurfaceTexture.release();
                this.mSurfaceTexture = null;
            }

            this.camera.setPreviewCallbackWithBuffer((PreviewCallback)null);
            this.camera.stopPreview();
            this.camera.release();
            this.camera = null;
        }

    }

    private void setPreviewSize(Size previewSize) {
        this.previewSize = previewSize;
    }

    public int switchCamera() {
        this.cameraFacing = this.cameraFacing == 1?0:1;
        if(!this.hasFacing(this.cameraFacing)) {
            return this.cameraFacing;
        } else {
            this.stopCamera();
            this.initCamera();
            this.openCamera();
            return this.cameraFacing;
        }
    }

    public int getCameraId() {
        return this.cameraFacing;
    }

    public void stopPreview() {
        if(this.camera != null) {
            this.camera.stopPreview();
        }

    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        camera.addCallbackBuffer(data);
        if(this.previewFrameListener != null) {
            this.previewFrameListener.onPreviewFrame(data, camera);
        }

    }

    private boolean hasFacing(int facing) {
        CameraInfo info = new CameraInfo();

        for(int i = 0; i < Camera.getNumberOfCameras(); ++i) {
            Camera.getCameraInfo(i, info);
            if(info.facing == facing) {
                return true;
            }
        }

        return false;
    }

    private int setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        short degrees = 0;
        switch(rotation) {
            case 0:
                degrees = 0;
                break;
            case 1:
                degrees = 90;
                break;
            case 2:
                degrees = 180;
                break;
            case 3:
                degrees = 270;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        Log.e("CameraHelper","自己测试修改方向："+rotation + " : " + info.orientation + " : " + result);
        camera.setDisplayOrientation(result);
        return result;
    }

    public void startRecord(String name) {
        try {
            this.camera.lock();
            this.camera.unlock();
            this.mediaRecorder = new MediaRecorder();
            this.mediaRecorder.setCamera(this.camera);
            this.mediaRecorder.setVideoSource(1);
            this.mediaRecorder.setAudioSource(1);
            this.mediaRecorder.setProfile(CamcorderProfile.get(1));
            this.mediaRecorder.setVideoSize(640, 480);
            this.mediaRecorder.setOutputFile(name);
            this.mediaRecorder.setPreviewDisplay(this.surfaceView.getHolder().getSurface());
            this.mediaRecorder.prepare();
            this.mediaRecorder.start();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void stopRecord() {
        if(this.mediaRecorder != null) {
            this.mediaRecorder.reset();
            this.camera.lock();
            this.mediaRecorder.release();
            this.mediaRecorder = null;
        }

    }

    public void setZoom(int pre) {
        if(this.camera != null) {
            pre = pre < 0?0:pre;
            pre = pre > 100?100:pre;
            Parameters parameters = this.camera.getParameters();
            int max = parameters.getMaxZoom();
            parameters.setZoom(max * pre / 100);
            this.camera.setParameters(parameters);
        }
    }

    public interface PreviewFrameListener {
        void onPreviewFrame(byte[] var1, Camera var2);
    }
}
