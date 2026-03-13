package org.example;

public class NotificationService {
    public void sendNotification(String username, String title) {
        System.out.println("Notification sent to " + username + ": Buku '" + title + "' berhasil dipinjam/dikembalikan.");
    }
}
