package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * @author gexinyu
 */
public class FineInputView extends FrameLayout implements View.OnClickListener, View.OnKeyListener {

    private Context mContext;
    private RelativeLayout rlSpecialContent;//存放特殊view的地方（可以自定义）
    private int touchSlop;//触点的位移用于校验是否是点击
    private long isClickTime = 350;//两次点击时间控制在为点击时间范围之内
    private long downTime;//按下时间
    private float downX, downY;//按下位置
    private OnInputListener onInputListener;

    private boolean mIsSpecial = false;//是否是特别页面
    private Button btnSwitch, btnSwitchPager, btnSend;

    public FineInputView(@NonNull Context context) {
        this(context, null);
    }

    public FineInputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FineInputView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_fine_input, this);
        rlSpecialContent = contentView.findViewById(R.id.rl_special_content);
        btnSwitch = contentView.findViewById(R.id.btn_switch);
        btnSwitchPager = contentView.findViewById(R.id.btn_switch_pager);
        btnSend = contentView.findViewById(R.id.btn_send);

        btnSwitch.setOnClickListener(this);
        btnSwitchPager.setOnClickListener(this);
        btnSend.setOnClickListener(this);

        getInputView().setOnKeyListener(this);
    }


    /**
     * 获取展示的view
     *
     * @return
     */
    public View getInputView() {
        return getChildAt(0);
    }

    /**
     * 初始化主要用于设置底部view的高度
     *
     * @param bottomHeight
     */
    public void initialize(int bottomHeight) {
        show();
        //设置为占空间
        rlSpecialContent.setVisibility(INVISIBLE);
        ViewGroup.LayoutParams linearParams = (ViewGroup.LayoutParams) rlSpecialContent.getLayoutParams();
        int lastHeight = linearParams.height;
        if (bottomHeight != lastHeight) {
            //不重复设置
            linearParams.height = bottomHeight;
            rlSpecialContent.setLayoutParams(linearParams);
        }
    }

    /**
     * dismiss之后重新显示 没销毁状态
     */
    public void show() {
        //获取焦点用于监听back键
        changeFocus(true);
        //添加视图到content中
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            final ViewGroup content = (ViewGroup) decorView.findViewById(android.R.id.content);
            if (getParent() == null) {
                content.addView(this);
            }
        }
    }

    /**
     * 取消
     */
    public void dismiss() {
        //获取焦点用于监听back键
        changeFocus(false);
        changeInputState(false);
        //移除掉当前的view
        ViewParent parent = getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(this);
        }
    }

    /**
     * 改变焦点
     *
     * @param isShow
     */
    public void changeFocus(boolean isShow) {
        if (isShow) {
            View inputView = getInputView();
            inputView.requestFocus();
            inputView.setFocusable(true);
            inputView.setFocusableInTouchMode(true);
        } else {
            clearFocus();
            View contentView = ((Activity) getContext()).findViewById(android.R.id.content);
            contentView.setFocusable(true);
            contentView.setFocusableInTouchMode(true);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e("TAG", "dispatchKeyEvent");
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        Log.e("TAG", "dispatchKeyEventPreIme");
        return super.dispatchKeyEventPreIme(event);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (onInputListener != null) {
                onInputListener.onBackPressed();
            }
            return true;
        }
        return false;
    }

    /**
     * 处理点击范围，如果在不在就取消
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Rect rect = new Rect();
        getInputView().getGlobalVisibleRect(rect);
        if (!WindowUtil.touchIsInRect(event.getX(), event.getY(), rect)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    downY = event.getY();
                    downTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_UP:
                    float dx = event.getX() - downX;
                    float dy = event.getY() - downY;
                    float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                    if (distance < touchSlop && (System.currentTimeMillis() - downTime) < isClickTime) {
                        //点击外部就消失
                        onInputListener.onCanceledOnTouchOutside();
                    }
                    downX = 0;
                    downY = 0;
                    downTime = 0;
                    break;
            }
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        if (v == btnSwitch) {
            switchContent();
        } else if (v == btnSwitchPager) {
            //切换页面
            switchPager();
        } else if (v == btnSend) {
            if (onInputListener != null) {
                onInputListener.onSendMessage();
            }
        }
    }

    /**
     * 切换页面
     */
    private void switchPager() {
        if (onInputListener != null) {
            onInputListener.onSwithPager();
        }
        dismiss();
    }

    /**
     * 按钮切换
     */
    private void switchContent() {
        mIsSpecial = !mIsSpecial;
        if (onInputListener != null) {
            onInputListener.onSwithInputState(mIsSpecial);
        }
        changeInputState(mIsSpecial);
    }

    /**
     * 切换内容
     *
     * @param isSpecialContent
     */
    public void changeInputState(boolean isSpecialContent) {
        mIsSpecial = isSpecialContent;
        //布局的时候
        rlSpecialContent.setVisibility(isSpecialContent == true ? VISIBLE : INVISIBLE);
        btnSwitch.setText(isSpecialContent == true ? "显示文本" : "显示表情");
    }


    /**
     * 设置输入框的一些功能监听
     *
     * @param onInputListener
     */
    public void setOnInputListener(OnInputListener onInputListener) {
        this.onInputListener = onInputListener;
    }


}
