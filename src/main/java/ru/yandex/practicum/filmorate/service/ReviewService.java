package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;
import ru.yandex.practicum.filmorate.storage.reviews.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Review create(Review review) {
        validateUserAndFilm(review.getUserId(), review.getFilmId());
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        getById(review.getReviewId());
        return reviewStorage.update(review);
    }

    public void delete(Long id) {
        reviewStorage.delete(id);
    }

    public Review getById(Long id) {
        return reviewStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id=" + id + " не найден"));
    }

    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        if (filmId != null) {
            filmStorage.getById(filmId);
        }
        return reviewStorage.getReviewsByFilmId(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        validateAndUpdateRating(reviewId, userId, true);
    }

    public void addDislike(Long reviewId, Long userId) {
        validateAndUpdateRating(reviewId, userId, false);
    }

    public void removeLike(Long reviewId, Long userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.removeDislike(reviewId, userId);
    }

    private void validateAndUpdateRating(Long reviewId, Long userId, boolean isLike) {
        validateReviewAndUser(reviewId, userId);

        Boolean currentRating = reviewStorage.getUserReviewRating(reviewId, userId);
        if (currentRating != null && currentRating == isLike) {
            throw new ConditionsNotMetException(String.format(
                    "Пользователь уже поставил %s этому отзыву",
                    isLike ? "лайк" : "дизлайк"));
        }

        if (isLike) {
            reviewStorage.addLike(reviewId, userId);
        } else {
            reviewStorage.addDislike(reviewId, userId);
        }
    }

    private void validateUserAndFilm(Long userId, Long filmId) {
        userStorage.getById(userId);
        filmStorage.getById(filmId);
    }

    private void validateReviewAndUser(Long reviewId, Long userId) {
        getById(reviewId);
        userStorage.getById(userId);
    }
}
