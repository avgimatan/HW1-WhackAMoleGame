package com.example.whackamolegame;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PlayGameActivity extends AppCompatActivity implements View.OnClickListener, Finals, LocationListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private int popTime = 3, location = 0, location2 = 0;
    private TextView txtTimer, txtScore, txtMiss;
    private Player player;
    private GridLayout grid;
    private Button[] button;
    private CountDownTimer timer;
    private MediaPlayer mp_lastMin, mp_hit, mp_miss, mp_bomb_hit;

    private Location currentLocation = null;
    private LocationManager locationManager;

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
        for (int i = 0; i < grid.getChildCount(); i++) {
            button[i] = (Button) grid.getChildAt(i);
            button[i].setOnClickListener(this);
        }

        if (checkPermission()){
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            setLocation();
        }

        timer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                txtTimer.setText(String.valueOf(millisUntilFinished / 1000));

                if ((millisUntilFinished / 1000) == 5)
                    playLastSecondsSound(millisUntilFinished / 1000);
                else if ((millisUntilFinished / 1000) == 0)
                    mp_lastMin.stop();

                if ((millisUntilFinished / 1000) <= 5)
                    txtTimer.setTextColor(Color.RED);

                txtScore.setText(String.valueOf(player.getScore()));
                txtMiss.setText(String.valueOf(player.getMisses()) + " / " + MAX_MISSES);
                popTime--;
                if (popTime == 0) {
                    button[location].setBackgroundResource(R.drawable.mole_hole_new);
                    button[location2].setBackgroundResource(R.drawable.mole_hole_new);
                    showMole();
                }
            }

            public void onFinish() {
                gameOver();
            }

        }.start();
    }

    public void showMole() {
        Random rand = new Random();
        int bombTime = rand.nextInt(6);
        popTime = rand.nextInt(2 - 1) + 1;
        location = rand.nextInt(8);
        if(bombTime == BOMB_TIME)
            button[location].setBackgroundResource(R.drawable.bomb);
        else if(bombTime == TOGETHER_TIME) {
            button[location].setBackgroundResource(R.drawable.bomb);
            location2 = rand.nextInt(8);
            button[location2].setBackgroundResource(R.drawable.out_hole);
        }
        else
            button[location].setBackgroundResource(R.drawable.out_hole);
    }

    public boolean successfulHit(Button clickedButton) {
        return clickedButton.getBackground().getConstantState() == getResources().getDrawable(R.drawable.out_hole).getConstantState();
    }

    public boolean bombHit(Button clickedButton) {
        return clickedButton.getBackground().getConstantState() == getResources().getDrawable(R.drawable.bomb).getConstantState();
    }

    public void gameOver() {
        if(isNetworkAvailable()) {
            Map<String, Object> user = new HashMap<>();
            user.put("name", player.getName());
            user.put("score", player.getScore());
            if (checkPermission()) {
                user.put("lat", currentLocation.getLatitude());
                user.put("lon", currentLocation.getLongitude());
            }
            db.collection("HighScore")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            endGame();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PlayGameActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(PlayGameActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            endGame();
        }

    }

    public void endGame() {
        Intent intent = new Intent(getApplicationContext(), EndGameActivity.class);
        intent.putExtra("player", player);
        startActivity(intent);
        timer.cancel();
        finish();
    }

    public void playLastSecondsSound(long sec) {

        new Runnable() {
            @Override
            public void run() {
                mp_lastMin = MediaPlayer.create(getBaseContext(), (R.raw.tick));
                mp_lastMin.start();
            }
        }.run();

    }

    public void playHitSound() {

        new Runnable() {
            @Override
            public void run() {
                mp_hit = MediaPlayer.create(getBaseContext(), (R.raw.sword_hit));
                mp_hit.start();
            }
        }.run();
    }

    public void playMissSound() {

        new Runnable() {
            @Override
            public void run() {
                mp_miss = MediaPlayer.create(getBaseContext(), (R.raw.hodvent));
                mp_miss.start();
            }
        }.run();
    }

    public void playLoseSound() {

        new Runnable() {
            @Override
            public void run() {
                mp_miss = MediaPlayer.create(getBaseContext(), (R.raw.booz));
                mp_miss.start();
            }
        }.run();
    }

    public void playBombHitSound() {

        new Runnable() {
            @Override
            public void run() {
                mp_bomb_hit = MediaPlayer.create(getBaseContext(), (R.raw.bomb_hit));
                mp_bomb_hit.start();
            }
        }.run();
    }

    @Override
    public void onClick(View view) {
        if (view instanceof Button) {
            Button clickedButton = findViewById(view.getId());
            if (successfulHit(clickedButton)) {
                playHitSound();
                player.increaseScore();
                clickedButton.setBackgroundResource(R.drawable.mole_hole_new);
            } else if(bombHit(clickedButton)) {
                playBombHitSound();
                wrongAnimation();
                player.decreaseScore();
                clickedButton.setBackgroundResource(R.drawable.mole_hole_new);
            }
            else {
                playMissSound();
                player.increaseMisses();
                if (player.getMisses() == MAX_MISSES) {
                    playLoseSound();
                    gameOver();
                }
            }
        }
    }

    private void wrongAnimation() {
        final float FREQ = 3f;
        final float DECAY = 2f;
        TimeInterpolator decayingSineWave = new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                double raw = Math.sin(FREQ * input * 2 * Math.PI);
                return (float)(raw * Math.exp(-input * DECAY));
            }
        };

        button[location].animate()
                .yBy(-100)
                .xBy(-100)
                .setInterpolator(decayingSineWave)
                .setDuration(400)
                .start();
    }

    private void setLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    getApplicationContext().checkSelfPermission(COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        if (currentLocation == null) {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        float metersToUpdate = 1;
        long intervalMilliseconds = 1000;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, intervalMilliseconds, metersToUpdate, this);
    }

    public boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }


}