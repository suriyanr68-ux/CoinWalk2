package com.example.coinwalk2;

public class Obstacle {
    public float x;
    public float dy;
    public float y;
    public float speedY;

    public Obstacle(float x, float dy, float y, float speedY) {
        this.x = x;
        this.dy = dy;
        this.y = y;
        this.speedY = speedY;
    }
}