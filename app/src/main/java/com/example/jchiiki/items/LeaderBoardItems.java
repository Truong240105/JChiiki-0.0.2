package com.example.jchiiki.items;

public class LeaderBoardItems {
    String userName;
    Integer score, rank;
    public LeaderBoardItems(String userName, Integer score, Integer rank) {
        this.userName = userName;
        this.score = score;
        this.rank = rank;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
