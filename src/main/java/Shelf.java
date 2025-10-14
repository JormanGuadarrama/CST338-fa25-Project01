import java.util.HashMap;
import java.util.Objects;


/**
 * @author guada
 * @version 0.1.0
 * @since 10/9/2025
 **/
public class Shelf {
    public static final int SHELF_NUMBER_ = 0;
    public static final int SUBJECT_ = 1;

    private HashMap<Book, Integer> books;
    private int shelfNumber;
    private String subject;

    @Deprecated
    public Shelf() {}

    public Shelf(int shelfNumber, String subject) {
        this.shelfNumber = shelfNumber;
        this.books = new HashMap<>();
    }

    public int getBookCount(Book book) {
        if (book == null) return -1;

        Integer count = getBooks().get(book);

        return (count == null) ? -1 : count;
    }

    public Code addBook(Book book) {
        if (book == null) return Code.BOOK_RECORD_COUNT_ERROR;

        if(getBooks().containsKey(book)) {
            getBooks().merge(book, 1, Integer::sum);
            System.out.println(book.toString() + " added to shelf " + this.toString());
            return Code.SUCCESS;
        }

        try {
            String bookSubject = book.getSubject();
            if (bookSubject == null || !bookSubject.trim().equalsIgnoreCase(getSubject())) {
                return Code.SHELF_SUBJECT_MISMATCH_ERROR;
            }
            } catch (Exception ingored) {
                return Code.SHELF_SUBJECT_MISMATCH_ERROR;
            }

        getBooks().put(book, 1);
        System.out.println(book.toString() + " added to shelf " + this.toString());
        return Code.SUCCESS;
    }

    public Code removeBook(Book book) {
        if (book == null) return Code.BOOK_NOT_IN_INVENTORY_ERROR;

        if (!getBooks().containsKey(book)) {
            System.out.println(book.getTitle() + " is not on shelf " + getSubject());
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }

        int count = getBooks().get(book);
        if (count <= 0) {
            System.out.println("No copies of " + book.getTitle() + " remain on shelf " + getSubject());
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }

        int newCount = count - 1;
        getBooks().put(book, newCount);
        System.out.println(book.getTitle() + " successfully removed from shelf " + getSubject());
        return Code.SUCCESS;

    }

    public String listBooks() {
        int total = 0;
        for (Integer c : getBooks().values()) {
            if(c != null && c > 0) total += c;
        }
        return total + " " + (total == 1 ? "book" : "books") + " on shelf: " + this.toString();
    }

    public HashMap<Book, Integer> getBooks() {
        if (books == null) {
            books = new HashMap<>();
        }
        return books;
    }

    public void setBooks(HashMap<Book, Integer> books) {
        this.books = books;
    }

    public int getShelfNumber() {
        return shelfNumber;
    }

    public void setShelfNumber(int shelfNumber) {
        this.shelfNumber = shelfNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Shelf shelf)) return false;
        return shelfNumber == shelf.shelfNumber && Objects.equals(subject, shelf.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shelfNumber, subject);
    }

    @Override
    public String toString() {
        return shelfNumber + " : " + subject;
    }
}
