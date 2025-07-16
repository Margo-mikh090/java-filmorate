package ru.yandex.practicum.filmorate.storage.reviews;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review create(Review review);

    Review update(Review review);

    void delete(Long id);

    Optional<Review> getById(Long id);

    List<Review> getReviewsByFilmId(Long filmId, int count);

    Boolean getUserReviewRating(Long reviewId, Long userId);
}