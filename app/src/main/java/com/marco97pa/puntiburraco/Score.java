package com.marco97pa.puntiburraco;

/**
 * Created by Marco F on 02/08/2017.
 */

public class Score {
    private String player1, player2, player3, date;
    private int point1, point2, point3;

    public Score() {
    }

    public Score(String player1, String player2, String player3, int point1, int point2, int point3, String date) {
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.point1 = point1;
        this.point2 = point2;
        this.point3 = point3;
        this.date = date;
    }

    public String getPlayer1() {
        return player1;
    }
    public String getPlayer2() {
        return player2;
    }
    public String getPlayer3() {
        return player3;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }
    public void setPlayer2(String player2) {
        this.player2 = player2;
    }
    public void setPlayer3(String player3) {
        this.player3 = player3;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPoint1() {
        return point1;
    }
    public int getPoint2() {
        return point2;
    }
    public int getPoint3() {
        return point3;
    }

    public void setPoint1(int point1) {
        this.point1 = point1;
    }
    public void setPoint2(int point2) {
        this.point2 = point2;
    }
    public void setPoint3(int point3) {
        this.point3 = point3;
    }

}
