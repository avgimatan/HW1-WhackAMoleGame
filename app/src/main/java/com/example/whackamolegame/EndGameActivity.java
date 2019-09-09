package com.example.whackamolegame;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class EndGameActivity extends AppCompatActivity implements View.OnClickListener, Finals, OnMapReadyCallback {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TableLayout tableLayout;
    private Player player;
    private double lat, lon;

    private LinearLayout linearLayoutFragment;

    private GoogleMap googleMap;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        TextView announcementTextView, pointsTextView;
        Button playAgainButton;

        mapFragment = MapFragment.newInstance();
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.map_holder, mapFragment);
        transaction.commit();



        player = (Player) getIntent().getSerializableExtra("player");
        linearLayoutFragment = findViewById(R.id.linear_fragment);
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
                                if (gpsPermission(documentSnapshot)) {
                                    lon = documentSnapshot.getDouble("lon");
                                    lat = documentSnapshot.getDouble("lat");
                                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(GoogleMap googleMap) {
                                            setGoogleMap(googleMap,lat,lon);
                                        }
                                    });

                                    linearLayoutFragment.setVisibility(View.VISIBLE);
                                }
                                else
                                    Toast.makeText(getApplicationContext(), "No GPS Permission", Toast.LENGTH_SHORT).show();
                            }
                        });
                        tableLayout.addView(tableRow);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "An error has occurred", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    public boolean gpsPermission(DocumentSnapshot documentSnapshot) {
        return player.isLocationPermission() &&
                documentSnapshot.getDouble("lon") != null &&
                documentSnapshot.getDouble("lon") != null;
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

    public void setGoogleMap(GoogleMap googleMap, double lat, double lon) {
        this.googleMap = googleMap;
        //googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); // Unmark to see the changes...

        boolean isAllowedToUseLocation = isPermissionForLocationServicesGranted();
        if (isAllowedToUseLocation) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),18f));
            googleMap.addMarker(new MarkerOptions()
                .draggable(true)
                .position(new LatLng(lat,lon)));
        }
    }

    private boolean isPermissionForLocationServicesGranted() {
        return android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                (!(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED));

    }
}