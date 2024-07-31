package com.dhiraj.book.book;

import com.dhiraj.book.user.User;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.dhiraj.book.Common.PageResponse;
import com.dhiraj.book.history.BookTransactionHistory;
import com.dhiraj.book.history.BookTransactionHistoryRepository;

import org.springframework.data.domain.Page;

import static com.dhiraj.book.book.BookSpecification.withOwnerId;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

import com.dhiraj.book.exception.OperationNotPermittedException;
import com.dhiraj.book.file.FileStorageService;

@Service
@RequiredArgsConstructor
public class BookService {
        private final BookRepository bookRepository;
        private final BookTransactionHistoryRepository transactionHistoryRepository;
        private final BookMapper bookMapper;
        private final FileStorageService fileStorageService;

        /**
         * This method saves a book into the database.
         *
         * @param request       The details of the book to be saved.
         * @param connectedUser The authenticated user.
         * @return The ID of the saved book.
         */
        public Integer saveBook(BookRequest request, Authentication connectedUser) {
                // Get the authenticated user
                User user = ((User) connectedUser.getPrincipal());

                // Map the request to a Book object
                Book book = bookMapper.toBook(request);

                // Set the owner of the book to the authenticated user
                book.setOwner(user);

                // Save the book in the database and return its ID
                return bookRepository.save(book).getId();
        }

        public BookResponse findById(Integer Id) {

                return bookRepository.findById(Id)
                                .map(bookMapper::toBookResponse)
                                .orElseThrow(() -> new EntityNotFoundException(" No book found with Id :: " + Id));
        }

        public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
                User user = ((User) connectedUser.getPrincipal());
                Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
                Page<Book> books = bookRepository.findAllDisplayableBooks(user.getId(), pageable);
                List<BookResponse> booksResponse = books.stream()
                                .map(bookMapper::toBookResponse)
                                .toList();
                return new PageResponse<>(
                                booksResponse,
                                books.getNumber(),
                                books.getSize(),
                                books.getTotalElements(),
                                books.getTotalPages(),
                                books.isFirst(),
                                books.isLast());
        }

        public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
                User user = ((User) connectedUser.getPrincipal());
                Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
                Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
                List<BookResponse> booksResponse = books.stream()
                                .map(bookMapper::toBookResponse)
                                .toList();
                return new PageResponse<>(
                                booksResponse,
                                books.getNumber(),
                                books.getSize(),
                                books.getTotalElements(),
                                books.getTotalPages(),
                                books.isFirst(),
                                books.isLast());
        }

        public PageResponse<BorrowedBookResponse> findAllBorrowedBook(int page, int size,
                        Authentication connectedUser) {
                User user = ((User) connectedUser.getPrincipal());
                Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
                Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBook(
                                pageable,
                                user.getId());
                List<BorrowedBookResponse> bookResponse = allBorrowedBooks
                                .stream()
                                .map(bookMapper::toBorrowedBookResponse)
                                .toList();
                return new PageResponse<>(
                                bookResponse,
                                allBorrowedBooks.getNumber(),
                                allBorrowedBooks.getSize(),
                                allBorrowedBooks.getTotalElements(),
                                allBorrowedBooks.getTotalPages(),
                                allBorrowedBooks.isFirst(),
                                allBorrowedBooks.isLast());
        }

        public PageResponse<BorrowedBookResponse> findAllReturnedBook(int page, int size,
                        Authentication connectedUser) {
                User user = ((User) connectedUser.getPrincipal());
                Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
                Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllReturnedBook(
                                pageable,
                                user.getId());
                List<BorrowedBookResponse> bookResponse = allBorrowedBooks
                                .stream()
                                .map(bookMapper::toBorrowedBookResponse)
                                .toList();
                return new PageResponse<>(
                                bookResponse,
                                allBorrowedBooks.getNumber(),
                                allBorrowedBooks.getSize(),
                                allBorrowedBooks.getTotalElements(),
                                allBorrowedBooks.getTotalPages(),
                                allBorrowedBooks.isFirst(),
                                allBorrowedBooks.isLast());
        }

        public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
                Book book = bookRepository
                                .findById(bookId)
                                .orElseThrow(() -> new EntityNotFoundException("No book found with Id :: " + bookId));
                User user = ((User) connectedUser.getPrincipal());
                if (!Objects.equals(book.getOwner().getId(), user.getId())) {
                        throw new OperationNotPermittedException("You cannot update others books shareable status");
                }
                book.setShareable(!book.isShareable());
                bookRepository.save(book);
                return bookId;
        }

        public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
                Book book = bookRepository
                                .findById(bookId)
                                .orElseThrow(() -> new EntityNotFoundException("No book found with Id :: " + bookId));
                User user = ((User) connectedUser.getPrincipal());
                if (!Objects.equals(book.getOwner().getId(), user.getId())) {
                        throw new OperationNotPermittedException("You cannot update others books archived status");
                }
                book.setArchived(!book.isArchived());
                bookRepository.save(book);
                return bookId;
        }

        public Integer borrowBook(Integer bookId, Authentication connectedUser) {
                Book book = bookRepository
                                .findById(bookId)
                                .orElseThrow(() -> new EntityNotFoundException("No book found with Id :: " + bookId));
                if (book.isArchived() || !book.isShareable()) {
                        throw new OperationNotPermittedException("You cannot borrow an archived or unshareable book");
                }
                User user = ((User) connectedUser.getPrincipal());
                if (Objects.equals(book.getOwner().getId(), user.getId())) {
                        throw new OperationNotPermittedException("You can not borrowed own Book");
                }
                final boolean isAlreadyBorrowed = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId,
                                user.getId());
                if (isAlreadyBorrowed) {
                        throw new OperationNotPermittedException("This Book is already borrowed..");
                }
                BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                                .user(user)
                                .book(book)
                                .returned(false)
                                .returnApproved(false)
                                .build();
                return transactionHistoryRepository.save(bookTransactionHistory).getId();
        }

        public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
                Book book = bookRepository
                                .findById(bookId)
                                .orElseThrow(() -> new EntityNotFoundException("No book found with Id :: " + bookId));
                if (book.isArchived() || !book.isShareable()) {
                        throw new OperationNotPermittedException("You cannot borrow an archived or unshareable book");
                }
                User user = ((User) connectedUser.getPrincipal());
                if (Objects.equals(book.getOwner().getId(), user.getId())) {
                        throw new OperationNotPermittedException("You can not borrowed or returned your own Book");
                }
                BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndUserId(
                                bookId, user.getId())
                                .orElseThrow(() -> new OperationNotPermittedException(
                                                "You didn't Borrow this book so you cann't returned it"));
                bookTransactionHistory.setReturned(true);
                return transactionHistoryRepository.save(bookTransactionHistory).getId();
        }

        public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
                Book book = bookRepository
                                .findById(bookId)
                                .orElseThrow(() -> new EntityNotFoundException("No book found with Id :: " + bookId));
                if (book.isArchived() || !book.isShareable()) {
                        throw new OperationNotPermittedException("You cannot borrow an archived or unshareable book");
                }
                User user = ((User) connectedUser.getPrincipal());
                if (Objects.equals(book.getOwner().getId(), user.getId())) {
                        throw new OperationNotPermittedException("You can not borrowed or returned your own Book");
                }
                BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndOwnerId(
                                bookId, user.getId())
                                .orElseThrow(() -> new OperationNotPermittedException(
                                                "The book is not returned yet. You cannot approve its return"));
                bookTransactionHistory.setReturnApproved(true);
                return transactionHistoryRepository.save(bookTransactionHistory).getId();
        }

        public void updatedCoverImage(Integer bookId, MultipartFile file, Authentication connectedUser) {
                Book book = bookRepository
                                .findById(bookId)
                                .orElseThrow(() -> new EntityNotFoundException("No book found with Id :: " + bookId));
                User user = ((User) connectedUser.getPrincipal());
                var bookCover = fileStorageService.saveFile(file, user.getId());
                book.setBookCover(bookCover);
                bookRepository.save(book);
        }
}