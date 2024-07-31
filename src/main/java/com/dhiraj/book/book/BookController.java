package com.dhiraj.book.book;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dhiraj.book.Common.PageResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {

    private final BookService service;

    /**
     * This method saves a book into the database.
     *
     * @param request       The details of the book to be saved.
     * @param connectedUser The authenticated user.
     * @return The ID of the saved book.
     */
    @PostMapping("/savebook")
    public ResponseEntity<Integer> saveBook(
            @Valid @RequestBody BookRequest request,
            Authentication connectedUser) {
        // Save the book using the service and return the saved book's ID.
        return ResponseEntity.ok(service.saveBook(request, connectedUser));
    }

    /**
     * Retrieves a book by its ID from the database.
     * 
     * @param bookId The ID of the book to retrieve.
     * @return The book with the specified ID, wrapped in a ResponseEntity
     *         with an OK status.
     */
    @GetMapping("/getBook/{book-id}")
    public ResponseEntity<BookResponse> findBookById(
            @PathVariable("book-id") Integer bookId) {
        // Retrieve the book from the service and wrap it in a ResponseEntity with
        return ResponseEntity.ok(service.findById(bookId));

    }

    @GetMapping("/getAll")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser) {

        return ResponseEntity.ok(service.findAllBooks(page, size, connectedUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.findAllBooksByOwner(page, size, connectedUser));
    }

    /**
     * Retrieves all borrowed books from the database.
     *
     * @param page          The page number of the results to retrieve.
     * @param size          The number of books to retrieve per page.
     * @param connectedUser The authenticated user.
     * @return The borrowed books, wrapped in a PageResponse with an OK status.
     */
    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBook(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser) {
        // Retrieve all borrowed books from the service and wrap them in a PageResponse.
        return ResponseEntity.ok(service.findAllBorrowedBook(page, size, connectedUser));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBook(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.findAllReturnedBook(page, size, connectedUser));
    }

    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.updateShareableStatus(bookId, connectedUser));
    }

    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.updateArchivedStatus(bookId, connectedUser));
    }

    @PostMapping("/borrow/{book-Id}")
    public ResponseEntity<Integer> borrowBook(@PathVariable("book-Id") Integer bookId, Authentication connectedUser) {
        return ResponseEntity.ok(service.borrowBook(bookId, connectedUser));
    }

    @PatchMapping("/borrow/returned/{book-id}")
    public ResponseEntity<Integer> returnBorrowBook(@PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.returnBorrowedBook(bookId, connectedUser));
    }

    @PatchMapping("/borrow/returned/approve/{book-id}")
    public ResponseEntity<Integer> approveReturnBorrowBook(@PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.approveReturnBorrowedBook(bookId, connectedUser));
    }

    @PatchMapping(path = "/cover/{book-id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateCover(@PathVariable("book-id") Integer bookId,
            @RequestParam("file") MultipartFile file, Authentication connectedUser) {
        service.updatedCoverImage(bookId, file, connectedUser);
        return ResponseEntity.accepted().build();
    }
}
