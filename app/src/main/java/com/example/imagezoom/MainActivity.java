package com.example.imagezoom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.ll_root)
    LinearLayout clRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        clRoot.setOnTouchListener(new View.OnTouchListener() {
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

                                    ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) iv
                                            .getLayoutParams();
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

    }


}
