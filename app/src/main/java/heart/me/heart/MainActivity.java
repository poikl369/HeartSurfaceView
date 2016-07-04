package heart.me.heart;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private HeartSurfaceView heartSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        heartSurfaceView = (HeartSurfaceView) findViewById(R.id.heart_SurfaceView);
        heartSurfaceView.start();
        handler.sendEmptyMessage(0);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            heartSurfaceView.addHeart("like");
            handler.sendEmptyMessageDelayed(0,150);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        heartSurfaceView.clear();
    }
}
