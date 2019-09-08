package com.example.whackamolegame;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class EndGameActivity extends AppCompatActivity implements View.OnClickListener, Finals {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TableLayout tableLayout;
    private Player player;
    private Location userLocation;
    private double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        TextView nameTextView, announcementTextView, pointsTextView;
        Button playAgainButton;

        player = (Player) getIntent().getSerializableExtra("player");

        announcementTextView = findViewById(R.id.txt_announcement);
        pointsTextView = findViewById(R.id.txt_points_result);
        tableLayout = findViewById(R.id.highScoreTable);
        playAgainButton = findViewById(R.id.btn_play_again);

        if(isLose(player))
            announcementTextView.setText("Game Over!");
        else
            announcementTextView.setText("You Lose!");
        pointsTextView.setText("Your Score: " + String.valueOf(player.getScore()));

        fillTable();

        playAgainButton.setOnClickListener(this);
    }

    public boolean isLose(Player p) {
        if (p.getMisses() >= MAX_MISSES)
            return false;
        return true;
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
        } else
            Log.e("3", "You must enter your name!");
    }

    private void fillTable() {

        CollectionReference collectionReference = db.collection("HighScore");
        Query query;
        query = collectionReference.orderBy("score", Query.Direction.DESCENDING).limit(10);
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        TableRow tableRow = new TableRow(getApplicationContext());

                        TextView name = new TextView(getApplicationContext());
                        name.setPadding(25, 10, 10, 0);
                        name.setTextSize(20);
                        name.setTextColor(Color.WHITE);
                        name.setText(documentSnapshot.get("name").toString());
                        TextView score = new TextView(getApplicationContext());
                        score.setText(documentSnapshot.get("score").toString());
                        score.setPadding(25, 10, 10, 0);
                        score.setTextSize(20);
                        score.setTextColor(Color.WHITE);

                        tableRow.addView(name);
                        tableRow.addView(score);
                        tableRow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                lon = documentSnapshot.getDouble("lon");
                                lat = documentSnapshot.getDouble("lat");
                                Toast.makeText(getApplicationContext(), documentSnapshot.get("name").toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        tableLayout.addView(tableRow);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "An error has occurred", Toast.LENGTH_SHORT).show();
                });
    }



    public static class DebugExampleTwoFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            EditText v = new EditText(getActivity());
            v.setText("Hello Fragment!");
            return v;
        }
    }
}