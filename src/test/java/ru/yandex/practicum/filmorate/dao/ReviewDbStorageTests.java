package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.storage.reviews.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.reviews.ReviewStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
public class ReviewDbStorageTests {
    private final ReviewStorage reviewStorage;
    private final JdbcTemplate jdbc;
    private Review review1;
    private Review review2;

    @Autowired
    public ReviewDbStorageTests(ReviewStorage reviewStorage, JdbcTemplate jdbc) {
        this.reviewStorage = reviewStorage;
        this.jdbc = jdbc;
    }

    @BeforeEach
    void setUp() {
        // Очистка данных
        jdbc.update("DELETE FROM review_likes");
        jdbc.update("DELETE FROM reviews");
        jdbc.update("DELETE FROM films");
        jdbc.update("DELETE FROM users");

        // Подготовка тестовых данных
        jdbc.update("INSERT INTO users (id, email, login, name, birthday) VALUES (?, ?, ?, ?, ?)",
                1L, "user1@mail.com", "login1", "User 1", "1990-01-01");
        jdbc.update("INSERT INTO users (id, email, login, name, birthday) VALUES (?, ?, ?, ?, ?)",
                2L, "user2@mail.com", "login2", "User 2", "1990-01-01");

        jdbc.update("INSERT INTO films (id, name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?, ?)",
                1L, "Film 1", "Description 1", "2000-01-01", 120, 1);
        jdbc.update("INSERT INTO films (id, name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?, ?)",
                2L, "Film 2", "Description 2", "2000-01-01", 120, 1);

        // Создание тестовых отзывов
        review1 = reviewStorage.create(Review.builder()
                .content("Отличный фильм!")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .build());

        review2 = reviewStorage.create(Review.builder()
                .content("Не понравилось")
                .isPositive(false)
                .userId(2L)
                .filmId(2L)
                .build());
    }

    @Test
    void create_shouldCreateAndReturnReviewWithZeroUseful() {
        Review newReview = Review.builder()
                .content("Новый отзыв")
                .isPositive(true)
                .userId(1L)
                .filmId(2L)
                .build();

        Review created = reviewStorage.create(newReview);

        assertNotNull(created.getReviewId());
        assertEquals("Новый отзыв", created.getContent());
        assertEquals(1L, created.getUserId());
        assertEquals(2L, created.getFilmId());
        assertTrue(created.getIsPositive());
        assertEquals(0, created.getUseful());
    }

    @Test
    void update_shouldUpdateExistingReview() {
        review1.setContent("Обновленный контент");
        review1.setIsPositive(false);

        Review updated = reviewStorage.update(review1);

        assertEquals(review1.getReviewId(), updated.getReviewId());
        assertEquals("Обновленный контент", updated.getContent());
        assertFalse(updated.getIsPositive());
    }

    @Test
    void getById_shouldReturnReviewWhenExists() {
        Optional<Review> found = reviewStorage.getById(review1.getReviewId());

        assertTrue(found.isPresent());
        assertEquals(review1.getContent(), found.get().getContent());
    }

    @Test
    void getById_shouldReturnEmptyWhenNotExists() {
        Optional<Review> found = reviewStorage.getById(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void delete_shouldRemoveReview() {
        reviewStorage.delete(review1.getReviewId());
        assertTrue(reviewStorage.getById(review1.getReviewId()).isEmpty());
    }

    @Test
    void getReviewsByFilmId_shouldReturnReviewsForFilm() {
        Review anotherReview = reviewStorage.create(Review.builder()
                .content("Еще один отзыв")
                .isPositive(true)
                .userId(2L)
                .filmId(1L)
                .build());

        List<Review> reviews = reviewStorage.getReviewsByFilmId(1L, 10);

        assertEquals(2, reviews.size());
        assertTrue(reviews.stream().anyMatch(r -> r.getReviewId().equals(review1.getReviewId())));
        assertTrue(reviews.stream().anyMatch(r -> r.getReviewId().equals(anotherReview.getReviewId())));
    }

    @Test
    void getReviewsByFilmId_shouldReturnEmptyListWhenNoReviews() {
        List<Review> reviews = reviewStorage.getReviewsByFilmId(999L, 10);
        assertTrue(reviews.isEmpty());
    }

    @Test
    void addLike_shouldIncreaseUseful() {
        reviewStorage.addLike(review1.getReviewId(), 2L);
        Optional<Review> updated = reviewStorage.getById(review1.getReviewId());
        assertTrue(updated.isPresent());
        assertEquals(1, updated.get().getUseful());
    }

    @Test
    void addDislike_shouldDecreaseUseful() {
        reviewStorage.addDislike(review1.getReviewId(), 2L);
        Optional<Review> updated = reviewStorage.getById(review1.getReviewId());
        assertTrue(updated.isPresent());
        assertEquals(-1, updated.get().getUseful());
    }

    @Test
    void removeLike_shouldDecreaseUseful() {
        reviewStorage.addLike(review1.getReviewId(), 2L);
        reviewStorage.removeLike(review1.getReviewId(), 2L);
        Optional<Review> updated = reviewStorage.getById(review1.getReviewId());
        assertTrue(updated.isPresent());
        assertEquals(0, updated.get().getUseful());
    }

    @Test
    void removeDislike_shouldIncreaseUseful() {
        reviewStorage.addDislike(review1.getReviewId(), 2L);
        reviewStorage.removeDislike(review1.getReviewId(), 2L);
        Optional<Review> updated = reviewStorage.getById(review1.getReviewId());
        assertTrue(updated.isPresent());
        assertEquals(0, updated.get().getUseful());
    }

    @Test
    void getUserReviewRating_shouldReturnRatingWhenExists() {
        assertNull(reviewStorage.getUserReviewRating(review1.getReviewId(), 2L));

        reviewStorage.addLike(review1.getReviewId(), 2L);
        assertTrue(reviewStorage.getUserReviewRating(review1.getReviewId(), 2L));

        reviewStorage.addDislike(review1.getReviewId(), 2L);
        assertFalse(reviewStorage.getUserReviewRating(review1.getReviewId(), 2L));
    }
}