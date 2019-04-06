package com.example.myapplication;

public interface OnInputListener {

    /**
     * 关于点击外部取消
     */
    void onCanceledOnTouchOutside();


    /**
     * 关于back键的监听
     */
    void onBackPressed();


    /**
     * 切换状态 例如有表情的时候
     *
     * @param isSpecial
     */
    void onSwithInputState(boolean isSpecial);

    /**
     * 有时候需要跳转到其他页面
     */
    void onSwithPager();

    /**
     * 发送监听 需要定义消息类型
     */
    void onSendMessage();
}
