package edu.dartmouth.cs.myorganizer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;

public class TextActivity extends AppCompatActivity {

    TextSwitcher tv;
    int imageIdx = 0;
    GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        Intent i = getIntent();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tv = (TextSwitcher) findViewById(R.id.textSwitcher);

        // specify the in/out animations you wish to use
        tv.setInAnimation(getApplicationContext(), android.R.anim.slide_in_left);
        tv.setOutAnimation(getApplicationContext(), android.R.anim.slide_out_right);

        // provide two TextViews for the TextSwitcher to use
        // you can apply styles to these Views before adding
        TextView tv1 = new TextView(getApplicationContext());
        TextView tv2 = new TextView(getApplicationContext());


        tv1.setTextColor(getResources().getColor(android.R.color.black));
        tv2.setTextColor(getResources().getColor(android.R.color.black));
        tv1.setTextSize(22);
        tv2.setTextSize(22);

        tv.addView(tv1);
        tv.addView(tv2);

        Intent intent = getIntent();
        tv.setText(intent.getStringExtra("text"));

        class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

            final String TAG = MyGestureDetector.class.getSimpleName();

            // for touch left or touch right events
            private static final int SWIPE_MIN_DISTANCE = 50;   //default is 120
            private static final int SWIPE_MAX_OFF_PATH = 400;
            private static final int SWIPE_THRESHOLD_VELOCITY = 50;

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDown(MotionEvent e) {

                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                Log.d(TAG, " on filing event, first velocityX :" + velocityX +
                        " second velocityY" + velocityY);
                try {
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                        return false;
                    if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        onHorizonTouch(true);  // left
                    }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        onHorizonTouch(false); // right
                    }
                } catch (Exception e) {
                    // nothing
                }
                return false;
            }

            void onHorizonTouch(Boolean toLeft) {

                if(!toLeft && imageIdx>0) {
                    tv.setInAnimation(AnimationUtils.loadAnimation(
                            getApplicationContext(), android.R.anim.fade_in));
                    tv.setOutAnimation(AnimationUtils.loadAnimation(
                            getApplicationContext(), android.R.anim.fade_out));
                    imageIdx--;
                   tv.setText("Text1");
                }
                if(toLeft && imageIdx<1) {
                    tv.setInAnimation(AnimationUtils.loadAnimation(
                            getApplicationContext(), android.R.anim.fade_in));
                    tv.setOutAnimation(AnimationUtils.loadAnimation(
                            getApplicationContext(), android.R.anim.fade_out));
                    imageIdx++;
                  tv.setText("Text2");
                }
            }
        }

        tv.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        tv.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));
        //start for touch events  Gesture detection
        gestureDetector = new GestureDetector(new MyGestureDetector());
        View.OnTouchListener gestureListener = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
        tv.setOnTouchListener(gestureListener);
    }
}
