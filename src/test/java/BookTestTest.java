import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author guada
 * @version 0.1.0
 * @since 10/1/2025
 **/
class BookTest {
    @Test
    void BookConstructorTest() {
        Book book = null;
        assertNull(book);

        book = new Book("", "", "", 0, "", null);
        assertNotNull(book);
    }

    @Test
    void FieldSettingGetterTest() {
        String isbn = "1337";
        String title = "Head First Java";
        String author = "Grady Booch";
        String subject = "Computer Science";
        int pageCount = 100;
        LocalDate dueDate = null;

        Book b = new Book(isbn, title, subject, pageCount, author, dueDate);

        assertEquals(isbn, b.getIsbn());
        assertEquals(title, b.getTitle());
        assertEquals(subject, b.getSubject());
        assertEquals(pageCount, b.getPageCount());
        assertEquals(author, b.getAuthor());
        assertNull(b.getDueDate());
    }

    @Test
    void SetterTest() {
        Book b = new Book("42-w-87", "Hitchhikers Guide To the Galaxy", "sci-fi", 42, "Douglas Adams", null);

        String newIsbn = "42-w-87";
        String newTitle = "Hitchhikers Guide To the Galaxy";
        String newSubject = "sci-fi";
        String newAuthor = "Douglas Adams";
        int newPageCount = 42;
        LocalDate newDueDate = LocalDate.of(2026, 02, 23);

        b.setIsbn(newIsbn);
        b.setTitle(newTitle);
        b.setSubject(newSubject);
        b.setPageCount(newPageCount);
        b.setAuthor(newAuthor);
        b.setDueDate(newDueDate);

        assertEquals(newIsbn, b.getIsbn());
        assertEquals(newTitle, b.getTitle());
        assertEquals(newSubject, b.getSubject());
        assertEquals(newPageCount, b.getPageCount());
        assertEquals(newAuthor, b.getAuthor());
        assertEquals(newDueDate, b.getDueDate());
    }

    @Test
    void EqualsTest() {
        Book a = new Book("42-w-87", "Hitchhikers Guide To the Galaxy", "sci-fi", 42, "Douglas Adams", null);
        Book b = new Book("34-w-34", "Dune", "sci-fi", 235, "Frank Herbert", null);
        assertNotEquals(a, b);

        Book c = new Book("42-w-87", "Hitchhikers Guide To the Galaxy", "sci-fi", 42, "Douglas Adams", null);
        assertEquals(a, c);
        assertEquals(a.hashCode(), c.hashCode());
    }
}