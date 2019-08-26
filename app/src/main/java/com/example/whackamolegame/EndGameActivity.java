package com.example.whackamolegame;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EndGameActivity extends AppCompatActivity implements View.OnClickListener, Finals{

    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        TextView nameTextView, announcementTextView, pointsTextView;
        Button playAgainButton;

        player = (Player) getIntent().getSerializableExtra("player");

        nameTextView = findViewById(R.id.txt_name_final);
        announcementTextView = findViewById(R.id.txt_announcement);
        pointsTextView = findViewById(R.id.txt_points_result);

        playAgainButton = findViewById(R.id.btn_play_again);

        nameTextView.setText(player.getName());
        announcementTextView.setText(checkAnnouncement(player));
        pointsTextView.setText("Score: " + String.valueOf(player.getScore()));

        playAgainButton.setOnClickListener(this);
    }

    public String checkAnnouncement(Player p) {
        if (p.getMisses() >= MAX_MISSES)
            return "Has Lost";
        else if (p.getScore() >= WIN_SCORE)
            return "Has Won";
        else
            return "Has Lost";
    }

    public void playAgain() {
        player.resetPoints();
        Intent intent = new Intent(this, PlayGameActivity.class);
        intent.putExtra("player", player);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        Button clickedButton = (Button) v;
        if (clickedButton.getId() == R.id.btn_play_again) {
            playAgain();
        }
        else
            Log.e("3", "You must enter your name!");
    }
}