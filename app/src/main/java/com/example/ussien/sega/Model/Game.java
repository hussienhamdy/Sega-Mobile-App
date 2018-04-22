package com.example.ussien.sega.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ussien on 18/09/2017.
 */

public class Game {
    @SerializedName("gameID")
    private int gameID;
    @SerializedName("gameName")
    private String gameName;
    @SerializedName("gameDescription")
    private String gameDescription;
    @SerializedName("gamePhoto")
    private String gamePhoto;
    @SerializedName("gameCode")
    private String gameCode;

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameDescription() {
        return gameDescription;
    }

    public void setGameDescription(String gameDescription) {
        this.gameDescription = gameDescription;
    }

    public String getGamePhoto() {
        return gamePhoto;
    }

    public void setGamePhoto(String gamePhoto) {
        this.gamePhoto = gamePhoto;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public Game(int gameID, String gameName, String gameDescription, String gamePhoto, String gameCode) {

        this.gameID = gameID;
        this.gameName = gameName;
        this.gameDescription = gameDescription;
        this.gamePhoto = gamePhoto;
        this.gameCode = gameCode;
    }
}
