package com.example.whackamolegame;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.Random;

public class PlayGameActivity extends AppCompatActivity implements View.OnClickListener, Finals{

    int popTime = 3, location = 0;
    TextView txtTimer, txtScore, txtMiss;
    Player player;
    GridLayout grid;
    Button[] button;
    CountDownTimer timer;
    MediaPlayer mp_lastMin, mp_hit, mp_miss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
        player = (Player) getIntent().getSerializableExtra("player");

        txtTimer = findViewById(R.id.txt_timer);
        txtScore = findViewById(R.id.txt_score);
        txtMiss = findViewById(R.id.txt_miss);
        grid = findViewById(R.id.grid_layout_play);

        button = new Button[SIZE];
        for(int i = 0; i < grid.getChildCount(); i++) {
            button[i] = (Button) grid.getChildAt(i);
            button[i].setOnClickListener(this);
        }


        timer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                txtTimer.setText(String.valueOf(millisUntilFinished / 1000));

                if((millisUntilFinished/1000) == 5 )
                    playLastSecondsSound(millisUntilFinished/1000);
                else if((millisUntilFinished/1000) == 0)
                    mp_lastMin.stop();

                if ((millisUntilFinished/1000) <= 5 )
                    txtTimer.setTextColor(Color.RED);

                txtScore.setText(String.valueOf(player.getScore()) + " / " + WIN_SCORE);
                txtMiss.setText(String.valueOf(player.getMisses()) + " / " + MAX_MISSES);
                popTime--;
                if(popTime == 0) {
                    button[location].setBackgroundResource(R.drawable.mole_hole_new);
                    showMole();
                }
            }

            public void onFinish() {
                playLoseSound();
                gameOver();
            }

        }.start();


    }

    public void showMole() {
        Random rand = new Random();
        popTime = rand.nextInt(4-2) + 2;
        location = rand.nextInt(8);
        button[location].setBackgroundResource(R.drawable.out_hole);
    }

    public boolean checkHit(Button clickedButton) {
        return clickedButton.getBackground().getConstantState() == getResources().getDrawable(R.drawable.out_hole).getConstantState();
    }

    public void gameOver() {
        Intent intent = new Intent(this, EndGameActivity.class);
        intent.putExtra("player", player);
        startActivity(intent);
        timer.cancel();
        finish();
    }

    public void playLastSecondsSound(long sec) {

        new Runnable(){
            @Override
            public void run() {
                mp_lastMin = MediaPlayer.create(getBaseContext(), (R.raw.tick));
                mp_lastMin.start();
            }
        }.run();

    }

    public void playHitSound() {

        new Runnable(){
            @Override
            public void run() {
                mp_hit = MediaPlayer.create(getBaseContext(), (R.raw.sword_hit));
                mp_hit.start();
            }
        }.run();
    }

    public void playMissSound() {

        new Runnable(){
            @Override
            public void run() {
                mp_miss = MediaPlayer.create(getBaseContext(), (R.raw.hodvent));
                mp_miss.start();
            }
        }.run();
    }

    public void playWinSound() {

        new Runnable(){
            @Override
            public void run() {
                mp_miss = MediaPlayer.create(getBaseContext(), (R.raw.applause));
                mp_miss.start();
            }
        }.run();
    }

    public void playLoseSound() {

        new Runnable(){
            @Override
            public void run() {
                mp_miss = MediaPlayer.create(getBaseContext(), (R.raw.booz));
                mp_miss.start();
            }
        }.run();
    }


    @Override
    public void onClick(View view) {
        if(view instanceof Button) {
            Button clickedButton = findViewById(view.getId());
            if(checkHit(clickedButton)) {
                playHitSound();
                player.increaseScore();
                clickedButton.setBackgroundResource(R.drawable.mole_hole_new);
                if(player.getScore() >= WIN_SCORE) {
                    playWinSound();
                    gameOver();
                }
            }
            else {
                playMissSound();
                player.increaseMisses();
                if(player.getMisses() >= MAX_MISSES) {
                    playLoseSound();
                    gameOver();
                }
            }
        }
    }

}