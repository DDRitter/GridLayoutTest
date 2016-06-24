package com.artactivo.gridlayouttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import static android.support.v7.app.ActionBar.*;

public class MainActivity extends AppCompatActivity implements OnTouchListener {

    private final static int NUM_COLUMNS = 4;
    private int[][] tileData = new int[NUM_COLUMNS][NUM_COLUMNS];
    private int solvedTiles = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createBoard();
    }



    private void createBoard() {
        int dimension = 0;
        int statusBarHeight = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resId);
        }
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
            dimension = displayMetrics.heightPixels - statusBarHeight;
        } else {
            dimension = displayMetrics.widthPixels;
        }
        final int TILE_SIZE = (int) dimension / NUM_COLUMNS;
        ImageView[][] tile = new ImageView[NUM_COLUMNS][NUM_COLUMNS];

        //float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        //float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        GridLayout board = (GridLayout) findViewById(R.id.board);
        board.setColumnCount(NUM_COLUMNS);
        LayoutParams lp = new LayoutParams(TILE_SIZE, TILE_SIZE);

        for (int i = 0; i < NUM_COLUMNS; i++) {
            for (int j = 0; j < NUM_COLUMNS; j++) {
                tile[i][j] = new ImageView(this);
                tile[i][j].setImageResource(R.drawable.tile_off);
                tile[i][j].setLayoutParams(lp);
                tile[i][j].setId(i * NUM_COLUMNS + j);
                tile[i][j].setOnTouchListener(this);
                tileData[i][j] = 0;
                board.addView(tile[i][j]);
            }
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
        changeSingleTile(id, posX, posY);
        if (posX != 0) {
            changeSingleTile(id - 1, posX - 1, posY);
        }
        if (posX != NUM_COLUMNS - 1) {
            changeSingleTile(id + 1, posX + 1, posY);
        }
        if (posY != 0) {
            changeSingleTile(id - NUM_COLUMNS, posX, posY - 1);
        }
        if (posY != NUM_COLUMNS - 1) {
            changeSingleTile(id + NUM_COLUMNS, posX, posY + 1);
        }
        if (checkBoard()) {
            TextView textView = (TextView) findViewById(R.id.messages);
            textView.setText(R.string.greeting_message);
        };
    }

    private void changeSingleTile(int id, int posX, int posY) {
        ImageView image = (ImageView) findViewById(id);
        if (tileData[posX][posY] == 0) {
            image.setImageResource(R.drawable.tile_on);
            solvedTiles ++;
            tileData[posX][posY] = 1;
        } else {
            image.setImageResource(R.drawable.tile_off);
            solvedTiles --;
            tileData[posX][posY] = 0;
        }
    }

    private boolean checkBoard() {
        return solvedTiles == NUM_COLUMNS * NUM_COLUMNS;
    }
}
