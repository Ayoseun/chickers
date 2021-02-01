package com.mushroom.chickers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    // high declarations
    private static GestureDetectGridView mGridView;
    private static final int COLUMNS = 3;
    private static final int DIMENSIONS = COLUMNS * COLUMNS;
    private static int mColumnWidth, mColumnHeight;
  @SuppressLint("StaticFieldLeak")
   private static  TextView textView;
    ImageButton puzzleBtn;
    private static MediaPlayer playThis;
    public static final String up = "up";
    public static final String down = "down";
    public static final String left = "left";
    public static final String right = "right";
    private int seconds = 30;
    private static String[] tileList;
    SeekBar seekBar;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        scramble();

        setDimensions();
        seekBar =(SeekBar)findViewById(R.id.seekBar);
        puzzleBtn = (ImageButton) findViewById(R.id.puzzleBtn);
        textView = (TextView) findViewById(R.id.textView);
       playThis =MediaPlayer.create(this,R.raw.swiper);


         //seekbar here
       seekBar.setMax(seconds);
       seekBar.setProgress(seconds);
       seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               if(fromUser){
                   seconds = progress;
               }
           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {

           }
       });
       startTimer(seconds * 1000);
    }


  //this is the timer
    public void startTimer(final long milliseconds) {

        if (countDownTimer != null) {

            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(milliseconds,500){

            @Override
            public void onTick(long leftTimeInMilliseconds) {
                int Edu = (int) leftTimeInMilliseconds / 1000;
                seekBar.setProgress(Edu);
                TimeUnit.MILLISECONDS.toMinutes(leftTimeInMilliseconds);
                TimeUnit.MILLISECONDS.toSeconds(leftTimeInMilliseconds);
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(leftTimeInMilliseconds));
            }

            @Override
            public void onFinish() {

                if(isSolved()){
                    seekBar.setProgress(0);
                }
               else if( seconds==0)
                   display(getApplicationContext());
                   scramble();
             textView.setText("Game Over");
            }
        }.start();
    }

    private void init() {
        mGridView = (GestureDetectGridView) findViewById(R.id.grid);
        mGridView.setNumColumns(COLUMNS);

        tileList = new String[DIMENSIONS];
        for (int i = 0; i < DIMENSIONS; i++) {
            tileList[i] = String.valueOf(i);
        }
    }

    private void scramble() {
        int index;
        String temp;
        Random random = new Random();

        for (int i = tileList.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = tileList[index];
            tileList[index] = tileList[i];
            tileList[i] = temp;
        }
    }

    private void setDimensions() {
        ViewTreeObserver vto = mGridView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int displayWidth = mGridView.getMeasuredWidth();
                int displayHeight = mGridView.getMeasuredHeight();

                //int statusbarHeight = getStatusBarHeight(getApplicationContext());
                //int requiredHeight = displayHeight - statusbarHeight;

                mColumnWidth = displayWidth / COLUMNS;
                mColumnHeight = displayHeight / COLUMNS;

                display(getApplicationContext());
            }
        });
    }

    //private int getStatusBarHeight(Context context) {
        //int result = 0;
        //int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                //"android");

        //if (resourceId > 0) {
            //result = context.getResources().getDimensionPixelSize(resourceId);
        //}

        //return result;
    //}

    private static void display(Context context) {
        ArrayList<Button> buttons = new ArrayList<>();
        Button button;

        for (int i = 0; i < tileList.length; i++) {
            button = new Button(context);

            if (tileList[i].equals("0"))
                button.setBackgroundResource(R.drawable.pjmask_peices1);
            else if (tileList[i].equals("1"))
                button.setBackgroundResource(R.drawable.pjmask_peices2);
            else if (tileList[i].equals("2"))
                button.setBackgroundResource(R.drawable.pjmask_peices3);
            else if (tileList[i].equals("3"))
                button.setBackgroundResource(R.drawable.pjmask_peices4);
            else if (tileList[i].equals("4"))
                button.setBackgroundResource(R.drawable.pjmask_peices5);
            else if (tileList[i].equals("5"))
                button.setBackgroundResource(R.drawable.pjmask_peices6);
            else if (tileList[i].equals("6"))
                button.setBackgroundResource(R.drawable.pjmask_peices7);
            else if (tileList[i].equals("7"))
                button.setBackgroundResource(R.drawable.pjmask_peices8);
            else if (tileList[i].equals("8"))
                button.setBackgroundResource(R.drawable.pjmask_peices9);

            buttons.add(button);
        }

        mGridView.setAdapter(new CustomAdapter(buttons, mColumnWidth, mColumnHeight));
    }

    private static void swap(Context context, int currentPosition, int swap) {
        String newPosition = tileList[currentPosition + swap];
        tileList[currentPosition + swap] = tileList[currentPosition];
        tileList[currentPosition] = newPosition;
        display(context);

        if (isSolved())

            textView.setText("Nice !!!");
    }

    public static void moveTiles(Context context, String direction, int position) {

        // Upper-left-corner tile
        if (position == 0) {

            if (direction.equals(right)) swap(context, position, 1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else textView.setText("Wrong move");  playThis.start();

            // Upper-center tiles
        } else if (position > 0 && position < COLUMNS - 1) {
            if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else textView.setText("Wrong move"); playThis.start();

            // Upper-right-corner tile
        } else if (position == COLUMNS - 1) {
            if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else textView.setText("Wrong move"); playThis.start();

            // Left-side tiles
        } else if (position > COLUMNS - 1 && position < DIMENSIONS - COLUMNS &&
                position % COLUMNS == 0) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else textView.setText("Wrong swap"); playThis.start();

            // Right-side AND bottom-right-corner tiles
        } else if (position == COLUMNS * 2 - 1 || position == COLUMNS * 3 - 1) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) {

                // Tolerates only the right-side tiles to swap downwards as opposed to the bottom-
                // right-corner tile.
                if (position <= DIMENSIONS - COLUMNS - 1) swap(context, position,
                        COLUMNS);
                else textView.setText("Wrong swap"); playThis.start();
            } else textView.setText("Wrong move"); playThis.start();

            // Bottom-left corner tile
        } else if (position == DIMENSIONS - COLUMNS) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else textView.setText("Swap again"); playThis.start();

            // Bottom-center tiles
        } else if (position < DIMENSIONS - 1 && position > DIMENSIONS - COLUMNS) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(right)) swap(context, position, 1);
            else textView.setText("Wrong move"); playThis.start();

            // Center tiles
        } else {
            if (direction.equals(up))   swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(right)) swap(context, position, 1);
            else swap(context, position, COLUMNS); playThis.start();
        }
    }

    private static boolean isSolved() {
        boolean solved = false;

        for (int i = 0; i < tileList.length; i++) {
            if (tileList[i].equals(String.valueOf(i))) {
                solved = true;
            } else {
                solved = false;
                break;
            }
        }

        return solved;
    }
}