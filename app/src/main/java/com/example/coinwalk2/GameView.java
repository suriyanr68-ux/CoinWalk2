package com.example.coinwalk2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameView extends View {

    public interface GameUpdateListener {
        void onScoreUpdated(int currentScore, int currentLevel);
        void onGameOver();
    }

    private GameUpdateListener updateListener;
    private float playerX = 0f;
    private float playerY = 0f;
    private float playerSpeed = 25f;
    private float coinX = 0f;
    private float coinY = 0f;
    private final float coinRadius = 40f;
    private int score = 0;
    private final List<Obstacle> obstacles = new ArrayList<>();

    private final Paint paint = new Paint();
    private boolean isInitialized = false;

    private int currentLane = 1;
    private float targetX = 0f;
    private float playerJumpY = 0f;
    private int jumpActionTime = 0;
    private boolean isDucking = false;
    private int duckActionTime = 0;
    private int gameTick = 0;

    private float startTouchX = 0f;
    private float startTouchY = 0f;
    private final float minSwipeDistance = 80f;
    private final Random random = new Random();
    private boolean isPaused = false;

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
    }

    public void setGameUpdateListener(GameUpdateListener listener) {
        this.updateListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isInitialized && getWidth() > 0 && getHeight() > 0) {
            initGameSetup();
            isInitialized = true;
        }

        drawGamePlay(canvas, isPaused);

        if (!isPaused) {
            invalidate();
        }
    }

    private void initGameSetup() {
        playerY = getHeight() - 650f;
        playerX = (getWidth() / 3f) * 1.5f;
        resetGameData();
    }

    public void resetGameData() {
        score = 0;
        currentLane = 1;
        // ... โค้ดอื่นๆ เหมือนเดิม ...
        gameTick = 0;
        obstacles.clear();

        coinY = -100f; // 👈 เปลี่ยนจาก 0f เป็น -100f
        isPaused = false;
    }

    public void drawGamePlay(Canvas canvas, boolean isPaused) {
        int currentLevel = (score / 20) + 1;

        float laneWidth = getWidth() / 3f;

        paint.setColor(Color.parseColor("#CBD5E1"));
        paint.setStrokeWidth(6f);
        for (int i = 1; i <= 2; i++) {
            float lx = i * laneWidth;
            canvas.drawLine(lx, 0f, lx, getPlayableBottom(), paint);
        }

        paint.setColor(Color.parseColor("#94A3B8"));
        canvas.drawLine(0, getPlayableBottom(), getWidth(), getPlayableBottom(), paint);

        if (!isPaused) {
            gameTick++;

            playerSpeed = 22f + (currentLevel * 2.0f);

            targetX = (currentLane * laneWidth) + (laneWidth / 2f);
            if (playerX < targetX) {
                playerX += playerSpeed;
                if (playerX > targetX) playerX = targetX;
            } else if (playerX > targetX) {
                playerX -= playerSpeed;
                if (playerX < targetX) playerX = targetX;
            }

            if (jumpActionTime > 0) {
                playerJumpY = -180f * (1f - (Math.abs(10 - jumpActionTime) / 10f));
                jumpActionTime--;
            } else {
                playerJumpY = 0f;
            }

            if (isDucking) {
                duckActionTime--;
                if (duckActionTime <= 0) isDucking = false;
            }

            int spawnRate = Math.max(14, 60 - (currentLevel * 6));
            if (gameTick % spawnRate == 0) {
                int obstacleLane = random.nextInt(3);
                float obsX = (obstacleLane * laneWidth) + (laneWidth / 2f);

                // 4️⃣ [🔥 ไวขึ้น] ปรับความเร็วการร่วงของอุปสรรคให้เร็วขึ้นแบบก้าวกระโดดตามเลเวล
                float obsSpeed = 10f + (currentLevel * 3.5f);

                float obsType = random.nextBoolean() ? 1f : 2f;
                obstacles.add(new Obstacle(obsX, obsSpeed, 0f, obsType));
            }

            if (coinY > getPlayableBottom() || coinY < 0f) {
                int coinLane = random.nextInt(3);
                coinX = (coinLane * laneWidth) + (laneWidth / 2f);
                coinY = 0f;
            } else {
                coinY += 9f + (currentLevel * 2.0f);
            }


            Iterator<Obstacle> iterator = obstacles.iterator();
            while (iterator.hasNext()) {
                Obstacle obs = iterator.next();
                obs.y += obs.dy;

                if (Math.abs(obs.x - playerX) < 60f && obs.y > playerY - 100f && obs.y < getPlayableBottom() - 30f) {
                    if (obs.speedY == 1f && playerJumpY < -60f) {
                        // กระโดดพ้น
                    } else if (obs.speedY == 2f && isDucking) {
                        // ก้มพ้น
                    } else {
                        resetGameData();
                        if (updateListener != null) {
                            updateListener.onGameOver();
                        }
                        return;
                    }
                }

                if (obs.y > getPlayableBottom() + 100f) {
                    iterator.remove();
                }
            }

            if (checkCircleCollision(playerX, playerY + playerJumpY, coinX, coinY)) {
                score += 5;
                int updatedLevel = (score / 20) + 1;
                if (updateListener != null) {
                    updateListener.onScoreUpdated(score, updatedLevel);
                }
                coinY = getPlayableBottom() + 100f;
            }
        }

        if (coinY <= getPlayableBottom()) {
            paint.setColor(Color.parseColor("#D97706"));
            canvas.drawCircle(coinX, coinY + 4f, coinRadius, paint);
            paint.setColor(Color.parseColor("#F59E0B"));
            canvas.drawCircle(coinX, coinY, coinRadius, paint);
            paint.setColor(Color.parseColor("#FEF08A"));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5f);
            canvas.drawCircle(coinX, coinY, coinRadius - 10f, paint);
            paint.setStyle(Paint.Style.FILL);
        }

        for (Obstacle obs : obstacles) {
            if (obs.speedY == 1f) {
                paint.setColor(Color.parseColor("#B91C1C"));
                canvas.drawRoundRect(obs.x - 65f, obs.y - 15f, obs.x + 65f, obs.y + 25f, 8f, 8f, paint);
                paint.setColor(Color.parseColor("#EF4444"));
                canvas.drawRoundRect(obs.x - 55f, obs.y - 25f, obs.x + 55f, obs.y + 15f, 8f, 8f, paint);
            } else {
                paint.setColor(Color.parseColor("#475569"));
                canvas.drawRect(obs.x - 65f, obs.y - 60f, obs.x - 50f, obs.y + 25f, paint);
                canvas.drawRect(obs.x + 50f, obs.y - 60f, obs.x + 65f, obs.y + 25f, paint);
                paint.setColor(Color.parseColor("#F97316"));
                canvas.drawRoundRect(obs.x - 55f, obs.y - 50f, obs.x + 55f, obs.y - 20f, 6f, 6f, paint);
                paint.setColor(Color.WHITE);
                canvas.drawRect(obs.x - 50f, obs.y - 40f, obs.x + 50f, obs.y - 30f, paint);
            }
        }

        float drawY = playerY + playerJumpY;
        if (isDucking) {
            paint.setColor(Color.parseColor("#1D4ED8"));
            canvas.drawCircle(playerX, drawY + 30f, 45f, paint);
            paint.setColor(Color.parseColor("#60A5FA"));
            canvas.drawCircle(playerX, drawY + 30f, 25f, paint);
        } else {
            paint.setColor(Color.parseColor("#2563EB"));
            canvas.drawCircle(playerX, drawY + 4f, 55f, paint);
            paint.setColor(Color.parseColor("#3B82F6"));
            canvas.drawCircle(playerX, drawY, 55f, paint);
            paint.setColor(Color.parseColor("#93C5FD"));
            canvas.drawCircle(playerX, drawY, 25f, paint);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(playerX - 12f, drawY - 12f, 10f, paint);
        }

        if (isPaused) {
            paint.setColor(Color.argb(180, 15, 23, 42));
            canvas.drawRect(0f, 0f, getWidth(), getHeight(), paint);
            paint.setColor(Color.parseColor("#F59E0B"));
            paint.setTextSize(75f);
            paint.setFakeBoldText(true);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("GAME PAUSED", getWidth() / 2f, getHeight() / 2f - 30f, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(35f);
            paint.setFakeBoldText(false);
            canvas.drawText("Tap anywhere to Resume", getWidth() / 2f, getHeight() / 2f + 40f, paint);
        }
    }

    public float getPlayableBottom() {
        return getHeight() - 400f;
    }

    public boolean checkCircleCollision(float x1, float y1, float cx, float cy) {
        double distance = Math.sqrt((x1 - cx) * (x1 - cx) + (y1 - cy) * (y1 - cy));
        return distance < (55f + coinRadius);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        float touchX = event.getX();
        float touchY = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startTouchX = touchX;
                startTouchY = touchY;
                if (isPaused) {
                    isPaused = false;
                    invalidate();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!isPaused) {
                    float diffX = touchX - startTouchX;
                    float diffY = touchY - startTouchY;

                    if (Math.abs(diffX) > minSwipeDistance || Math.abs(diffY) > minSwipeDistance) {
                        if (Math.abs(diffX) > Math.abs(diffY)) {
                            if (diffX > 0) {
                                if (currentLane < 2) currentLane++;
                            } else {
                                if (currentLane > 0) currentLane--;
                            }
                        } else {
                            if (diffY > 0) {
                                if (jumpActionTime <= 0 && !isDucking) {
                                    isDucking = true;
                                    duckActionTime = 12;
                                }
                            } else {
                                if (jumpActionTime <= 0 && !isDucking) {
                                    jumpActionTime = 30;
                                }
                            }
                        }
                    } else {
                        isPaused = true;
                        invalidate();
                    }
                }
                performClick();
                break;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}