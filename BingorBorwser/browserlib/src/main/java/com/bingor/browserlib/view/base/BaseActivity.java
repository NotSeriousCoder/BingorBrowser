package com.bingor.browserlib.view.base;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;


import com.bingor.browserlib.view.ViewProcedure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HXB on 2017-06-20.
 */
public abstract class BaseActivity extends FragmentActivity implements ViewProcedure {
    public static final int REQUEST_CODE_PERMISSION = 0x1001;
    protected final String TAG = this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //阻止frament恢复销毁前状态
        //        if (savedInstanceState != null) {
        //            savedInstanceState.remove("android:support:fragments");   //注意：基类是Activity时参数为android:fragments， 一定要在super.onCreate函数前执行！！！
        //        }
        super.onCreate(savedInstanceState);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //添加到Activity管理器
//        ActivityManager.add(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        afterInit();
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.close_show, R.anim.close_dismiss);
    }

    @Override
    public void setContentView(int layoutResID) {
        prepareInit();
        super.setContentView(layoutResID);
        runFlow();
    }

    @Override
    public void setContentView(View view) {
        prepareInit();
        super.setContentView(view);
        runFlow();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        prepareInit();
        super.setContentView(view, params);
        runFlow();
    }

//    @Override
//    public void startActivity(Intent intent) {
//        super.startActivity(intent);
//        overridePendingTransition(R.anim.open_show, R.anim.open_dismiss);
//    }

//    @Override
//    public void startActivity(Intent intent, @Nullable Bundle options) {
//        super.startActivity(intent, options);
//        overridePendingTransition(R.anim.open_show, R.anim.open_dismiss);
//    }

//    @Override
//    public void startActivities(Intent[] intents) {
//        super.startActivities(intents);
//        overridePendingTransition(R.anim.open_show, R.anim.open_dismiss);
//    }

//    @Override
//    public void startActivities(Intent[] intents, @Nullable Bundle options) {
//        super.startActivities(intents, options);
//        overridePendingTransition(R.anim.open_show, R.anim.open_dismiss);
//    }

//    @Override
//    public void startActivityForResult(Intent intent, int requestCode) {
//        super.startActivityForResult(intent, requestCode);
//        overridePendingTransition(R.anim.open_show, R.anim.open_dismiss);
//    }

//    @Override
//    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
//        super.startActivityForResult(intent, requestCode, options);
//        overridePendingTransition(R.anim.open_show, R.anim.open_dismiss);
//    }

    protected void runFlow() {
        initView();
        initListener();
        initData();
//        ScreenUtil.initSystemBar(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION && permissions != null && grantResults != null) {
            List<String> permissionGranted = new ArrayList();
            List<String> permissionDenned = new ArrayList();
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                // 缺失的权限
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted.add(permission);
                } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    permissionDenned.add(permission);
                }
            }
            if (!permissionGranted.isEmpty()) {
                onApplyPermissionSuccess(permissions.length, permissionGranted.size(), permissionGranted.toArray(new String[0]));
            }
            if (!permissionDenned.isEmpty()) {
                onApplyPermissionFail(permissions.length, permissionDenned.size(), permissionDenned.toArray(new String[0]));
            }
        }
    }

    /**
     * 授权成功
     *
     * @param permission
     */
    protected void onApplyPermissionSuccess(int numApply, int numSuccess, String[] permission) {
    }

    /**
     * 授权失败
     *
     * @param permission
     */
    protected void onApplyPermissionFail(int numApply, int numFail, String[] permission) {
    }

}
