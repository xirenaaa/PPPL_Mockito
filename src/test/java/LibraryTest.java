
import org.example.Book;
import org.example.LibraryControl;
import org.example.LibraryModel;
import org.example.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;


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

    // Cek stok buku

    @Test
    void testCheckAvailability_bookAvailable() {
        Mockito.when(bookModel.checkAvailability(1)).thenReturn(true);

        boolean result = control.checkAvailability(1);

        Assertions.assertTrue(result);
        Mockito.verify(bookModel, Mockito.times(1)).checkAvailability(1);
    }

    @Test
    void testCheckAvailability_bookNotAvailable() {
        Mockito.when(bookModel.checkAvailability(99)).thenReturn(false);

        boolean result = control.checkAvailability(99);

        Assertions.assertFalse(result);
        Mockito.verify(bookModel, Mockito.times(1)).checkAvailability(99);
    }

    // Pinjam buku

    @Test
    void testBorrowBook_bookNotBorrowed_shouldCallBorrowBook() {
        Mockito.when(bookModel.checkAvailability(1)).thenReturn(true);

        boolean result = control.borrowBook(1);

        Assertions.assertTrue(result);
        Mockito.verify(bookModel, Mockito.times(1)).borrowBook(1);
    }

    @Test
    void testBorrowBook_bookAlreadyBorrowed_shouldNotCallBorrowBook() {
        Mockito.when(bookModel.checkAvailability(1)).thenReturn(false);

        boolean result = control.borrowBook(1);

        Assertions.assertFalse(result);
        Mockito.verify(bookModel, Mockito.never()).borrowBook(Mockito.anyInt());
    }


    @Test
    void testBorrowBookWithNotification_inOrder() {
        List<Book> books = new ArrayList<>();
        Book book = new Book(1, "Tutorial Figma", "Budi");
        book.setBorrowed(false);
        books.add(book);

        Mockito.when(bookModel.getAllBooks()).thenReturn(books);

        boolean result = control.borrowBookWithNotification("Tutorial Figma", "Andi");

        Assertions.assertTrue(result);

        InOrder inOrder = Mockito.inOrder(bookModel, notificationService);
        inOrder.verify(bookModel).getAllBooks();
        inOrder.verify(bookModel).borrowBook(1);
        inOrder.verify(notificationService).sendNotification("Andi", "Tutorial Figma");
    }

    @Test
    void testBorrowBookWithNotification_bookAlreadyBorrowed_inOrder() {
        List<Book> books = new ArrayList<>();
        Book book = new Book(1, "Tutorial Figma", "Budi");
        book.setBorrowed(true);
        books.add(book);

        Mockito.when(bookModel.getAllBooks()).thenReturn(books);

        boolean result = control.borrowBookWithNotification("Tutorial Figma", "Andi");

        Assertions.assertFalse(result);

        Mockito.verify(bookModel, Mockito.times(1)).getAllBooks();
        Mockito.verify(bookModel, Mockito.never()).borrowBook(Mockito.anyInt());
        Mockito.verify(notificationService, Mockito.never()).sendNotification(Mockito.anyString(), Mockito.anyString());
    }
}