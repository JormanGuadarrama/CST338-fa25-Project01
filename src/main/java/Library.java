import java.io.File;
import java.util.*;

/**
 * PROJECT 01 Part 04/04
 * @author guada
 * @version 0.1.0
 * @since 10/20/2025
 **/
public class Library {

    public static final int LENDING_LIMIT = 5;

    private String name;
    private static int libraryCard = 0;
    private final List<Reader> readers = new ArrayList<>();
    private final Map<String, Shelf> shelves = new HashMap<>();
    private final Map<Book, Integer> books = new HashMap<>();

    public Library(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Code checkOutBook(Reader reader, Book book) {
        if (reader == null || !readers.contains(reader)) {
            System.out.println(((reader == null) ? "null" : reader.getName()) + " doesn't have an account here");
            return Code.READER_NOT_IN_LIBRARY_ERROR;
        }
        int held = (reader.getBooks() == null) ? 0 : reader.getBooks().size();
        if (held >= LENDING_LIMIT) {
            System.out.println(reader.getName() + " has reached the lending limit, (" + LENDING_LIMIT + ")");
            return Code.BOOK_LIMIT_REACHED_ERROR;
        }
        Integer inInventory = books.get(book);
        if (inInventory == null) {
            System.out.println("ERROR: could not find " + book);
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }
        Shelf shelf = shelves.get(book.getSubject());
        if (shelf == null) {
            System.out.println("no shelf for " + book.getSubject() + " books!");
            return Code.SHELF_EXISTS_ERROR;
        }
        int onShelf = shelf.getBookCount(book);
        if (onShelf < 1) {
            System.out.println("ERROR: no copies of " + book + " remain");
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }
        Code addCode = reader.addBook(book);
        if (addCode != Code.SUCCESS) {
            System.out.println("Couldn't checkout " + book);
            return addCode;
        }
        Code removeCode = shelf.removeBook(book);
        if (removeCode == Code.SUCCESS) {
            System.out.println(book + " checked out successfully");
        }
        return removeCode;
    }


    public Code returnBook(Reader reader, Book book) {
        if (reader.getBooks() == null || !reader.getBooks().contains(book)) {
            System.out.println(reader.getName() + " doesn't have " + book.getTitle() + " checked out");
            return Code.READER_DOESNT_HAVE_BOOK_ERROR;
        }
        if (!books.containsKey(book)) {
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }
        System.out.println(reader.getName() + " is returning " + book);
        Code removed = reader.removeBook(book);
        if (removed == Code.SUCCESS) {
            return returnBook(book);
        }
        System.out.println("Could not return " + book);
        return removed;
    }


    public Code returnBook(Book book) {
       Shelf shelf = shelves.get(book.getSubject());
       if (shelf == null) {
           System.out.println("No shelf for " + book);
           return Code.SHELF_EXISTS_ERROR;
       }

       return shelf.addBook(book);
    }

    public Code addBook(Book newBook) {
        Integer count = books.get(newBook);
        if (count != null) {
            int updated = count + 1;
            books.put(newBook, updated);
            System.out.println(updated + " copies of " + newBook.getTitle() + " in the stacks");
        } else {
            books.put(newBook, 1);
            System.out.println(newBook.getTitle() + " added to the stacks.");
        }
        Shelf shelf = shelves.get(newBook.getSubject());
        if (shelf !=  null) {
            shelf.addBook(newBook);
            return Code.SUCCESS;
        }
        System.out.println("No shelf for "  + newBook.getSubject() + " books");
        return Code.SHELF_EXISTS_ERROR;
    }


    public Code addReader(Reader reader) {
        if (readers.contains(reader)) {
            System.out.println(reader.getName() + " already has an account here!");
            return Code.READER_ALREADY_EXISTS_ERROR;
        }

        for (Reader r : readers) {
            if (r.getCardNumber() == reader.getCardNumber()) {
                System.out.println(r.getName() + " and " + reader.getName() + " have the same card number!");
                return Code.READER_CARD_NUMBER_ERROR;
            }
        }

        readers.add(reader);
        System.out.println(reader.getName() + " added to the library!");
        if (reader.getCardNumber() > libraryCard) {
            libraryCard = reader.getCardNumber();
        }

        return Code.SUCCESS;
    }

    public Code removeReader(Reader reader) {
        if (!readers.contains(reader)) {
            System.out.println(reader + " is not part of this Library");
            return Code.READER_NOT_IN_LIBRARY_ERROR;
        }

        if (reader.getBooks() != null && !reader.getBooks().isEmpty()) {
            System.out.println(reader.getName() + " must return all books!");
            return Code.READER_STILL_HAS_BOOKS_ERROR;
        }

        readers.remove(reader);
        return Code.SUCCESS;
    }

    public Code addShelf(String subject, int shelfNumber) {
        if (subject == null || subject.isBlank()) {
            System.out.println("ERROR: subject required");
            return Code.SHELF_EXISTS_ERROR;
        }

        if (shelves.containsKey(subject)) {
            System.out.println(subject + " shelf already exists!");
            return Code.SHELF_EXISTS_ERROR;
        }

        for (Shelf s : shelves.values()) {
            if (s.getShelfNumber() == shelfNumber) {
                System.out.println("Shelf " + shelfNumber + " already used!");
                return Code.SHELF_EXISTS_ERROR;
            }
        }

        Shelf shelf = new Shelf(shelfNumber, subject);
        shelves.put(subject, shelf);
        System.out.println("Shelf " + shelfNumber + " added for " + subject+ ".");
        return Code.SUCCESS;
    }

    public Book getBookByISBN(String isbn) {
        for (Book b : books.keySet()) {
            if (b.getIsbn().equals(isbn)) {
                return b;
            }
        }

        System.out.println("ERROR: Could not find a book with isbn: " + isbn);
        return null;
    }

    public static int convertInt(String recordCountString, Code code) {
        try {
            return Integer.parseInt(recordCountString.trim());
        } catch (Exception e) {
            System.out.println("Value which caused the error: " + recordCountString);
            System.out.println("Error message: " + code.getMessage());
            switch (code) {
                case BOOK_COUNT_ERROR:
                    System.out.println("Error: Could not read number of books");
                    break;
                case PAGE_COUNT_ERROR:
                    System.out.println("Error: could not parse page count");
                    break;
                case DATE_CONVERSION_ERROR:
                    System.out.println("Error: Could not parse date component");
                    break;
                default:
                    System.out.println("Error: Unknown conversion error");
            }
            return code.getCode();
        }
    }

    public Code init(String filename) {
        try (java.util.Scanner scan = new java.util.Scanner(new java.io.File(filename))) {
            String bookCountStr = scan.nextLine();
            int bookCount = convertInt(bookCountStr, Code.BOOK_COUNT_ERROR);
            if (bookCount < 0) {
                return errorCode(bookCount);
            }
            Code b = initBooks(bookCount, scan);
            if (b != Code.SUCCESS) return b;
            listBooks();

            String shelfCountStr = scan.nextLine();
            int shelfCount = convertInt(shelfCountStr, Code.SHELF_COUNT_ERROR);
            if (shelfCount < 0) {
                return errorCode(shelfCount);
            }
            Code s = initShelves(shelfCount, scan);
            if (s != Code.SUCCESS) return s;
            listShelves();

            String readerCountStr = scan.nextLine();
            int readerCount = convertInt(readerCountStr, Code.READER_COUNT_ERROR);
            if (readerCount < 0) {
                return errorCode(readerCount);
            }
            Code r = initReader(readerCount, scan);
            if (r != Code.SUCCESS) return r;
            listReaders();

            return Code.SUCCESS;
        } catch (java.io.FileNotFoundException e) {
            return Code.FILE_NOT_FOUND_ERROR;
        }
    }

    private Code initBooks(int bookCount, java.util.Scanner scan) {
        if (bookCount < 1) return Code.LIBRARY_ERROR;
        for (int i = 0; i < bookCount; i++) {
            if (!scan.hasNextLine()) return Code.BOOK_RECORD_COUNT_ERROR;
            String line = scan.nextLine();
            String[] parts = line.split(",", -1);
            if (parts.length <= Book.DUE_DATE_) return Code.BOOK_RECORD_COUNT_ERROR;
            String isbn = parts[Book.ISBN_];
            String title = parts[Book.TITLE_];
            String subject = parts[Book.SUBJECT_];
            String pagesS = parts[Book.PAGE_COUNT_];
            String author = parts[Book.AUTHOR_];
            String dueS = parts[Book.DUE_DATE_];
            int pages = convertInt(pagesS, Code.PAGE_COUNT_ERROR);
            if (pages <= 0) return Code.PAGE_COUNT_ERROR;
            java.time.LocalDate due = convertDate(dueS, Code.DATE_CONVERSION_ERROR);
            if (due == null) return Code.DATE_CONVERSION_ERROR;
            Book b = new Book(isbn, title, subject, pages, author, due);
            Code added = addBook(b);
            if (added != Code.SUCCESS) return added;
        }
        return Code.SUCCESS;
    }

    public int listBooks() {
        int total = 0;
        for (java.util.Map.Entry<Book, Integer> e : books.entrySet()) {
            Book b = e.getKey();
            int count = e.getValue();
            System.out.println(count + " copies of " + b.getTitle() + " by " + b.getAuthor() + " ISBN:" + b.getIsbn());
            total += count;
        }
        return total;
    }

    public int listShelves() {
        return listShelves(false);
    }

    public int listShelves(boolean showbooks) {
        if (showbooks) {
            for (Shelf s : shelves.values()) {
                s.listBooks();
            }
        } else {
            for (Shelf s : shelves.values()) {
                System.out.println(s.toString());
            }
        }
        return shelves.size();
    }

    public int listReaders() {
        for (Reader r : readers) {
            System.out.println(r.toString());
        }
        return readers.size();
    }

    public int listReaders(boolean showBooks) {
        if (!showBooks) {
            return listReaders();
        }
        for (Reader r : readers) {
            System.out.println(r.getName() + "(#" + r.getCardNumber() + ")  has the following books: ");
            java.util.List<Book> held = r.getBooks();
            if (held == null || held.isEmpty()) {
                System.out.println("[]");
                continue;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < held.size(); i++) {
                Book b = held.get(i);
                sb.append(b.getTitle()).append(" by ").append(b.getAuthor()).append(" ISBN:").append(b.getIsbn());
                if (i < held.size() - 1) sb.append(", ");
            }
            sb.append("]");
            System.out.println(sb.toString());
        }
        return readers.size();
    }

    public Reader getReaderByCard(int cardNumber) {
        for (Reader r : readers) {
            if (r.getCardNumber() == cardNumber) {
                return r;
            }
        }
        System.out.println("Could not find a reader with card #" + cardNumber);
        return null;
    }

    private Code errorCode(int codeNumber) {
        for (Code c : Code.values()) {
            if (c.getCode() == codeNumber) return c;
        }
        return Code.UNKNOWN_ERROR;
    }


    private Code initShelves(int shelfCount, java.util.Scanner scan) {
        if (shelfCount < 1) {
            return Code.SHELF_COUNT_ERROR;
        }
        int start = shelves.size();
        for (int i = 0; i < shelfCount; i++) {
            if (!scan.hasNextLine()) {
                System.out.println("Number of shelves doesn't match expected");
                return Code.SHELF_NUMBER_PARSE_ERROR;
            }
            String line = scan.nextLine();
            String[] parts = line.split(",", -1);
            if (parts.length < 2) {
                System.out.println("Number of shelves doesn't match expected");
                return Code.SHELF_NUMBER_PARSE_ERROR;
            }
            int number;
            try {
                number = Integer.parseInt(parts[0].trim());
            } catch (NumberFormatException nfe) {
                return Code.SHELF_NUMBER_PARSE_ERROR;
            }
            String subject = parts[1];
            addShelf(subject, number);
        }
        if (shelves.size() == start + shelfCount) {
            return Code.SUCCESS;
        }
        System.out.println("Number of shelves doesn't match expected");
        return Code.SHELF_NUMBER_PARSE_ERROR;
    }

    private Code initReader(int readerCount, java.util.Scanner scan) {
        if (readerCount <= 0) {
            return Code.READER_COUNT_ERROR;
        }
        for (int i = 0; i < readerCount; i++) {
            if (!scan.hasNextLine()) {
                return Code.READER_COUNT_ERROR;
            }
            String line = scan.nextLine();
            String[] parts = line.split(",", -1);
            int cardNumber = Integer.parseInt(parts[0].trim());
            String name = parts[1];
            String phone = parts[2];
            int bookN = Integer.parseInt(parts[Reader.BOOK_COUNT_].trim());
            Reader reader = new Reader(cardNumber, name, phone);
            addReader(reader);
            for (int b = 0; b < bookN; b++) {
                int isbnIdx = Reader.BOOK_START_ + b * 2;
                int dueIdx = isbnIdx + 1;
                if (isbnIdx >= parts.length || dueIdx >= parts.length) continue;
                String isbn = parts[isbnIdx];
                String dueS = parts[dueIdx];
                java.time.LocalDate due = convertDate(dueS, Code.DATE_CONVERSION_ERROR);
                Book book = getBookByISBN(isbn);
                if (book == null) {
                    System.out.println("ERROR");
                    continue;
                }
                checkOutBook(reader, book);
            }
        }
        return Code.SUCCESS;
    }

    public static java.time.LocalDate convertDate(String date, Code errorCode) {
        if (date == null) {
            System.out.println("ERROR: date conversion error, could not parse " + date);
            System.out.println("Using default date (01-jan-1970)");
            return java.time.LocalDate.of(1970, 1, 1);
        }
        if ("0000".equals(date)) {
            return java.time.LocalDate.of(1970, 1, 1);
        }
        String[] parts = date.split("-", -1);
        if (parts.length != 3) {
            System.out.println("ERROR: date conversion error, could not parse " + date);
            System.out.println("Using default date (01-jan-1970)");
            return java.time.LocalDate.of(1970, 1, 1);
        }
        int y, m, d;
        try {
            y = Integer.parseInt(parts[0].trim());
            m = Integer.parseInt(parts[1].trim());
            d = Integer.parseInt(parts[2].trim());
        } catch (Exception ex) {
            System.out.println("ERROR: date conversion error, could not parse " + date);
            System.out.println("Using default date (01-jan-1970)");
            return java.time.LocalDate.of(1970, 1, 1);
        }
        boolean bad = false;
        if (y < 0) { System.out.println("Error converting date: Year " + y); bad = true; }
        if (m < 0) { System.out.println("Error converting date: Month " + m); bad = true; }
        if (d < 0) { System.out.println("Error converting date: Dat " + d); bad = true; }
        if (bad) {
            System.out.println("Using default date (01-jan-1970)");
            return java.time.LocalDate.of(1970, 1, 1);
        }
        return java.time.LocalDate.of(y, m, d);
    }

    public Code addShelf(String shelfSubject) {
        int next = shelves.size() + 1;
        Shelf shelf = new Shelf(next, shelfSubject);
        return addShelf(shelf);
    }

    public Code addShelf(Shelf shelf) {
        if (shelves.containsKey(shelf.getSubject())) {
            System.out.println("ERROR: Shelf already exists " + shelf);
            return Code.SHELF_EXISTS_ERROR;
        }
        int max = 0;
        for (Shelf s : shelves.values()) {
            if (s.getShelfNumber() > max) max = s.getShelfNumber();
        }
        int next = max + 1;
        shelf.setShelfNumber(next);
        shelves.put(shelf.getSubject(), shelf);
        for (java.util.Map.Entry<Book, Integer> e : books.entrySet()) {
            Book b = e.getKey();
            if (shelf.getSubject().equals(b.getSubject())) {
                int copies = e.getValue();
                for (int i = 0; i < copies; i++) {
                    shelf.addBook(b);
                }
            }
        }
        return Code.SUCCESS;
    }

    public static int getLibraryCardNumber() {
        return libraryCard + 1;
    }

    public Shelf getShelf(String subject) {
        Shelf s = shelves.get(subject);
        if (s == null) {
            System.out.println("No shelf for " + subject + " books");
        }
        return s;
    }

    public Shelf getShelf(Integer shelfNumber) {
        for (Shelf s : shelves.values()) {
            if (s.getShelfNumber() == shelfNumber) {
                return s;
            }
        }
        System.out.println("No shelf number " + shelfNumber + " found");
        return null;
    }


}
