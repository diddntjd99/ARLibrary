package com.example.HSB;

import org.json.JSONObject;

import java.util.List;

public class BookList {
    private List<JSONObject> books;

    private static class BookListHolder {
        public static final BookList INSTANCE = new BookList();
    }

    public static BookList getBookListObject() {
        return BookListHolder.INSTANCE;
    }

    public JSONObject getBook(int index) {
        return books.get(index);
    }

    public void setBooks(List<JSONObject> books) {
        this.books = books;
    }
}
