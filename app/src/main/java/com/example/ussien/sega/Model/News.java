package com.example.ussien.sega.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ussien on 20/09/2017.
 */

public class News {
    @SerializedName("image")
    String image;
    @SerializedName("content")
    String content;

    public News(String image, String content, int newsID) {
        this.image = image;
        this.content = content;
        this.newsID = newsID;
    }

    @SerializedName("newsID")
    int newsID;

    public int getNewsID() {
        return newsID;
    }

    public void setNewsID(int newsID) {
        this.newsID = newsID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
