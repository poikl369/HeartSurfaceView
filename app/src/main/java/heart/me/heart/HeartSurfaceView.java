package heart.me.heart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yun.liu@avazu.net on 2016/3/21.
 */
public class HeartSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private List<BitmapBean> mDisplayList = new ArrayList<>();
    private SurfaceHolder mSurfaceHolder;
    private boolean mIsDrawOk = false;
    private Context context;
    private int screenHeight, end_y, start_y;
    private Map<Integer, Bitmap> heartMap = new HashMap<>();
    private Bitmap board = null;
    private Canvas boardCanvas = null;
    private Rect mRect = new Rect();

    public HeartSurfaceView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public HeartSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public HeartSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private int widthMeasureSpec, heightMeasureSpec;
    private boolean isFirst = true;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isFirst) {
            this.widthMeasureSpec = widthMeasureSpec;
            this.heightMeasureSpec = heightMeasureSpec;
        }
        isFirst = false;
        super.onMeasure(this.widthMeasureSpec, this.heightMeasureSpec);
    }

    private void initData() {
        heartMap.put(R.mipmap.icon_heart_0, BitmapFactory.decodeResource(getResources(), R.mipmap.icon_heart_0));
        heartMap.put(R.mipmap.icon_heart_1, BitmapFactory.decodeResource(getResources(), R.mipmap.icon_heart_1));
        heartMap.put(R.mipmap.icon_heart_2, BitmapFactory.decodeResource(getResources(), R.mipmap.icon_heart_2));
        heartMap.put(R.mipmap.icon_heart_3, BitmapFactory.decodeResource(getResources(), R.mipmap.icon_heart_3));
        heartMap.put(R.mipmap.icon_heart_4, BitmapFactory.decodeResource(getResources(), R.mipmap.icon_heart_4));
        heartMap.put(R.mipmap.icon_heart_5, BitmapFactory.decodeResource(getResources(), R.mipmap.icon_heart_5));
        heartMap.put(R.mipmap.icon_heart_6, BitmapFactory.decodeResource(getResources(), R.mipmap.icon_heart_6));
        heartMap.put(R.mipmap.icon_heart_7, BitmapFactory.decodeResource(getResources(), R.mipmap.icon_heart_7));
        heartMap.put(R.mipmap.icon_shocked, BitmapFactory.decodeResource(getResources(), R.mipmap.icon_shocked));
        heartMap.put(R.mipmap.icon_happy, BitmapFactory.decodeResource(getResources(), R.mipmap.icon_happy));
        heartMap.put(R.mipmap.icon_neutral, BitmapFactory.decodeResource(getResources(), R.mipmap.icon_neutral));
        heartMap.put(R.mipmap.icon_tongue, BitmapFactory.decodeResource(getResources(), R.mipmap.icon_tongue));
    }

    private Paint paint;
    private DrawThread drawThread;
    private boolean isRunning = true;

    private void init() {
        drawThread = new DrawThread();

        initData();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        setZOrderMediaOverlay(true);
        setWillNotCacheDrawing(true);
        setDrawingCacheEnabled(false);
        setWillNotDraw(true);
        setZOrderOnTop(true);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        screenHeight = ScreenUtils.getScreenHeight(context);
        start_y = screenHeight - SizeUtils.dp2Px(context, 77) - ScreenUtils.getStatusBarHeight(context);
        end_y = SizeUtils.dp2Px(context, 52);
    }

    public void addHeart(String str) {
        int drawableId;
        if (str.equals("like"))
            drawableId = random1();
        else
            drawableId = Heart.getDrawableId(str);
        BitmapBean bitmapBean = new BitmapBean();
        bitmapBean.setBitmap(heartMap.get(drawableId));
        bitmapBean.setP1(new PointF(150, start_y));
        int offSetWidth = getOffSetWidth();
        bitmapBean.setP2(new PointF(offSetWidth, start_y / 3));
        int offSetWidth1 = getOffSetWidth();
        bitmapBean.setP3(new PointF(offSetWidth1, start_y / 3 * 2));
        bitmapBean.setP4(new PointF(150, 0));
        if (mDisplayList.size() < 30) {
            mDisplayList.add(bitmapBean);
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsDrawOk = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        board = Bitmap.createBitmap(400, this.getHeight(),
                Bitmap.Config.ARGB_8888);
        boardCanvas = new Canvas(board);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawOk = false;
    }

    public void start() {
        drawThread.start();
    }

    public void clear() {
        isRunning = false;
        Iterator<Map.Entry<Integer, Bitmap>> it = heartMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Bitmap> entry = it.next();
            entry.getValue().recycle();
        }
        if (board != null)
            board.recycle();
        heartMap.clear();
    }

    private void draw() {
        synchronized (this) {
            long startTime = System.currentTimeMillis();
            if (!mIsDrawOk) {
                return;
            }
            if (boardCanvas != null) {
                boardCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }
            for (int i = 0; i < mDisplayList.size(); i++) {
                BitmapBean bitmapBean = mDisplayList.get(i);
                if (bitmapBean.time >= 1) {
                    mDisplayList.remove(i);
                }
            }

            mRect.left = getWidth() - 400;
            mRect.top = end_y;
            mRect.right = getWidth();
            mRect.bottom = getHeight();
            Canvas canvas = mSurfaceHolder.lockCanvas(mRect);
            if (canvas == null) return;
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            try {
                for (int i = 0; i < mDisplayList.size(); i++) {
                    BitmapBean bitmapBean = mDisplayList.get(i);
                    bitmapBean.draw();
                }

                if (canvas != null && board != null) {
                    canvas.drawBitmap(board, getWidth() - 400, 0, null);
                }
                long endTime = System.currentTimeMillis();
                long frameRate = endTime - startTime;
                if (frameRate > 20)
                    Log.e("===", " time 2 : " + frameRate);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mSurfaceHolder != null && canvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

        }
    }

    /**
     * 计算塞贝儿曲线
     *
     * @param time       时间，范围0-1
     * @param startValue 起始点
     * @param pointF1    拐点1
     * @param pointF2    拐点2
     * @param endValue   终点
     * @return 塞贝儿曲线在当前时间下的点
     */
    public PointF evaluate(float time, PointF startValue, PointF pointF1, PointF pointF2,
                           PointF endValue) {

        float timeLeft = 1.0f - time;
        PointF point = new PointF();//结果
        PointF point0 = startValue;//起点
        PointF point3 = endValue;//终点
        //代入公式
        point.x = timeLeft * timeLeft * timeLeft * (point0.x)
                + 3 * timeLeft * timeLeft * time * (pointF1.x)
                + 3 * timeLeft * time * time * (pointF2.x)
                + time * time * time * (point3.x);

        point.y = timeLeft * timeLeft * timeLeft * (point0.y)
                + 3 * timeLeft * timeLeft * time * (pointF1.y)
                + 3 * timeLeft * time * time * (pointF2.y)
                + time * time * time * (point3.y);
        return point;
    }

    Random random = new Random();

    private int getOffSetWidth() {
        return random.nextInt(400) - random.nextInt(200) + SizeUtils.dp2Px(context, 30);
    }

    private int getRandomRotate() {
        return random.nextInt(30) - random.nextInt(30);
    }

    public class BitmapBean {
        private Bitmap bitmap;
        private float time = 0;
        private PointF p1;
        private PointF p2;
        private PointF p3;
        private PointF p4;
        private float size = 0;
        private int alpha = 255;
        private int rotate = getRandomRotate();
        private boolean isRotateLeft = true;

        public PointF getP1() {
            return p1;
        }

        public void setP1(PointF p1) {
            this.p1 = p1;
        }

        public PointF getP2() {
            return p2;
        }

        public void setP2(PointF p2) {
            this.p2 = p2;
        }

        public PointF getP3() {
            return p3;
        }

        public void setP3(PointF p3) {
            this.p3 = p3;
        }

        public PointF getP4() {
            return p4;
        }

        public void setP4(PointF p4) {
            this.p4 = p4;
        }

        public float getTime() {
            return time;
        }

        public void setTime(float time) {
            this.time = time;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public void draw() {
            float time = getTime();
            PointF point = evaluate(time, getP1(), getP2(), getP3(), getP4());
            time += 0.0039f;
            float x = point.x;
            float y = point.y;
            if (alpha > 0)
                paint.setAlpha(alpha--);
            else
                paint.setAlpha(0);
            if (rotate >= -30 && rotate <= 30) {
                if (rotate == 30) {
                    isRotateLeft = false;
                } else if (rotate == -30) {
                    isRotateLeft = true;
                }
                if (isRotateLeft)
                    rotate++;
                else
                    rotate--;
            }
            Matrix mMatrix = new Matrix();
//            mMatrix.postRotate(rotate);
            if (size < 1)
                size += 0.1;
            mMatrix.postScale(size, size);
            mMatrix.postTranslate(x, y);
            boardCanvas.drawBitmap(getBitmap(), mMatrix, paint);
            setTime(time);
        }

    }

    class DrawThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (isRunning) {
                draw();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int random1() {
        int[] heartArray = {R.mipmap.icon_heart_0, R.mipmap.icon_heart_1, R.mipmap.icon_heart_2, R.mipmap.icon_heart_3, R.mipmap.icon_heart_4, R.mipmap.icon_heart_5, R.mipmap.icon_heart_6, R.mipmap.icon_heart_7, R.mipmap.icon_shocked, R.mipmap.icon_happy, R.mipmap.icon_neutral, R.mipmap.icon_tongue};
        int index = (int) (Math.random() * heartArray.length);
        return heartArray[index];
    }

}
