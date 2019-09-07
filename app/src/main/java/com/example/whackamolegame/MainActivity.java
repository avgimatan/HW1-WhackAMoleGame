package com.example.whackamolegame;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Serializable {

    private Player player = new Player();
    private EditText editTextName;
    private Button startButton;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.txt_name);
        startButton = findViewById(R.id.btn_start);
        startButton.setOnClickListener(this);
        getGoogleMapsPermissions();
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

    public void getGoogleMapsPermissions() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

}