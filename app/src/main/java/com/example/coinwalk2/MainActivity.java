package com.example.coinwalk2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showStartMenu();
    }

    private void showStartMenu() {
        setContentView(R.layout.activity_start_menu);

        View btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> startTheGame());
    }

    private void startTheGame() {
        setContentView(R.layout.activity_gameplay);

        GameView gameView = findViewById(R.id.gameView);
        TextView tvScore = findViewById(R.id.tvScore);
        TextView tvLevel = findViewById(R.id.tvLevel);
        View btnStop = findViewById(R.id.btnStop);

        gameView.setGameUpdateListener(new GameView.GameUpdateListener() {
            @Override
            public void onScoreUpdated(int currentScore, int currentLevel) {
                runOnUiThread(() -> {
                    tvScore.setText("SCORE: " + currentScore);
                    tvLevel.setText("LV. " + currentLevel);
                });
            }

            @Override
            public void onGameOver() {
                runOnUiThread(() -> showStartMenu());
            }
        });

        btnStop.setOnClickListener(v -> showStartMenu());
    }
}