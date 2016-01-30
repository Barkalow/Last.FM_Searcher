package edu.apsu.csci4020.finalproject;

/**
 * Created by ACOFF on 11/27/2015.
 *
 * Object to hold the returned data from the API calls
 */
public class Item {
    private String title;
    private String url;

    public Item() {
    }

    public Item(String name, String web) {
        title = name;
        url = web;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return title;
    }
}
