package com.example.coinwalk2;

public interface GameUpdateListener {
    // ฟังก์ชันสำหรับอัปเดตคะแนนและเลเวลไปแสดงบนหน้าจอ
    void onScoreUpdated(int currentScore, int currentLevel);

    // ฟังก์ชันสำหรับแจ้งเตือนเมื่อตัวละครชนสิ่งกีดขวาง
    void onGameOver();
}