package com.artactivo.gridlayouttest;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnTouchListener {

    private final static int NUM_COLUMNS = 3;
    private final String LOGCAT = "Grid Layout Test";
    private String levelCode = "111100101010101101010011110001010101010100010101010101010100010101001010010101001010100101010101010010";
    private int[] tileData = new int[NUM_COLUMNS * NUM_COLUMNS];
    private int solvedTiles = NUM_COLUMNS * NUM_COLUMNS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createBoard();
    }

    /**
     * This method populates the background board and the lit tiles
     *
     */
    private void createBoard() {
        //figures out the smallest screen side available to get the board tile size
        int dimension = 0;
        int statusBarHeight = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resId);
        }
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
            dimension = displayMetrics.heightPixels - statusBarHeight - (int) (convertDpToPx(16, this) * 2);
        } else {
            dimension = displayMetrics.widthPixels - (int) (convertDpToPx(16, this) * 2);
        }
        final int TILE_SIZE = (int) dimension / NUM_COLUMNS;

        //populates the background board with off tiles
        ImageView image;
        LayoutParams lp;
        lp = new LayoutParams(TILE_SIZE, TILE_SIZE);
        GridLayout backBoard = (GridLayout) findViewById(R.id.board_background);
        backBoard.setColumnCount(NUM_COLUMNS);
        for (int i = 0; i < NUM_COLUMNS * NUM_COLUMNS; i++) {
            image = new ImageView(this);
            image.setLayoutParams(lp);
            image.setImageResource(R.drawable.tile_off);
            backBoard.addView(image);
        }

        //populates the foreground board with lit tiles
        // with alpha 0 or 1 depending on the visibility
        GridLayout board = (GridLayout) findViewById(R.id.board);
        board.setColumnCount(NUM_COLUMNS);
        for (int i = 0; i < NUM_COLUMNS * NUM_COLUMNS; i++) {
            image = new ImageView(this);
            image.setLayoutParams(lp);
            image.setId(i);
            image.setOnTouchListener(this);
            image.setImageResource(R.drawable.tile_on);
            if (levelCode.substring(i, i + 1).equals("0")) {
                image.setAlpha(1.0f);
                solvedTiles --;
                tileData[i] = 0;
            } else if (levelCode.substring(i, i + 1).equals("1")) {
                image.setAlpha(0.0f);
                tileData[i] = 1;
            }
            board.addView(image);
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            //Toast.makeText(this, "View ID: " + view.getId(), Toast.LENGTH_SHORT).show();
            changeTiles(view.getId());
            return true;
        } else {
            return false;
        }
    }

    private void changeTiles(int id) {
        int posX = id % NUM_COLUMNS;
        int posY = id / NUM_COLUMNS;
        changeSingleTile(id);
        if (posX != 0) {
            changeSingleTile(id - 1);
        }
        if (posX != NUM_COLUMNS - 1) {
            changeSingleTile(id + 1);
        }
        if (posY != 0) {
            changeSingleTile(id - NUM_COLUMNS);
        }
        if (posY != NUM_COLUMNS - 1) {
            changeSingleTile(id + NUM_COLUMNS);
        }
        if (checkBoard()) {
            TextView textView = (TextView) findViewById(R.id.messages);
            textView.setText(R.string.greeting_message);
            // END GAME
        }
    }

    private void changeSingleTile(int id) {
        ImageView image = (ImageView) findViewById(id);
        if (tileData[id] == 0) {                         //if it's the blue tile
            animateFade(image, 0);
            //image.setImageResource(R.drawable.tile_off); //set as off
            solvedTiles ++;
            tileData[id] = 1;
        } else if (tileData[id] == 1) {                  // if it's the off tile
            //image.setImageResource(R.drawable.tile_on);  // set as blue
            animateFade(image, 1);
            solvedTiles --;
            tileData[id] = 0;
        }
    }

    private boolean checkBoard() {
        return solvedTiles == NUM_COLUMNS * NUM_COLUMNS;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPx(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method animates the fade in-out of an ImageView
     *
     * @param imageView the ImageView object
     * @param id the identifier location on the board
     */
    public void animateFade(ImageView imageView, int id) {
        Animation fade = null;
        if (id == 1) {
            fade = new AlphaAnimation(0, 1);
        } else {
            fade = new AlphaAnimation(1, 0);
        }
        fade.setInterpolator(new DecelerateInterpolator());
        fade.setDuration(500);
        fade.setFillAfter(true);
        imageView.setAlpha(1.0f);
        imageView.startAnimation(fade);
    }
}