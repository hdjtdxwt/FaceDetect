package com.epsit.ihealth.robot.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.epsit.ihealt.robot.greendao.gen.FaceImgDataBeanDao;
import com.epsit.ihealth.robot.R;
import com.epsit.ihealth.robot.base.BaseActivity;
import com.epsit.ihealth.robot.base.RobotApplication;
import com.epsit.ihealth.robot.dbentity.FaceImgDataBean;
import com.epsit.ihealth.robot.ebentity.EbDownloadFileInfo;
import com.epsit.ihealth.robot.model.ILoginModel;
import com.epsit.ihealth.robot.presenter.impl.LoginPresenter;
import com.epsit.ihealth.robot.util.AlertError;
import com.epsit.ihealth.robot.view.ILoginView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import dou.utils.ToastUtil;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity<ILoginView, LoginPresenter> implements ILoginView ,ILoginModel.OnLoginListener,ILoginModel.getCountListener   {
    String TAG = "LoginActivity";
    private static final int REQUEST_READ_CONTACTS = 0;

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo:hello", "bar@example.com:world"
    };
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    ProgressBar horizontalPb;
    private View mLoginFormView;
    private int totalDownloadFileCount;
    private int totalDownloadFinished;
    FaceImgDataBeanDao faceImgDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        horizontalPb = (ProgressBar) findViewById(R.id.download_progress);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        faceImgDao = RobotApplication.getInstance().getDaoSession().getFaceImgDataBeanDao();
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "=-----权限获取");
            requestAllPermissionsIfNeed();
        }
        EventBus.getDefault().register(this);
    }
    @TargetApi(Build.VERSION_CODES.M)
    protected void requestAllPermissionsIfNeed() {
        List<String> permissionList = new ArrayList<String>();
        // 申请相机权限
        // Camera permission
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // 用户拒绝过权限申请，下一次再进入的时候给出的解释
                AlertError.showDialog(this, getResources().getString(R.string.error_title), getResources().getString(R.string.no_camera_perm_hint));
            } else {
                permissionList.add(Manifest.permission.CAMERA);
            }
        }
        // 我们需要从应用外的目录获取照片，所以需要申请读取外部存储权限
        // read external storage permission, for we need to read the photos
        // outside application-specific directories
        /*if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertError.showDialog(this, getResources().getString(R.string.error_title),
                        getResources().getString(R.string.no_file_perm_hint));
            } else {
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }*/
        if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.INTERNET)) {
                AlertError.showDialog(this, getResources().getString(R.string.error_title),
                        getResources().getString(R.string.no_file_perm_hint));
            } else {
                permissionList.add(Manifest.permission.INTERNET);
            }
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertError.showDialog(this, getResources().getString(R.string.error_title),
                        getResources().getString(R.string.no_file_perm_hint));
            } else {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
        if (permissionList.size() > 0) {
            requestPermissions(permissionList.toArray(new String[permissionList.size()]), 0);
        }
    }

    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            basePresenter.login(email,password, this);
            /*mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);*/
        }
    }
    @Override
    public void onSuccess() {
        Toast.makeText(getApplicationContext(),"登录成功，接下来初始化...",Toast.LENGTH_SHORT).show();
        basePresenter.faceInfoByCustomize(this);
    }

    @Override
    public void onFail() {
        showProgress(false);
        Toast.makeText(getApplicationContext(),"登录失败",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void getCount(int count,int needDownload) {
        Log.e(TAG,"后台返回数量："+count+"  本地需要下载："+needDownload);
        totalDownloadFileCount = needDownload;
        horizontalPb.setMax(needDownload);
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onDownloadProgresschange(EbDownloadFileInfo info){
        totalDownloadFinished ++;
        horizontalPb.setProgress(totalDownloadFinished);
        if(!info.isSuccess()){
            String url = info.getDownloadUrl();
            //DataSupport.delete() //删除这个没下载成功的
            Log.e(TAG, "---又失败了一个："+info.getDownloadUrl());
        }else{
            Log.e(TAG, "-下载成功了一个："+info.getDownloadUrl());
        }
        if(totalDownloadFileCount==totalDownloadFinished){
            showProgress(false);
            horizontalPb.setVisibility(View.GONE);
        }
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
            horizontalPb.setVisibility(show ? View.GONE : View.VISIBLE);
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            horizontalPb.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    public void showDialog() {

    }

    @Override
    public void hideDialog() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

}

