package org.example;

import java.util.List;

public class LibraryControl {
    private final LibraryModel model;
    private NotificationService notificationService;

    public LibraryControl(LibraryModel model) {
        this.model = model;
    }

    public LibraryControl(LibraryModel model, NotificationService notificationService) {
        this.model = model;
        this.notificationService = notificationService;
    }

    public String searchBookAuthor(String title)
    {
        List<Book> books = model.getAllBooks();
        String result = "";
        for (Book book : books) {
            if (book.getTitle().equals(title)) {
                model.saveSearchKeyword(title);
                result = book.getAuthor();
            }
            //else result = "Book Not Found";
        }
        return result;
    }

    public boolean checkAvailability(int id) {
        return model.checkAvailability(id);
    }

    public boolean borrowBook(int id) {
        boolean available = model.checkAvailability(id);
        if (available) {
            model.borrowBook(id);
            return true;
        }
        return false;
    }

    public boolean borrowBookWithNotification(String title, String username) {
        List<Book> books = model.getAllBooks();
        for (Book book : books) {
            if (book.getTitle().equals(title) && !book.isBorrowed()) {
                model.borrowBook(book.getId());
                notificationService.sendNotification(username, title);
                return true;
            }
        }
        return false;
    }
}
