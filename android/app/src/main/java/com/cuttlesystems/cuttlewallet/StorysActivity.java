package com.cuttlesystems.cuttlewallet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cuttlesystems.util.Storyz;
import com.cuttlesystems.cuttlewallet.R;

public class StorysActivity extends AppCompatActivity {

    CountDownTimer mCountDownTimer;
    private static final long START_TIME_IN_MILLIS = 10000;
    private static final long MIN_LENGTH_SIDE_ACTION = 200;
    private static final long LENGTH_SIDE_PANEL = 200;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    boolean mTimerRunning;
// -------------------------------------------------------------------------------------------------

    TextView nameWidget;
    TextView infoWidget;
    ImageView imageWidget;
    ProgressBar progressBar;
// -------------------------------------------------------------------------------------------------
    int count;
    float startX = 0;
    float startY = 0;
// -------------------------------------------------------------------------------------------------

    private final Storyz[] storyzArrayList = {
            new Storyz("Your safety is our priority",
                    R.drawable.story_1,
                    "Our crypto wallet uses advanced encryption technology to ensure the " +
                            "safety of your funds. We pay great attention to safety because we " +
                            "know that it is important for you."),
            new Storyz("Ability to store different cryptocurrencies in one place",
                    R.drawable.story_2,
                    "Our crypto wallet allows you to store various cryptocurrencies in " +
                            "one place, which makes managing your investments more convenient " +
                            "and simple."),
            new Storyz("Ease of use",
                    R.drawable.story_3,
                    "Our crypto wallet is easy to use and intuitive. You can send and " +
                            "receive cryptocurrencies quickly and conveniently, as well as " +
                            "track your investments."),
            new Storyz("Reliable support",
                    R.drawable.story_4,
                    "We guarantee reliable support ready to help you with any questions" +
                            " or problems related to our crypto wallet. We are always ready " +
                            "to help our customers and provide the best experience using " +
                            "our product."),
            new Storyz("Manage your investments on the go",
                    R.drawable.story_5,
                    "Our crypto wallet allows you to manage your investments on the go," +
                            " thanks to the mobile app. You can monitor and manage your portfolio" +
                            " wherever you are."),
            new Storyz("Fast and reliable transactions",
                    R.drawable.story_6,
                    "Our crypto wallet provides fast and reliable transactions through the " +
                            "use of advanced technologies and high network performance. " +
                            "You can choose how much commission you want to pay, and based on " +
                            "this, the transaction speed will be calculated"),
            new Storyz("Accessibility and ease of use",
                    R.drawable.story_7,
                    "Our crypto wallet is available for use anytime, anywhere. You can " +
                            "access your investments through our web interface or mobile " +
                            "application, which provide the best user experience.")
    };
// -------------------------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storys);
        getSupportActionBar().hide();

        nameWidget = findViewById(R.id.name_storyz);
        infoWidget = findViewById(R.id.info_storyz);
        imageWidget = findViewById(R.id.image_storyz);
        progressBar = findViewById(R.id.progressBarStoryz);

        count = getIntent().getExtras().getInt("item");

        initializationPreview();
        startTimer();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        float currentX = 0;
        float currentY = 0;
        float deltaX = 0;
        float deltaY = 0;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Log.d("Started params", "x: "+String.valueOf(startX) + " y: "
                //        +String.valueOf(startY));
                startX = event.getX();
                startY = event.getY();
                pauseTimer();
                break;
            case MotionEvent.ACTION_UP:
                currentX = event.getX();
                currentY = event.getY();
                deltaX = currentX - startX;
                deltaY = currentY-startY;
                if (Math.abs(deltaY)>MIN_LENGTH_SIDE_ACTION)
                {
                    resetTimer();
                    pauseTimer();
                    finish();
                    break;
                }
                if (deltaX<=MIN_LENGTH_SIDE_ACTION) {
                    if (Math.abs(deltaX) > MIN_LENGTH_SIDE_ACTION) {
                        goOnBackStory();
                        break;
                    }
                    if ((currentX < LENGTH_SIDE_PANEL)||Math.abs(deltaX) > MIN_LENGTH_SIDE_ACTION){
                        goOnBackStory();
                        break;
                    }
                    else if (currentX < displayMetrics.widthPixels &&
                            currentX > (displayMetrics.widthPixels - LENGTH_SIDE_PANEL)){
                        goOnNextStory();
                        break;
                    }
                } else if (deltaX > MIN_LENGTH_SIDE_ACTION)
                {
                    goOnNextStory();
                    break;
                }
                startTimer();
                break;
        }
        updateCountDownProgressBar();
        return super.onTouchEvent(event);
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownProgressBar();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                finish();
            }
        }.start();

        mTimerRunning = true;
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        startTimer();
        updateCountDownProgressBar();
    }

    public void closeActivityStorysSlot(View v)
    {
        finish();
    }

    private void initializationPreview()
    {
        Storyz item = storyzArrayList[count];
        nameWidget.setText(item.getName());
        infoWidget.setText(item.getInfo());
        imageWidget.setImageResource(item.getImg());
    }

    private void goOnNextStory()
    {
        if (count!=storyzArrayList.length-1) {
            count++;
            resetTimer();
            initializationPreview();
        }
    }

    private void goOnBackStory()
    {
        if (count!=0) {
            count--;
            resetTimer();
            initializationPreview();
        }
    }

    private void updateCountDownProgressBar() {
        progressBar.setProgress((int) (
                ((double)(START_TIME_IN_MILLIS-mTimeLeftInMillis) /
                        START_TIME_IN_MILLIS)
                        *100));
    }
}