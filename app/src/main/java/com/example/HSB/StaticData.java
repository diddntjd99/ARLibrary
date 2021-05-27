package com.example.HSB;

import org.json.JSONObject;

import java.util.List;

public class StaticData {
    private List<JSONObject> books;
    private String user_id;
    private String user_name;
    private int position;

    private static class StaticDataHolder {
        public static final StaticData INSTANCE = new StaticData();
    }
    public static StaticData getStaticDataObject() {
        return StaticDataHolder.INSTANCE;
    }

    public JSONObject getBook(int index) {
        return books.get(index);
    }
    public void setBooks(List<JSONObject> books) {
        this.books = books;
    }

    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
}
