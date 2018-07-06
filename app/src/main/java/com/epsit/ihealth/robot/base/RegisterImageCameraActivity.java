package com.epsit.ihealth.robot.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.epsit.ihealth.robot.R;
import com.epsit.ihealth.robot.util.DrawUtil;
import com.epsit.ihealth.robot.util.TrackUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import dou.utils.BitmapUtil;
import dou.utils.DLog;
import dou.utils.StringUtils;
import mobile.ReadFace.YMFace;
import mobile.ReadFace.net.NetFaceTrack;

/**
 * Created by mac on 16/8/11.
 */
public class RegisterImageCameraActivity extends BaseCameraActivity {
    String TAG ="ImageCameraActivity ";

    boolean isAdd = false;
    boolean isKnowing = false;
    int addCount = 0;
    int personId = -111;

    private RelativeLayout top_view;
    private TextView tips, next;
    private View show_image, camera_layout;
    private Button add_face;

    private boolean saveImage = false;
    private String age, gender, score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unlock_insert_activity);
        setCamera_max_width(-1);
        netFaceTrack.setRetrofit("http://121.42.141.249:8011/");
        initCamera();
        showFps(false);

        initView();
    }

    public void initView() {
        TextView title = (TextView) findViewById(R.id.page_title);
        tips = (TextView) findViewById(R.id.tips);
        next = (TextView) findViewById(R.id.next);

        show_image = findViewById(R.id.show_image);
        add_face = (Button) findViewById(R.id.add_face);
        add_face.setVisibility(View.GONE);
        show_image.setBackgroundResource(R.drawable.nomal_1);


        add_face.getLayoutParams().width = getDoomW(450);
        add_face.getLayoutParams().height = getDoomW(170);

        title.setText(R.string.photograph_input);
        next.setVisibility(View.GONE);

        Button register = (Button) findViewById(R.id.register);
        top_view = (RelativeLayout) findViewById(R.id.top_view);
        camera_layout = findViewById(R.id.camera_layout);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                top_view.setVisibility(View.GONE);
                camera_layout.setVisibility(View.VISIBLE);
                add_face.setVisibility(View.VISIBLE);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCount = 4;
            }
        });
    }

    @Override
    protected void drawAnim(List<YMFace> faces, SurfaceView draw_view, float scale_bit, int cameraId, String fps) {
        TrackUtil.drawAnim(faces, draw_view, scale_bit, cameraId, fps, false);
    }

    @Override
    protected List<YMFace> analyse(final byte[] bytes, int iw, int ih) {
        final List<YMFace> faces = faceTrack.trackMulti(bytes, iw, ih);
//        final List<YMFace> faces = faceTrack.faceDetect(bytes, iw, ih);
//        YMFace face = faceTrack.faceDetect(bytes, iw, ih);
//        if (face != null)
//            faces.add(face);

        final byte[] data = bytes;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (faces != null && faces.size() > 0) {
                    initModel2(faces, data);
                } else {
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
        //rule 1 人脸框在指定框内
//        if (!isCenter(faces.get(0).getRect())) {
//            setUnEnable();
//            return;
//        }
        //rule 2 第一张图片是正脸的
        final float[] rect = faces.get(0).getRect();
        int limx = (int) (rect[0] + rect[2] / 2);
        int limy = (int) (rect[1] + rect[3] / 2);
        boolean isTouchable = TrackUtil.isTouchable(limx, limy);
//        if (isAdd && !isTouchable) {
//            Toast.makeText(RegisterImageCameraActivity.this, "面部移动速度过快，请慢点", Toast.LENGTH_SHORT).show();
//            isAdd = false;
//        }
        Log.e(TAG,"addCount="+addCount);
        switch (addCount) {
            case 0:
                if (!isTrue) isTrue = isAdd1(faces);

                if (isTrue) {
                    tipSetText("正脸添加");
                    setEnable();
                    if (isAdd) {
                        isTrue = false;

                        //TODO local register
                        personId = faceTrack.identifyPerson(0);
                        if (personId == -111) {
                            addFace1(bytes, rect);
                        } else { //之前通过其他方式添加过人脸
                            /*User user = DrawUtil.getUserById(personId + "");
                            String name = personId + "";
                            if (user != null) name = user.getName();

                            final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterImageCameraActivity.this);
                            builder.setTitle(R.string.dalog_notice).setCancelable(false);
                            builder.setMessage(String.format(getString(R.string.dialog_msg), name))
                                    .setPositiveButton("忽略", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            personId = -111;
                                            addCount = 0;
                                        }
                                    })
                                    .setNegativeButton("更新", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            faceTrack.deletePerson(personId);
                                            DataSource dataSource = new DataSource(getAppContext());
                                            String imgPath = getAppContext().getCacheDir()
                                                    + "/" + personId + ".jpg";
                                            File imgFile = new File(imgPath);
                                            if (imgFile.exists()) {
                                                imgFile.delete();
                                            }
                                            dataSource.deleteById(personId + "");
                                            faceTrack.deletePerson(personId);
                                            addFace1(bytes, rect);
                                        }
                                    })
                                    .setNeutralButton("这个不是我呀", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            addFace1(bytes, rect);
                                        }
                                    });
                            builder.create().show();*/
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
                        saveImageFromCamera(personId, 1, bytes);
                        show_image.setBackgroundResource(R.drawable.nomal_3);

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
                        saveImageFromCamera(personId, 2, bytes);
                        show_image.setBackgroundResource(R.drawable.nomal_4);
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
                        saveImageFromCamera(personId, 3, bytes);
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

        isAdd = false;
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

    public void topClick(View view) {
        switch (view.getId()) {
            case R.id.page_cancle:
                onBackPressed();
                break;
            case R.id.add_face:
                if (!isAdd) isAdd = true;
                if (isKnowing) {
                    isAdd = false;
                    /*Toast.makeText(RegisterImageCameraActivity.this,
                            String.format(getString(R.string.know_yet), personId), Toast.LENGTH_SHORT).show();*/
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (addCount >= 4 || personId == -111) {
            stopCamera();
            finish();
        } else {
            /*final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dalog_notice).setMessage(String.format(getString(R.string.dialog_msg1), addCount))
                    .setNegativeButton(R.string._sure_out, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            faceTrack.deletePerson(personId);
                            stopCamera();
                            finish();
                        }
                    }).setPositiveButton(R.string._keep_pre, null);
            builder.create().show();*/
        }
       // DrawUtil.updateDataSource();
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

    boolean button_enable = false;

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

    void addFace1(byte[] bytes, float[] rect) {
        next.setVisibility(View.VISIBLE);
        personId = faceTrack.addPerson(0);//添加人脸
        int gender_score = faceTrack.getGender(0);
        int gender_confidence = faceTrack.getGenderConfidence(0);
        gender = " ";
        if (gender_confidence >= 90)
            gender = faceTrack.getGender(0) == 0 ? "F" : "M";
        score = " ";
        age = String.valueOf(TrackUtil.computingAge(faceTrack.getAge(0)));
        DLog.d("add Face 1 " + personId + " age :" + age + " gender: " + gender);
        saveImageFromCamera(personId, 0, bytes);
        if (personId > 0) {
            addCount++;//添加人脸成功

            show_image.setBackgroundResource(R.drawable.nomal_2);

            Bitmap image = BitmapUtil.getBitmapFromYuvByte(bytes, iw, ih);

            //TODO 此处在保存人脸小图
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                Matrix matrix = new Matrix();
                matrix.postRotate(270);
                head = Bitmap.createBitmap(image, iw - (int) rect[1] - (int) rect[3], (int) rect[0],
                        (int) rect[3], (int) rect[2], matrix, true);
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
            Toast.makeText(mContext, "添加人脸失败！请重新添加", Toast.LENGTH_SHORT).show();
        }

    }

    private Bitmap head = null;

    void doEnd() {

        /*final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        final EditText et = new EditText(mContext);
        et.setGravity(Gravity.CENTER);
        et.setHint(R.string.insert_nickname);
        et.setHintTextColor(0xffc6c6c6);
        builder.setTitle(R.string.dalog_notice)
                .setMessage(String.format(getString(R.string.dialog_msg2), personId))
                .setView(et)
                .setPositiveButton(R.string._sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String name = et.getText().toString();
                        if (!StringUtils.isEmpty(name.trim())) {
                        } else {
                            doEnd();
                            return;
                        }
//                        if (saveImage) {
//                            //修改文件夹名
//                            File tmpFile = new File("/sdcard/img/fr/" + personId);
//                            tmpFile.renameTo(new File("/sdcard/img/fr/" + name));
//                        }

                        *//*User user = new User("" + personId, name, age, gender);
                        user.setScore(score);
                        DataSource dataSource = new DataSource(mContext);
                        dataSource.insert(user);
                        BitmapUtil.saveBitmap(head, mContext.getCacheDir() + "/" + personId + ".jpg");

                        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setCancelable(false);
                        builder.setMessage(R.string.image_sure_next)
                                .setNegativeButton(R.string._yes,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                addCount = 0;
//                                                personId = -111;
//                                                show_image.setBackgroundResource(R.drawable.nomal_1);
//                                                next.setVisibility(View.GONE);


                                                setResult(101, getIntent());
                                                onBackPressed();
                                            }
                                        })
                                .setPositiveButton(R.string._no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DLog.d("back start");
                                        setResult(102, getIntent());
                                        onBackPressed();
                                    }
                                });
                        builder.create().show();*//*

                    }
                });
        builder.create().show();*/
    }


}
