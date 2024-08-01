package com.dhiraj.book.feedback;

import com.dhiraj.book.book.Book;
import java.util.Objects;

class FeedbackMapper {

    Feedback toFeedback(FeedbackRequest feedback) {

        return Feedback.builder()
                .rating(feedback.note())
                .comment(feedback.comment())
                .book(Book.builder()
                        .id(feedback.bookId())
                        .build())
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer id) {
        return FeedbackResponse.builder()
                .note(feedback.getRating())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), id))
                .build();
    }

}
