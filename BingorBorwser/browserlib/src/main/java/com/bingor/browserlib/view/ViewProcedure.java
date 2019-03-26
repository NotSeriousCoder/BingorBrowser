package com.bingor.browserlib.view;

/**
 * Created by Bingor on 2019/3/7.
 */
public interface ViewProcedure {

    void prepareInit();

    void initView();

    void initListener();

    void initData();

    void afterInit();

    //回收资源，所有在onDestroy的时候需要回收/销毁/关闭的资源，都在这里进行
    void recycleResources();
}
