package org.example;

import java.util.ArrayList;
import java.util.List;

public class LibraryModel {
    public List<Book> getAllBooks()
    {
        List<Book> allBooks = new ArrayList<>();
        return allBooks;
    }
    public void saveSearchKeyword(String keyword)
    {
    }

    public boolean checkAvailability(int id) {
        return false;
    }

    public void borrowBook(int id) {
    }
}
