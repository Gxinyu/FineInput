package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtils {


    /**
     * 判断当前输入法是否打开
     *
     * @param activity
     * @return
     */
    public static boolean getInputOpen(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();//isOpen若返回true，则表示输入法打开
    }


    /**
     * 显示软键盘
     *
     * @param activity
     */
    public static void showSoftkeyboard(Activity activity) {
        try {
            InputMethodManager inputManager =
                    (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(activity.getCurrentFocus(), 0);
        } catch (NullPointerException e) {

        }
    }


    /**
     * 强制显示软键盘
     *
     * @param activity
     * @param view
     */
    public static void forcedShowSoftkeyboard(Activity activity, View view) {
        try {
            InputMethodManager inputManager =
                    (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, 0);
        } catch (NullPointerException e) {

        }
    }

    /**
     * 隐藏软键盘
     *
     * @param activity
     */
    public static void hideSoftkeyboard(Activity activity) {
        try {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException e) {

        }
    }

    /**
     * 强制隐藏软键盘
     *
     * @param activity
     * @param view
     */
    public static void forcedhideSoftkeyboard(Activity activity, View view) {

        try {
            InputMethodManager inputManager =
                    (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
        } catch (NullPointerException e) {

        }
    }


//    public static void getSoftkeyboardHeight(Activity activity, final OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener) {
//
//        final View decorView = activity.getWindow().getDecorView();
//        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//
//            //当键盘弹出隐藏的时候会 调用此方法。
//            @Override
//            public void onGlobalLayout() {
//                final Rect rect = new Rect();
//                decorView.getWindowVisibleDisplayFrame(rect);
//                onSoftKeyBoardChangeListener.onSoftKeyBoardHeight(rect.bottom);
//            }
//        });
//
//    }


    /**
     * 切换软键盘状态 如果是弹出就隐藏
     *
     * @param activity
     */
    public static void toggleSoftkeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 监听输入键盘的谈起和收起
     *
     * @param activity
     * @param onSoftkeyboardChangeListener
     */
    public static void setOnSoftkeyboardChangeListener(Activity activity, final OnSoftKeyBoardChangeListener onSoftkeyboardChangeListener) {

        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            //当键盘弹出隐藏的时候会 调用此方法。
            @Override
            public void onGlobalLayout() {
                final Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                final int screenHeight = decorView.getRootView().getHeight();
                final int heightDifference = screenHeight - rect.bottom;
                boolean visible = heightDifference > screenHeight / 3;
                onSoftkeyboardChangeListener.onSoftKeyBoardChange(visible, heightDifference);
            }
        });
    }


    /**
     * 监听软键盘的弹起和收起的回调
     */
    public interface OnSoftKeyBoardChangeListener {

        /**
         * 第一个参数是否显示
         * 第二个参数是软键盘高度
         *
         * @param isShow
         * @param keyboadHeight
         */
        void onSoftKeyBoardChange(boolean isShow, int keyboadHeight);

    }

}
