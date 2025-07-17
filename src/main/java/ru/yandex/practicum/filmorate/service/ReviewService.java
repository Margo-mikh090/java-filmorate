package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;
import ru.yandex.practicum.filmorate.storage.reviews.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.reviews.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventService eventService;

    public Review create(Review review) {
        log.info("Получен запрос на создание отзыва: {}", review);
        validateUserAndFilm(review.getUserId(), review.getFilmId());
        Review createdReview = reviewStorage.create(review);
        eventService.addEvent(Event.builder()
                .userId(createdReview.getUserId())
                .entityId(createdReview.getReviewId())
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .build());
        return createdReview;
    }

    public Review update(Review review) {
        log.info("Получен запрос на обновление отзыва с id={}", review.getReviewId());
        getById(review.getReviewId());
        Review updatedReview = reviewStorage.update(review);
        eventService.addEvent(Event.builder()
                .userId(updatedReview.getUserId())
                .entityId(updatedReview.getReviewId())
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .build());
        return updatedReview;
    }

    public void delete(Long id) {
        log.info("Получен запрос на удаление отзыва с id={}", id);
        Review review = getById(id);
        eventService.addEvent(Event.builder()
                .userId(review.getUserId())
                .entityId(id)
                .eventType(EventType.REVIEW)
                .operation(Operation.REMOVE)
                .build());
        reviewStorage.delete(id);
    }

    public Review getById(Long id) {
        log.debug("Получение отзыва с id={}", id);
        return reviewStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id=" + id + " не найден"));
    }

    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        log.info("Получен запрос на получение отзывов для фильма с id={}, count={}", filmId, count);
        if (filmId != null) {
            filmStorage.getById(filmId);
        }
        return reviewStorage.getReviewsByFilmId(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        log.info("Получен запрос на добавление лайка отзыву с id={} от пользователя с id={}", reviewId, userId);
        validateAndUpdateRating(reviewId, userId, true);
    }

    public void addDislike(Long reviewId, Long userId) {
        log.info("Получен запрос на добавление дизлайка отзыву с id={} от пользователя с id={}", reviewId, userId);
        validateAndUpdateRating(reviewId, userId, false);
    }

    public void removeLike(Long reviewId, Long userId) {
        log.info("Получен запрос на удаление лайка отзыву с id={} от пользователя с id={}", reviewId, userId);
        validateReviewAndUser(reviewId, userId);
        reviewLikeStorage.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        log.info("Получен запрос на удаление дизлайка отзыву с id={} от пользователя с id={}", reviewId, userId);
        validateReviewAndUser(reviewId, userId);
        reviewLikeStorage.removeDislike(reviewId, userId);
    }

    private void validateAndUpdateRating(Long reviewId, Long userId, boolean isLike) {
        validateReviewAndUser(reviewId, userId);

        Boolean currentRating = reviewLikeStorage.getUserReviewRating(reviewId, userId);
        if (currentRating != null && currentRating == isLike) {
            throw new ConditionsNotMetException(String.format(
                    "Пользователь уже поставил %s этому отзыву",
                    isLike ? "лайк" : "дизлайк"));
        }

        if (isLike) {
            reviewLikeStorage.addLike(reviewId, userId);
        } else {
            reviewLikeStorage.addDislike(reviewId, userId);
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
