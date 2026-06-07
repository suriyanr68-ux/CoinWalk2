package com.example.coinwalk2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;

public class MainActivity extends AppCompatActivity implements GameUpdateListener {

    private GameView gameView;
    // ... (ตัวแปรอื่นๆ ของคุณ) ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(gameView);
        // ... (โค้ดใน onCreate ของคุณ) ...
    }

    // ฟังก์ชันอื่นๆ ของคุณ (ถ้ามี) เช่น showStartMenu(), onScoreUpdated()
    // ...

    // ----------------------------------------------------
    // เก็บ onGameOver() และ showReviveDialog() ไว้แค่ชุดเดียวแบบนี้ครับ
    // ----------------------------------------------------

    @Override
    public void onGameOver() {
        runOnUiThread(() -> {
            showReviveDialog();
        });
    }@Override
    public void onScoreUpdated(int currentScore, int currentLevel) {
        // ให้รันบน UI Thread เพราะเป็นการอัปเดตหน้าจอ
        runOnUiThread(() -> {
            // TODO: นำตัวเลขไปแสดงบนหน้าจอ
            // ตัวอย่างเช่น ถ้าคุณมี TextView ชื่อ tvScore ให้เอาคอมเมนต์ (//) ด้านล่างออก
            // tvScore.setText("Score: " + currentScore);
            // tvLevel.setText("LV: " + currentLevel);
        });
    }

    private void showReviveDialog() {
        // 1. สุ่มตัวเลข 2 ตัวมาบวกกัน
        int num1 = (int)(Math.random() * 10) + 1;
        int num2 = (int)(Math.random() * 10) + 1;
        int correctAnswer = num1 + num2;
        int wrongAnswer = correctAnswer + (Math.random() > 0.5 ? 2 : -2);

        // 2. สุ่มว่าปุ่มไหนจะเป็นคำตอบที่ถูก (ซ้าย หรือ ขวา)
        boolean isCorrectLeft = Math.random() > 0.5;

        // 3. สร้างหน้าต่าง Popup (AlertDialog)
        new android.app.AlertDialog.Builder(this)
                .setTitle("โอกาสแก้ตัว!")
                .setMessage("ตอบคำถามเพื่อเล่นต่อ:  " + num1 + " + " + num2 + " = ?")
                .setCancelable(false)
                .setPositiveButton(isCorrectLeft ? String.valueOf(correctAnswer) : String.valueOf(wrongAnswer), (dialog, which) -> {
                    if (isCorrectLeft) {
                        gameView.revivePlayer();
                    } else {
                        gameView.resetGameData();
                        showStartMenu();
                    }
                })
                .setNegativeButton(!isCorrectLeft ? String.valueOf(correctAnswer) : String.valueOf(wrongAnswer), (dialog, which) -> {
                    if (!isCorrectLeft) {
                        gameView.revivePlayer();
                    } else {
                        gameView.resetGameData();
                        showStartMenu();
                    }
                })
                .show();
    }// ฟังก์ชันสำหรับจัดการหน้าเมนูหลัก (เมื่อผู้เล่นตอบคำถามผิด หรือเลือกไม่เล่นต่อ)
    private void showStartMenu() {
        runOnUiThread(() -> {
            // TODO: ใส่โค้ดสำหรับกลับหน้าเมนูหลัก หรือ รีสตาร์ทเกมของคุณตรงนี้

            // ----------------------------------------------------
            // 💡 ตัวอย่างที่ 1: ถ้าระบบเกมของคุณใช้วิธีแสดงหน้าจอเมนู (Layout) ขึ้นมาทับ
            // menuLayout.setVisibility(View.VISIBLE);
            // gameView.setVisibility(View.GONE);
            // ----------------------------------------------------

            // ----------------------------------------------------
            // 💡 ตัวอย่างที่ 2: ถ้าคุณยังไม่มีหน้าเมนู และอยากให้ "เริ่มเกมใหม่" ทันที ให้ใช้โค้ด 3 บรรทัดนี้ครับ:
            /*
            android.content.Intent intent = new android.content.Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            */
            // ----------------------------------------------------
        });
    }

} // ปีกกาปิดของคลาส MainActivity