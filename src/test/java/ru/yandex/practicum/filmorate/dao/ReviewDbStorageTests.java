package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.reviews.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.reviews.ReviewStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
public class ReviewDbStorageTests {
    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final JdbcTemplate jdbc;
    private Review review1;
    private Review review2;

    @Autowired
    public ReviewDbStorageTests(ReviewStorage reviewStorage, ReviewLikeStorage reviewLikeStorage, JdbcTemplate jdbc) {
        this.reviewStorage = reviewStorage;
        this.reviewLikeStorage = reviewLikeStorage;
        this.jdbc = jdbc;
    }

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM review_likes");
        jdbc.update("DELETE FROM reviews");
        jdbc.update("DELETE FROM films");
        jdbc.update("DELETE FROM users");

        jdbc.update("INSERT INTO users (id, email, login, name, birthday) VALUES (?, ?, ?, ?, ?)",
                1L, "user1@mail.com", "login1", "User 1", "1990-01-01");
        jdbc.update("INSERT INTO users (id, email, login, name, birthday) VALUES (?, ?, ?, ?, ?)",
                2L, "user2@mail.com", "login2", "User 2", "1990-01-01");

        jdbc.update("INSERT INTO films (id, name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?, ?)",
                1L, "Film 1", "Description 1", "2000-01-01", 120, 1);
        jdbc.update("INSERT INTO films (id, name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?, ?)",
                2L, "Film 2", "Description 2", "2000-01-01", 120, 1);

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

        assertThat(created.getReviewId()).isNotNull();
        assertThat(created.getContent()).isEqualTo("Новый отзыв");
        assertThat(created.getUserId()).isEqualTo(1L);
        assertThat(created.getFilmId()).isEqualTo(2L);
        assertThat(created.getIsPositive()).isTrue();
        assertThat(created.getUseful()).isZero();
    }

    @Test
    void update_shouldUpdateExistingReview() {
        review1.setContent("Обновленный контент");
        review1.setIsPositive(false);

        Review updated = reviewStorage.update(review1);

        assertThat(updated.getReviewId()).isEqualTo(review1.getReviewId());
        assertThat(updated.getContent()).isEqualTo("Обновленный контент");
        assertThat(updated.getIsPositive()).isFalse();
    }

    @Test
    void getById_shouldReturnReviewWhenExists() {
        Optional<Review> found = reviewStorage.getById(review1.getReviewId());

        assertThat(found)
                .isPresent()
                .get()
                .extracting(Review::getContent)
                .isEqualTo(review1.getContent());
    }

    @Test
    void getById_shouldReturnEmptyWhenNotExists() {
        Optional<Review> found = reviewStorage.getById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void delete_shouldRemoveReview() {
        reviewStorage.delete(review1.getReviewId());
        assertThat(reviewStorage.getById(review1.getReviewId())).isEmpty();
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

        assertThat(reviews)
                .hasSize(2)
                .extracting(Review::getReviewId)
                .contains(review1.getReviewId(), anotherReview.getReviewId());
    }

    @Test
    void getReviewsByFilmId_shouldReturnEmptyListWhenNoReviews() {
        List<Review> reviews = reviewStorage.getReviewsByFilmId(999L, 10);
        assertThat(reviews).isEmpty();
    }

    @Test
    void addLike_shouldIncreaseUseful() {
        reviewLikeStorage.addLike(review1.getReviewId(), 2L);
        Optional<Review> updated = reviewStorage.getById(review1.getReviewId());

        assertThat(updated)
                .isPresent()
                .get()
                .extracting(Review::getUseful)
                .isEqualTo(1);
    }

    @Test
    void addDislike_shouldDecreaseUseful() {
        reviewLikeStorage.addDislike(review1.getReviewId(), 2L);
        Optional<Review> updated = reviewStorage.getById(review1.getReviewId());

        assertThat(updated)
                .isPresent()
                .get()
                .extracting(Review::getUseful)
                .isEqualTo(-1);
    }

    @Test
    void removeLike_shouldDecreaseUseful() {
        reviewLikeStorage.addLike(review1.getReviewId(), 2L);
        reviewLikeStorage.removeLike(review1.getReviewId(), 2L);
        Optional<Review> updated = reviewStorage.getById(review1.getReviewId());

        assertThat(updated)
                .isPresent()
                .get()
                .extracting(Review::getUseful)
                .isEqualTo(0);
    }

    @Test
    void removeDislike_shouldIncreaseUseful() {
        reviewLikeStorage.addDislike(review1.getReviewId(), 2L);
        reviewLikeStorage.removeDislike(review1.getReviewId(), 2L);
        Optional<Review> updated = reviewStorage.getById(review1.getReviewId());

        assertThat(updated)
                .isPresent()
                .get()
                .extracting(Review::getUseful)
                .isEqualTo(0);
    }

    @Test
    void getUserReviewRating_shouldReturnRatingWhenExists() {
        assertThat(reviewStorage.getUserReviewRating(review1.getReviewId(), 2L)).isNull();

        reviewLikeStorage.addLike(review1.getReviewId(), 2L);
        assertThat(reviewStorage.getUserReviewRating(review1.getReviewId(), 2L)).isTrue();

        reviewLikeStorage.addDislike(review1.getReviewId(), 2L);
        assertThat(reviewStorage.getUserReviewRating(review1.getReviewId(), 2L)).isFalse();
    }
}