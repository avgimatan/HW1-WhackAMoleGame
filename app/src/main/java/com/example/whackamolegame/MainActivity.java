package com.example.whackamolegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Serializable {

    Player player = new Player();
    LinearLayout mainLayout;
    EditText editTextName;
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.main_layout);
        editTextName = findViewById(R.id.txt_name);
        startButton = findViewById(R.id.btn_start);
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
}