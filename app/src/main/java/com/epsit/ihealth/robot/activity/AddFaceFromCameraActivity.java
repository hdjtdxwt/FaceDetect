package com.epsit.ihealth.robot.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.epsit.facelibrary.FaceDetectHelper;
import com.epsit.facelibrary.constant.SenseConfig;
import com.epsit.ihealth.robot.R;
import com.epsit.ihealth.robot.base.RobotApplication;
import com.epsit.ihealth.robot.util.TrackUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import dou.utils.BitmapUtil;
import dou.utils.DLog;
import dou.utils.ToastUtil;
import mobile.ReadFace.YMFace;
import mobile.ReadFace.YMFaceTrack;
import mobile.ReadFace.net.NetFaceTrack;

public class AddFaceFromCameraActivity extends AppCompatActivity implements View.OnClickListener, Camera.PreviewCallback {
    String TAG ="AddFaceFromCamera";
    TextView tips;
    SurfaceView surfaceView;
    SurfaceView preSurfaceView;
    Button add_face;
    private float scale_bit;
    boolean isAdd = false;
    boolean isKnowing = false;

    protected int sw;
    protected int sh;

    int personId = -111;//表示不认识
    int addCount;//第几次添加，sdk提示最多加10张一个人
    boolean button_enable = false;

    private boolean saveImage = false;
    private String age, gender, score;

    protected int iw = 0, ih;
    YMFaceTrack faceTrack ;
    SurfaceHolder surfaceHolder;
    Camera.Size previewSize;
    int cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
    Camera camera;
    protected boolean stop = false;
    private final Object lock = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_face_from_camera);
        tips = (TextView) findViewById(R.id.tips);
        surfaceView = (SurfaceView) findViewById(R.id.add_surfaceView);
        preSurfaceView = (SurfaceView) findViewById(R.id.camera_preview);
        add_face = (Button) findViewById(R.id.addFace);
        add_face.setOnClickListener(this);
        faceTrack = new YMFaceTrack();
        faceTrack.setDistanceType(YMFaceTrack.DISTANCE_TYPE_NEAR);

        faceTrack.setOrientation(90);
        int result  = faceTrack.initTrack(getApplicationContext(), YMFaceTrack.FACE_270, YMFaceTrack.RESIZE_WIDTH_640, SenseConfig.appid, SenseConfig.appsecret);

        //设置人脸识别置信度，设置75，不允许修改
        if (result == 0) {
            faceTrack.setRecognitionConfidence(75);
            new ToastUtil(this).showSingletonToast("初始化检测器成功");
        } else {
            new ToastUtil(this).showSingletonToast("初始化检测器失败");
        }
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                DLog.d("surfaceCreated****");
                initCamera();
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                DLog.d("surfaceChanged****");
                openCamera();
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                DLog.d("surfaceDestroyed****");
                stopCamera();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addFace:
                stop = false;
                isAdd = true;
                break;
        }
    }
    void setEnable() {
        if (!button_enable) {
            add_face.setEnabled(true);
            add_face.setBackgroundResource(R.drawable.add_face_able);
            button_enable = !button_enable;
        }
    }

    void setUnEnable() {
        if (button_enable) {
            add_face.setEnabled(false);
            add_face.setBackgroundResource(R.drawable.add_face_unable);
            button_enable = !button_enable;
        }
    }
    protected void drawAnim(List<YMFace> faces, SurfaceView draw_view, float scale_bit, int cameraId, String fps) {
        TrackUtil.drawAnim(faces, draw_view, scale_bit, cameraId, fps, false);
    }

    protected List<YMFace> analyse(final byte[] bytes, int iw, int ih) {
        final List<YMFace> faces = FaceDetectHelper.trackMulti(bytes, iw, ih);
        final byte[] data = bytes;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (faces != null && faces.size() > 0) {
                    Log.e(TAG,"有人脸，initModel2(faces, data)");
                    initModel2(faces, data);
                } else {
                     Log.e(TAG,"没找到你的人脸");
                    tipSetText("没找到你的人脸");
                    setUnEnable();
                }
            }
        });
        return faces;
    }

    void tipSetText(final String string) {
        if (!tips.getText().toString().equals(string))
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tips.setText(string);
                }
            });
    }

    boolean isTrue = false;
    static NetFaceTrack netFaceTrack = NetFaceTrack.getInstance("5042a421fb4f1324ba7948370559f03e", "98b898104646a8c88345fadbbeeef8b360e69c58");

    private void initModel2(List<YMFace> faces, final byte[] bytes) {
        final float[] rect = faces.get(0).getRect();
        int limx = (int) (rect[0] + rect[2] / 2);
        int limy = (int) (rect[1] + rect[3] / 2);
        boolean isTouchable = TrackUtil.isTouchable(limx, limy);
        if(isTouchable){ //人脸转换太快，图片太模糊
            Log.e(TAG,"人脸转换太快，图片太模糊--->");
            return;
        }
        Log.e(TAG,"addCount="+addCount);
        switch (addCount) {
            case 0:
                if (!isTrue) isTrue = isAdd1(faces);
                Log.e(TAG,"isTrue--->"+isTrue+"  isAdd==="+isAdd);
                if (isTrue) {
                    tipSetText("正脸添加");
                    setEnable();
                    if (isAdd) {
                        isTrue = false;

                        //TODO local register
                        personId = faceTrack.identifyPerson(0);
                        if (personId == -111) {
                            Log.e(TAG,"图片确认有人脸，而且不认识");
                            addFace1(bytes, rect);
                        } else { //之前通过其他方式添加过人脸
                            Log.e(TAG,"图片确认有人脸，认识");
                        }
                    }
                } else {
                    tipSetText("正脸");
                    setUnEnable();
                }

                break;
            case 1:
                if (!isTrue) isTrue = isAdd2(faces);
                if (isTrue) {
                    tipSetText("侧脸20度");
                    setEnable();
                    if (isAdd) {
                        isTrue = false;
                        addCount++;
                        int i = faceTrack.updatePerson(personId, 0);
                        DLog.d("update 1 ：" + i);
                        //saveImageFromCamera(personId, 1, bytes);
                        //show_image.setBackgroundResource(R.drawable.nomal_3);

                    }
                } else {
                    tipSetText("侧脸20度");
                    setUnEnable();
                }

                break;
            case 2:
                if (!isTrue) isTrue = isAdd3(faces);
                if (isTrue) {
                    tipSetText("抬头20度");
                    setEnable();
                    if (isAdd) {
                        isTrue = false;
                        addCount++;
                        int i = faceTrack.updatePerson(personId, 0);
                        DLog.d("update 2 ：" + i);
                        //saveImageFromCamera(personId, 2, bytes);
                        //show_image.setBackgroundResource(R.drawable.nomal_4);
                    }
                } else {
                    tipSetText("抬头20度");
                    setUnEnable();
                }
                break;
            case 3:
                if (!isTrue) isTrue = isAdd4(faces);
                if (isTrue) {
                    tipSetText("低头");
                    setEnable();
                    if (isAdd) {
                        isTrue = false;
                        addCount++;
                        int i = faceTrack.updatePerson(personId, 0);
                        DLog.d("update 3 ：" + i);
                        //saveImageFromCamera(personId, 3, bytes);
                    }
                } else {
                    tipSetText("低头20度");
                    setUnEnable();
                }
                break;
            case 4:
                //TODO 结束
                DLog.d("end add person");
                doEnd();
                addCount++;
                break;
        }

    }
    public void saveImageFromCamera(int personId, int count, byte[] yuvBytes) {
        if (!saveImage) return;
        File tmpFile = new File("/sdcard/img/fr/" + personId);
        if (!tmpFile.exists()) tmpFile.mkdirs();
        tmpFile = new File("/sdcard/img/fr/" + personId + "/img_" + count + ".jpg");
        saveImage(tmpFile, yuvBytes);
    }
    private void saveImage(File file, byte[] yuvBytes) {

        FileOutputStream fos = null;
        try {
            YuvImage image = new YuvImage(yuvBytes, ImageFormat.YV12, iw, ih, null);
            fos = new FileOutputStream(file);
            image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 90, fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private Bitmap head = null;
    void addFace1(byte[] bytes, float[] rect) {
        //next.setVisibility(View.VISIBLE);
        personId = faceTrack.addPerson(0);//添加人脸
        int gender_score = faceTrack.getGender(0);
        int gender_confidence = faceTrack.getGenderConfidence(0);
        String gender = " ";
        if (gender_confidence >= 90)
            gender = faceTrack.getGender(0) == 0 ? "F" : "M";
        String score = " ";
        String age = String.valueOf(TrackUtil.computingAge(faceTrack.getAge(0)));
        DLog.d("add Face 1 " + personId + " age :" + age + " gender: " + gender);
        saveImageFromCamera(personId, 0, bytes);
        if (personId > 0) {
            addCount++;//添加人脸成功
            //show_image.setBackgroundResource(R.drawable.nomal_2);
            Bitmap image = BitmapUtil.getBitmapFromYuvByte(bytes, iw, ih);

            //TODO 此处在保存人脸小图
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                Matrix matrix = new Matrix();
                matrix.postRotate(270);
                head = Bitmap.createBitmap(image, iw - (int) rect[1] - (int) rect[3], (int) rect[0],(int) rect[3], (int) rect[2], matrix, true);
            } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {

                if (RobotApplication.reverse_180) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(180);
                    head = Bitmap.createBitmap(image, iw - (int) rect[0] - (int) rect[2], ih - (int) rect[1] - (int) rect[3],
                            (int) rect[2], (int) rect[3], matrix, true);
                } else {
                    head = Bitmap.createBitmap(image, (int) rect[0], (int) rect[1],
                            (int) rect[2], (int) rect[3], null, true);
                }
            }
        } else {
            DLog.d("添加人脸失败！");
            Toast.makeText(this, "添加人脸失败！请重新添加", Toast.LENGTH_SHORT).show();
        }

    }
    private boolean isAdd4(List<YMFace> faces) {//加低头数据

        YMFace face = faces.get(0);
        float facialOri[] = face.getHeadpose();
        float y = facialOri[1];

        if (y > -10) {
            return true;
        }
        return false;
    }

    private boolean isAdd3(List<YMFace> faces) {//加抬头数据

        YMFace face = faces.get(0);
        float facialOri[] = face.getHeadpose();
        float y = facialOri[1];
        if (y <= -10) {
            return true;
        }
        return false;
    }

    private boolean isAdd2(List<YMFace> faces) {//加侧脸数据

        YMFace face = faces.get(0);
        float facialOri[] = face.getHeadpose();
        float z = facialOri[2];
        if (Math.abs(z) >= 15) {
            return true;
        }
        return false;
    }

    private boolean isAdd1(List<YMFace> faces) {//加正脸数据

        YMFace face = faces.get(0);
        float facialOri[] = face.getHeadpose();

        float x = facialOri[0];
        float y = facialOri[1];
        float z = facialOri[2];

        if (Math.abs(x) <= 15 && Math.abs(y) <= 15 && Math.abs(z) <= 15) {
            return true;
        }
        return false;
    }
    void doEnd() {

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
                }
            } catch (IOException var31) {
                var31.printStackTrace();
                if(null != this.camera) {
                    this.camera.release();
                    this.camera = null;
                }
            }
            try {
                Camera.Parameters parameters = this.camera.getParameters();
                this.setCameraDisplayOrientation((Activity)this , cameraFacing, this.camera);
                this.previewSize = this.getBestPreviewSize(640, 480,parameters);
                parameters.setPreviewFormat(ImageFormat.NV21);
                parameters.setPreviewSize(previewSize.width, previewSize.height);
                this.camera.setParameters(parameters);
                this.startPreview();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }
    }
    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }
        if(result==null){
            if(parameters.getSupportedPreviewSizes().size()>0){
                result = parameters.getSupportedPreviewSizes().get(0);
                result.width = 640;
                result.height = 480;
            }
        }
        Log.e(TAG,"bestSize="+result.width+"  "+result.height);
        return result;
    }
    private int setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
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
        DLog.d(rotation + " : " + info.orientation + " : " + result);
        camera.setDisplayOrientation(result);
        return result;
    }
    public void stopCamera() {
        if(null != this.camera) {
            if(this.surfaceView != null) {
            }
            this.camera.setPreviewCallbackWithBuffer((Camera.PreviewCallback)null);
            this.camera.stopPreview();
            this.camera.release();
            this.camera = null;
        }
    }
    public void startPreview() {
        this.camera.startPreview();
        this.camera.setPreviewCallbackWithBuffer(this);
        Log.e(TAG,"this.previewSize.width="+this.previewSize.width+"  this.previewSize.height="+this.previewSize.height);
        this.camera.addCallbackBuffer(new byte[this.previewSize.width * this.previewSize.height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8]);
    }

    @Override
    public void onPreviewFrame(final byte[] bytes, Camera camera) {
        camera.addCallbackBuffer(bytes);
        Log.e(TAG,"获取了！！！");
        initCameraMsg();
        if(!stop){
            stop=true;
            Log.e(TAG,"获取了！！！---》");
            analyse(bytes,previewSize.width, previewSize.height);
        }
    }
    private void initCameraMsg() {
        if (iw == 0) {
            int surface_w = preSurfaceView.getLayoutParams().width;
            int surface_h = preSurfaceView.getLayoutParams().height;
            Log.e(TAG,"surface_w="+surface_w+"  surface_h"+surface_h);
            iw = 640 ;
            ih = 480;


            int orientation = 0;
            ////注意横屏竖屏问题
            Log.e(TAG,getResources().getConfiguration().orientation + " : " + Configuration.ORIENTATION_PORTRAIT);
            if (sw < sh) {
                scale_bit = surface_w / (float) ih;
                orientation = YMFaceTrack.FACE_270;
            } else {
                scale_bit = surface_h / (float) ih;
                orientation = YMFaceTrack.FACE_0;
                if (RobotApplication.reverse_180) {
                    orientation += 180;
                }
            }
            if (faceTrack == null) {
                iw = 0;
                return;
            }

            faceTrack.setOrientation(orientation);
            ViewGroup.LayoutParams params = preSurfaceView.getLayoutParams();
            params.width = surface_w;
            params.height = surface_h;
            preSurfaceView.requestLayout();
        }

    }
    private void runTrack(byte[] data) {
        try {
            long time = System.currentTimeMillis();
            final List<YMFace> faces = analyse(data, iw, ih);

            String str = "";
            StringBuilder fps = new StringBuilder();
            /*if (showFps) {
                fps.append("fps = ");
                long now = System.currentTimeMillis();
                float than = now - time;
                timeList.add(than);
                if (timeList.size() >= 20) {
                    float sum = 0;
                    for (int i = 0; i < timeList.size(); i++) {
                        sum += timeList.get(i);
                    }
                    fps.append(String.valueOf((int) (1000f * timeList.size() / sum)))
                            .append(" camera ")
                            .append(camera_fps);
                    timeList.remove(0);
                }
            }*/
            final String fps1 = fps.toString() + str;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TrackUtil.drawAnim(faces, preSurfaceView, scale_bit, cameraFacing, "", false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
