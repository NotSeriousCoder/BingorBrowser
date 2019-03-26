package com.bingor.browserlib.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.bingor.browserlib.BrowserConstant;
import com.bingor.browserlib.R;
import com.bingor.browserlib.util.WebViewUtil;
import com.bingor.browserlib.view.base.BaseActivity;

/**
 * Created by Bingor on 2019/3/7.
 */
public class BrowserActivity extends BaseActivity {
    private WebView wvMain;
    private WebViewUtil webViewUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
    }

    @Override
    public void prepareInit() {

    }

    @Override
    public void initView() {
        wvMain = findViewById(R.id.wv_m_act_browser_p_main);
        webViewUtil = new WebViewUtil(wvMain);
        webViewUtil.initWebView();
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        String url = intent.getStringExtra(BrowserConstant.KEY_URL);
        wvMain.loadUrl(url);
    }

    @Override
    public void afterInit() {

    }

    @Override
    public void recycleResources() {

    }


    @Override
    public void onBackPressed() {
        if (wvMain.canGoBack()) {
            wvMain.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
