package heart.me.heart;

import android.view.GestureDetector;
import android.view.MotionEvent;


/**
 * Created by yun.liu@avazu.net on 2016/5/6.
 */
public class HeartGestureListener extends GestureDetector.SimpleOnGestureListener {

    public static final int GESTURE_RIGHT = 0x01;
    public static final int GESTURE_LEFT = 0x02;
    public static final int GESTURE_DOWN = 0x03;
    public static final int GESTURE_UP = 0x04;
    private OnHeartGestureListener onGestureListener;


    public HeartGestureListener(OnHeartGestureListener onGestureListener) {
        this.onGestureListener = onGestureListener;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        onGestureListener.onSingleTapUp();
        return super.onSingleTapUp(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float x = e2.getX() - e1.getX();
        float y = e2.getY() - e1.getY();
        //限制必须得划过屏幕80才能算划过
        float x_limit = 80;
        float y_limit = 80;
        float x_abs = Math.abs(x);
        float y_abs = Math.abs(y);
        if (x_abs >= y_abs) {
            if (x > x_limit || x < -x_limit) {
                if (x > 0) {
                    onGestureListener.onFling(GESTURE_RIGHT);
                } else if (x <= 0) {
                    onGestureListener.onFling(GESTURE_LEFT);
                }
            }
        } else {
            if (y > y_limit || y < -y_limit) {
                if (y > 0) {
                    onGestureListener.onFling(GESTURE_DOWN);
                } else if (y <= 0) {
                    onGestureListener.onFling(GESTURE_UP);
                }
            }
        }
        return true;
    }

    public interface OnHeartGestureListener {
        void onSingleTapUp();

        void onFling(int direction);
    }
}