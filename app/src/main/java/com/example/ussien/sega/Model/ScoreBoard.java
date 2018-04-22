package com.example.ussien.sega.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ussien on 29/09/2017.
 */

public class ScoreBoard {
    @SerializedName("studentPicture")
    String image;
    @SerializedName("studentName")
    String name;
    @SerializedName("progress")
    int progress;
    @SerializedName("points")
    int points ;

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    @SerializedName("studentID")
    int studentID;

    public ScoreBoard(String image, String name, int progress, int points) {
        this.image = image;
        this.name = name;
        this.progress = progress;
        this.points = points;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
