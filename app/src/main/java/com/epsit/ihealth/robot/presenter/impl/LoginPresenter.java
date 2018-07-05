package com.epsit.ihealth.robot.presenter.impl;

import android.text.TextUtils;
import android.widget.Toast;

import com.epsit.ihealth.robot.base.BasePresenter;
import com.epsit.ihealth.robot.base.RobotApplication;
import com.epsit.ihealth.robot.model.ILoginModel;
import com.epsit.ihealth.robot.model.impl.ILoginModelImpl;
import com.epsit.ihealth.robot.presenter.ILoginPresenter;
import com.epsit.ihealth.robot.view.ILoginView;


/**
 * Created by Administrator on 2018/6/14/014.
 */

public class LoginPresenter extends BasePresenter<ILoginView> implements ILoginPresenter {
    String TAG ="LoginPresenter";
    ILoginModel loginModel = new ILoginModelImpl();

    @Override
    public void login(final String robotId, String password,ILoginModel.OnLoginListener listener) {
        loginModel.login(robotId,password, listener);
    }

    @Override
    public void faceInfoByCustomize(ILoginModel.getCountListener listener) {
        loginModel.faceInfoByCustomize(listener);
    }

    @Override
    public void workerSign() {

    }

    @Override
    public void saveUserInfo() {

    }

    @Override
    public void idCardTakeNumber() {

    }

    @Override
    public void showToast(String msg) {
        if(!TextUtils.isEmpty(msg)){
            Toast.makeText(RobotApplication.getInstance().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }


}
