
import org.example.Book;
import org.example.LibraryControl;
import org.example.LibraryModel;
import org.example.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class LibraryTest {

    @Mock
    private LibraryModel bookModel;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LibraryControl control;

    @Test
    void TestSearchBookAuthor() {

        List<Book> books = new ArrayList<>();
        books.add(new Book(1,"Tutorial Figma", "Budi"));
        books.add(new Book(2,"Pengujian PL itu mudah", "putri"));
        //stub
        Mockito.when(bookModel.getAllBooks()).thenReturn(books);
        Assertions.assertEquals( "Budi", control.searchBookAuthor("Tutorial Figma"));
        //mock
        Mockito.verify(bookModel, Mockito.times(1)).getAllBooks();
    }
    @Test
    void testSearchBookAuthor_withNever() {

        List<Book> books = new ArrayList<>();
        books.add(new Book(1,"Tutorial Figma", "Budi"));
        Mockito.when(bookModel.getAllBooks()).thenReturn(books);
        control.searchBookAuthor("Unknown Book");

        Mockito.verify(bookModel, Mockito.never()).saveSearchKeyword(Mockito.anyString());

    }

    // ==================== 1. Test Cek Ketersediaan Buku ====================

    @Test
    void testCheckAvailability_bookAvailable() {
        // Stub: model.checkAvailability(1) mengembalikan true (buku tersedia)
        Mockito.when(bookModel.checkAvailability(1)).thenReturn(true);

        boolean result = control.checkAvailability(1);

        // Assert bahwa buku tersedia
        Assertions.assertTrue(result);
        // Mock: verifikasi bahwa checkAvailability dipanggil 1 kali dengan id 1
        Mockito.verify(bookModel, Mockito.times(1)).checkAvailability(1);
    }

    @Test
    void testCheckAvailability_bookNotAvailable() {
        // Stub: model.checkAvailability(99) mengembalikan false (buku tidak tersedia)
        Mockito.when(bookModel.checkAvailability(99)).thenReturn(false);

        boolean result = control.checkAvailability(99);

        // Assert bahwa buku tidak tersedia
        Assertions.assertFalse(result);
        // Mock: verifikasi bahwa checkAvailability dipanggil 1 kali dengan id 99
        Mockito.verify(bookModel, Mockito.times(1)).checkAvailability(99);
    }

    // ==================== 2. Test Meminjam Buku ====================

    @Test
    void testBorrowBook_bookNotBorrowed_shouldCallBorrowBook() {
        // Stub: buku tersedia (tidak sedang dipinjam)
        Mockito.when(bookModel.checkAvailability(1)).thenReturn(true);

        boolean result = control.borrowBook(1);

        // Assert peminjaman berhasil
        Assertions.assertTrue(result);
        // Mock: verifikasi bahwa fungsi borrowBook DIPANGGIL karena buku tersedia
        Mockito.verify(bookModel, Mockito.times(1)).borrowBook(1);
    }

    @Test
    void testBorrowBook_bookAlreadyBorrowed_shouldNotCallBorrowBook() {
        // Stub: buku tidak tersedia (sedang dipinjam)
        Mockito.when(bookModel.checkAvailability(1)).thenReturn(false);

        boolean result = control.borrowBook(1);

        // Assert peminjaman gagal
        Assertions.assertFalse(result);
        // Mock: verifikasi bahwa fungsi borrowBook TIDAK DIPANGGIL karena buku sedang dipinjam
        Mockito.verify(bookModel, Mockito.never()).borrowBook(Mockito.anyInt());
    }

    // ==================== 3. Test InOrder: getAllBooks -> borrowBook -> sendNotification ====================

    @Test
    void testBorrowBookWithNotification_inOrder() {
        // Arrange: siapkan data buku
        List<Book> books = new ArrayList<>();
        Book book = new Book(1, "Tutorial Figma", "Budi");
        book.setBorrowed(false); // buku belum dipinjam
        books.add(book);

        // Stub: getAllBooks mengembalikan list buku
        Mockito.when(bookModel.getAllBooks()).thenReturn(books);

        // Act: panggil borrowBookWithNotification
        boolean result = control.borrowBookWithNotification("Tutorial Figma", "Andi");

        // Assert peminjaman berhasil
        Assertions.assertTrue(result);

        // Verifikasi urutan pemanggilan menggunakan InOrder
        InOrder inOrder = Mockito.inOrder(bookModel, notificationService);
        // 1. getAllBooks() dipanggil terlebih dahulu
        inOrder.verify(bookModel).getAllBooks();
        // 2. borrowBook(id) dipanggil setelah getAllBooks
        inOrder.verify(bookModel).borrowBook(1);
        // 3. sendNotification(username, title) dipanggil terakhir
        inOrder.verify(notificationService).sendNotification("Andi", "Tutorial Figma");
    }

    @Test
    void testBorrowBookWithNotification_bookAlreadyBorrowed_inOrder() {
        List<Book> books = new ArrayList<>();
        Book book = new Book(1, "Tutorial Figma", "Budi");
        book.setBorrowed(true);
        books.add(book);

        // Stub
        Mockito.when(bookModel.getAllBooks()).thenReturn(books);

        // Act
        boolean result = control.borrowBookWithNotification("Tutorial Figma", "Andi");

        // Assert peminjaman gagal
        Assertions.assertFalse(result);

        // Verifikasi: getAllBooks dipanggil, tapi borrowBook dan sendNotification TIDAK dipanggil
        Mockito.verify(bookModel, Mockito.times(1)).getAllBooks();
        Mockito.verify(bookModel, Mockito.never()).borrowBook(Mockito.anyInt());
        Mockito.verify(notificationService, Mockito.never()).sendNotification(Mockito.anyString(), Mockito.anyString());
    }
}