package com.example.whackamolegame;

import java.io.Serializable;

class Player implements Serializable {

    private String name;
    private int score;
    private int misses;

    public Player() {
        this.name = "";
        this.score = 0;
        this.misses = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setMisses(int misses) {
        this.misses = misses;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return this.score;
    }

    public int getMisses() {
        return misses;
    }

    public void increaseScore() {
        this.score += 3;
    }

    public void decreaseScore() {
        if(this.score >= 3)
            this.score -= 3;
        else
            this.score = 0;
    }

    public void increaseMisses() {
        this.misses += 1;
    }

    public void resetPoints() {
        setScore(0);
        setMisses(0);
    }
}