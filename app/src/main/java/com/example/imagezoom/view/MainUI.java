package com.example.imagezoom.view;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.example.imagezoom.R;

/**
 * Created by lenovo on 2017/5/3.
 */

public class MainUI extends RelativeLayout {

    private Context context;            //上下文
    private FrameLayout leftMenu;       //左边部分
    private FrameLayout middleMenu;     //中间部分
    private FrameLayout rightMenu;      //右边部分
    private FrameLayout middleMask;     //蒙版效果
    private Scroller mScroller;         //滑动动画
    public static final int ID = 0;     //ID
    public static final int LEFT_ID = ID + 1;
    public static final int MIDDLE_ID = ID + 2;
    public static final int RIGHT_ID = ID + 3;

    //构造函数
    public MainUI(Context context) {
        super(context);
        initView(context);
    }

    public MainUI(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    //初始化视图
    private void initView(Context context) {
        this.context = context;
        mScroller = new Scroller(context, new DecelerateInterpolator());
        leftMenu = new FrameLayout(context);
        middleMenu = new FrameLayout(context);
        rightMenu = new FrameLayout(context);
        middleMask = new FrameLayout(context);
        View mainView=(LayoutInflater.from(context).inflate(R.layout.activity_main,this,false));
        LinearLayout llRoot= (LinearLayout) mainView.findViewById(R.id.ll_root);
        final ImageView iv= (ImageView) mainView.findViewById(R.id.iv);
        llRoot.setOnTouchListener(new View.OnTouchListener() {
            float currentDistance;
            float lastDistance = -1; // 记录最有一次所记录的值
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.print("event.getAction()==>" + event.getAction());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getPointerCount() >= 2) { // 判断大于等于两个触摸点

                            float offsetX = event.getX(0) - event.getX(1);
                            float offsetY = event.getY(0) - event.getY(1);
                            // 计算出两个点之间的距离
                            currentDistance = (float) Math.sqrt(offsetX * offsetX
                                    + offsetY * offsetY);

                            if (lastDistance < 0) { // 初始值
                                lastDistance = currentDistance;
                            } else {
                                // 当前距离比上次距离>5个像素，可以识别为放大。
                                if (currentDistance - lastDistance > 5) {
                                    System.out.println("放大");

                                    ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) iv.getLayoutParams();
                                    lp.width = (int) (1.1f * iv.getWidth());
                                    lp.height = (int) (1.1f * iv.getHeight());

                                    iv.setLayoutParams(lp);

                                    lastDistance = currentDistance;
                                } else if (lastDistance - currentDistance > 5) { //
                                    System.out.println("缩小");

                                    ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) iv
                                            .getLayoutParams();
                                    lp.width = (int) (0.9f * iv.getWidth());
                                    lp.height = (int) (0.9f * iv.getHeight());

                                    iv.setLayoutParams(lp);

                                    lastDistance = currentDistance;
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });
        middleMenu.addView(mainView);

        leftMenu.setBackgroundColor(Color.RED);     //设置背景颜色
        middleMenu.setBackgroundColor(Color.WHITE);
        rightMenu.setBackgroundColor(Color.RED);
        middleMask.setBackgroundColor(0x88000000);
        leftMenu.setId(LEFT_ID);
        middleMenu.setId(MIDDLE_ID);
        rightMenu.setId(RIGHT_ID);
        addView(leftMenu);      //添加至View
        addView(middleMenu);
        addView(rightMenu);
        addView(middleMask);
        middleMask.setAlpha(0);     //设置middleMask的透明度
    }

    public float onMiddleMask() {
        System.out.println("透明度：" + middleMask.getAlpha());
        return middleMask.getAlpha();
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        onMiddleMask(); //输出透明度
        int curX = Math.abs(getScrollX());
        float scale = curX / (float) leftMenu.getMeasuredWidth();  //设置透明度的渐变
        middleMask.setAlpha(scale);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        middleMenu.measure(widthMeasureSpec, heightMeasureSpec);
        middleMask.measure(widthMeasureSpec, heightMeasureSpec);
        int realWidth = MeasureSpec.getSize(widthMeasureSpec);      //获取整体屏幕最大宽度
        int tempWidthMeasure = MeasureSpec.makeMeasureSpec(         //测量左右菜单的宽度（为中间宽度的0.8倍）
                (int) (realWidth * 0.8f), MeasureSpec.EXACTLY);
        leftMenu.measure(tempWidthMeasure, heightMeasureSpec);       //左右侧的高度和中间的一样
        rightMenu.measure(tempWidthMeasure, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {      //l，t，r,b分别为中间部分的左，右，上，下边界
        super.onLayout(changed, l, t, r, b);    //设置布局
        middleMenu.layout(l, t, r, b);             //中间部分的四个边界不变
        middleMask.layout(l, t, r, b);             //蒙版的四个边界不变
        leftMenu.layout(1 - leftMenu.getMeasuredWidth(), t, r, b);   //左侧部分的左边边界=中间部分的左边边界-左侧部分的宽度
        rightMenu.layout(
                1 + middleMenu.getMeasuredWidth(),//右侧部分的左边边界则等于中间部分的左边边界加上中间部分的宽度
                t,
                1 + middleMenu.getMeasuredWidth() + rightMenu.getMeasuredWidth(), b);//右侧部分的右边边界等于中间部分的左边边界加上中间部分的宽度加上右侧部分的宽度
    }

    //再用两个布尔值来确定是否在左右滑动
    private boolean isTestCompete;      //测试是否完成,true完成了,false没有完成
    private boolean isleftrightEvent;   //判断是否是左右滑动 左右true，上下false

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (!isTestCompete) {       //没有完成滑动动作
            getEventType(ev);       //继续调用事件进行判断
            return true;
        }
        if (isleftrightEvent) {      // 左右滑动
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_MOVE:
                    int curScrollX = getScrollX();
                    int dis_x = (int) (ev.getX() - point.x);
                    int expectX = -dis_x + curScrollX;//把滑动的多少跟左右菜单的滑动相对应
                    int finalX = 0;
                    if (expectX < 0) {
                        finalX = Math.max(expectX, -leftMenu.getMeasuredWidth());
                    } else {
                        finalX = Math.min(expectX, rightMenu.getMeasuredWidth());
                    }
                    scrollTo(finalX, 0);
                    point.x = (int) ev.getX();
                    break;


                //处理掉手指离开ACTION_UP与滑出边界ACTION_CANCEL
                case MotionEvent.ACTION_UP://手指离开

                case MotionEvent.ACTION_CANCEL://滑出边界
                    curScrollX = getScrollX();
                    if (Math.abs(curScrollX) > leftMenu.getMeasuredWidth() >> 1) {//当大于一半
                        if (curScrollX < 0) {//手指向右滑动，出现左菜单
                            mScroller.startScroll(curScrollX, 0,
                                    -leftMenu.getMeasuredWidth() - curScrollX, 0,
                                    200);
                        } else {//手指向左滑动，出现右菜单
                            mScroller.startScroll(curScrollX, 0,
                                    leftMenu.getMeasuredWidth() - curScrollX, 0,
                                    200);
                        }
                    } else {//小于一半，回到处
                        mScroller.startScroll(curScrollX, 0, -curScrollX, 0, 200);
                    }
                    invalidate();
                    isleftrightEvent = false;
                    isTestCompete = false;
                    break;
            }

        } else {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_UP:
                    isleftrightEvent = false;
                    isTestCompete = false;
                    break;
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (!mScroller.computeScrollOffset()) {
            return;
        }
        int tempX = mScroller.getCurrX();
        scrollTo(tempX, 0);
    }

    private Point point = new Point();
    private static final int TEST_DIS = 20;

    private void getEventType(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                point.x = (int) ev.getX();
                point.y = (int) ev.getY();
                super.dispatchTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE://只要判断出滑动是左右的滑动还是上下的滑动
                int dX = (int) Math.abs(ev.getX() - point.x);
                int dY = (int) Math.abs(ev.getY() - point.y);
                if (dX >= TEST_DIS && dX > dY) { // 左右滑动
                    isleftrightEvent = true;
                    isTestCompete = true;
                    point.x = (int) ev.getX();
                    point.y = (int) ev.getY();
                } else if (dY >= TEST_DIS && dY > dX) { // 上下滑动
                    isleftrightEvent = false;
                    isTestCompete = true;
                    point.x = (int) ev.getX();
                    point.y = (int) ev.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL://屏幕边缘
                super.dispatchTouchEvent(ev);
                isleftrightEvent = false;
                isTestCompete = false;
                break;
        }
    }

}
