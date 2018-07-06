package com.epsit.ihealth.robot.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.epsit.ihealth.robot.R;

public class AddFaceFromCameraActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tipsTv;
    SurfaceView surfaceView;
    Button addFace;
    int faceId = -111;//表示不认识
    int addCount;//第几次添加，sdk提示最多加10张一个人

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_face_from_camera);
        tipsTv = (TextView) findViewById(R.id.tips);
        surfaceView = (SurfaceView) findViewById(R.id.add_surfaceView);
        addFace = (Button) findViewById(R.id.addFace);
        addFace.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
