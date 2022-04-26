package com.example.hitthetarget;

import static java.lang.Integer.min;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    DrawingSpace myDrawing;
    CountDownTimer timer = null;
    LinearLayout ll;
    Button playButton;
    TextView scoreTV;
    long timerMilis = 5000;
    boolean ifPlay = false;
    private long milliLeft = 0;
//    int score=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int minSize = min(displayMetrics.heightPixels, displayMetrics.widthPixels);


        myDrawing = new DrawingSpace(this, minSize);
        setContentView(R.layout.activity_main);
        ll = findViewById(R.id.ll);
        ll.addView(myDrawing);
        playButton = findViewById(R.id.startButton);
        scoreTV = findViewById(R.id.score);
        initNoPlay();
        System.out.println("###################### onCreate ");
    }

    protected void initNoPlay() {
        myDrawing.setVisibility(View.GONE);
        playButton.setVisibility(View.VISIBLE);
        scoreTV.setVisibility(View.VISIBLE);
        scoreTV.setText(String.format(Locale.US,"%s%d", getString(R.string.yourScoreIs), myDrawing.score));
        cancelTimer();
    }

    protected void initPlay() {
        myDrawing.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.GONE);
        scoreTV.setVisibility(View.GONE);
        scoreTV.setText(String.format(Locale.US,"%s%d", getString(R.string.yourScoreIs), myDrawing.score));
        startTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ifPlay) {
            initPlay();
        } else {
            initNoPlay();
        }
        System.out.println("###################### onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelTimer();
        System.out.println("###################### onPause");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle data) {
        super.onSaveInstanceState(data);
        data.putLong("milliLeft", milliLeft);
        data.putLong("timerMilis", timerMilis);
        data.putInt("score", myDrawing.score);
        data.putBoolean("ifPlay", ifPlay);
        System.out.println("###################### onSaveInstanceState");
    }


    @Override
    protected void onRestoreInstanceState(Bundle data) {
        super.onRestoreInstanceState(data);
        milliLeft = data.getLong("milliLeft");
        timerMilis = data.getLong("timerMilis");
        myDrawing.score = data.getInt("score");
        ifPlay = data.getBoolean("ifPlay");
        System.out.println("###################### onRestoreInstanceState");
    }

    void startTimer() {
        timer = new CountDownTimer(timerMilis, 1000) {
            public void onTick(long millisUntilFinished) {
                milliLeft = millisUntilFinished;
            }

            public void onFinish() {
                ifPlay = false;
                initNoPlay();
            }
        };
        timer.start();
    }


    void cancelTimer() {
        if (timer != null)
            timer.cancel();
    }


    public void startGame(View view) {
        myDrawing.score = 0;
        initPlay();
        ifPlay = true;
    }

}


class DrawingSpace extends View implements View.OnTouchListener {


    float xCircle, yCircle;
    int radius=100;
    final int N = 4;
    final int[] radii = new int[N];
    float xTouch, yTouch;
    int score = 0;

    final Paint paint = new Paint();
    final Paint paintInner = new Paint();

    public DrawingSpace(Context context) {
        super(context);
    }

    public DrawingSpace(Context context, int maxScreenSize) {
        super(context);
        radius = (int) (maxScreenSize / 10);
        init();
    }


    public void onDraw(Canvas canvas) {
        xCircle = ThreadLocalRandom.current().nextInt(2 * radius, getWidth() - 2 * radius + 1);
        yCircle = ThreadLocalRandom.current().nextInt(2 * radius, getHeight() - 2 * radius + 1);
        canvas.drawColor(Color.WHITE);
        for (int i = 0; i < (N - 1); i++) {
            canvas.drawCircle(xCircle, yCircle, radii[i], paint);
        }
        canvas.drawCircle(xCircle, yCircle, radii[N - 1], paintInner);
//        System.out.println("###################### ondraw");
    }


    public void init() {

        for (int i = 0; i < 4; i++) {
            radii[i] = (int) ((N - i) * radius / N);
        }
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((int) (radius / 10));
        paintInner.setColor(Color.RED);
        setOnTouchListener(this);
        System.out.println("init");
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        xTouch = motionEvent.getX();
        yTouch = motionEvent.getY();
        float distanceToCenter = (xTouch - xCircle) * (xTouch - xCircle) + (yTouch - yCircle) * (yTouch - yCircle);
        if (distanceToCenter <= radius * radius) {
            score += 1;
            invalidate();
        }
        return false;
    }
}