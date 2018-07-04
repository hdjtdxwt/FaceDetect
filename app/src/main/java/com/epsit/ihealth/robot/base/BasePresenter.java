package com.epsit.ihealth.robot.base;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018/6/6/006.
 */

public class BasePresenter<T> {
    protected WeakReference<T> mViewRef;

    public void attachView(T view){
        mViewRef = new WeakReference<T>(view);
    }
    public void detachView(){
        mViewRef.clear();
    }
}
