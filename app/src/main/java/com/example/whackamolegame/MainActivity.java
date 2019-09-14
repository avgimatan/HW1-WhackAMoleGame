package com.example.whackamolegame;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Serializable, Finals, View.OnTouchListener {

    private Player player = new Player();
    private EditText editTextName;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.main_layout).setOnTouchListener(this);
        editTextName = findViewById(R.id.txt_name);
        startButton = findViewById(R.id.btn_start);
        getGoogleMapsPermissions();
        startButton.setOnClickListener(this);

    }

    public boolean isInputEmpty(String name) {
        return name.equals("");
    }

    public void startGame() {
        Intent intent = new Intent(this, PlayGameActivity.class);
        intent.putExtra("player", player);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view instanceof Button) {
            Button clickedButton = (Button) view;
            if (clickedButton.getId() == R.id.btn_start) {
                player.setName(editTextName.getText().toString());
                if (!isInputEmpty(player.getName())) {
                    startGame();
                } else
                    Toast.makeText(this, "You must enter your name!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void getGoogleMapsPermissions() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return false;
    }
}