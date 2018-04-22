package com.example.ussien.sega.Model;

/**
 * Created by ussien on 19/09/2017.
 */

public class GameRegisterResult {
    String valid;
    String error;

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public GameRegisterResult(String valid, String error) {

        this.valid = valid;
        this.error = error;
    }
}
