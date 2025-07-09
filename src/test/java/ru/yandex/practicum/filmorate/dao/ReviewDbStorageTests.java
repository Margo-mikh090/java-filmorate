package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.reviews.ReviewDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(ReviewDbStorage.class)
public class ReviewDbStorageTests {
    private final ReviewDbStorage reviewDbStorage;
    private final JdbcTemplate jdbc;

    private Review review1;
    private Review review2;

    @BeforeEach
    public void beforeEach() {
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

        review1 = Review.builder()
                .content("Отличный фильм!")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .useful(0)
                .build();

        review2 = Review.builder()
                .content("Не понравилось")
                .isPositive(false)
                .userId(2L)
                .filmId(2L)
                .useful(0)
                .build();

        review1 = reviewDbStorage.create(review1);
        review2 = reviewDbStorage.create(review2);
    }

    @Test
    public void testCreateReview() {
        Review newReview = Review.builder()
                .content("Новый отзыв")
                .isPositive(true)
                .userId(1L)
                .filmId(2L)
                .useful(0)
                .build();

        Review createdReview = reviewDbStorage.create(newReview);

        assertNotNull(createdReview.getReviewId());
        assertEquals(newReview.getContent(), createdReview.getContent());
        assertEquals(newReview.getIsPositive(), createdReview.getIsPositive());
        assertEquals(0, createdReview.getUseful());
    }

    @Test
    public void testUpdateReview() {
        review1.setContent("Обновленный отзыв");
        review1.setIsPositive(false);

        Review updatedReview = reviewDbStorage.update(review1);

        assertEquals(review1.getReviewId(), updatedReview.getReviewId());
        assertEquals("Обновленный отзыв", updatedReview.getContent());
        assertFalse(updatedReview.getIsPositive());
    }

    @Test
    public void testGetReviewById() {
        Optional<Review> foundReview = reviewDbStorage.getById(review1.getReviewId());

        assertTrue(foundReview.isPresent());
        assertEquals(review1.getContent(), foundReview.get().getContent());
    }

    @Test
    public void testDeleteReview() {
        reviewDbStorage.delete(review1.getReviewId());

        Optional<Review> deletedReview = reviewDbStorage.getById(review1.getReviewId());
        assertFalse(deletedReview.isPresent());
    }

    @Test
    public void testGetReviewsByFilmId() {
        Review anotherReview = Review.builder()
                .content("Еще один отзыв")
                .isPositive(true)
                .userId(2L)
                .filmId(1L)
                .useful(0)
                .build();
        reviewDbStorage.create(anotherReview);

        List<Review> reviews = reviewDbStorage.getReviewsByFilmId(1L, 10);

        assertEquals(2, reviews.size());
        assertThat(reviews).extracting(Review::getFilmId).containsOnly(1L);
    }

    @Test
    public void testAddLike() {
        reviewDbStorage.addLike(review1.getReviewId(), 2L);

        Optional<Review> updatedReview = reviewDbStorage.getById(review1.getReviewId());
        assertTrue(updatedReview.isPresent());
        assertEquals(1, updatedReview.get().getUseful());
    }

    @Test
    public void testAddDislike() {
        reviewDbStorage.addDislike(review1.getReviewId(), 2L);

        Optional<Review> updatedReview = reviewDbStorage.getById(review1.getReviewId());
        assertTrue(updatedReview.isPresent());
        assertEquals(-1, updatedReview.get().getUseful());
    }

    @Test
    public void testRemoveLike() {
        reviewDbStorage.addLike(review1.getReviewId(), 2L);
        reviewDbStorage.removeLike(review1.getReviewId(), 2L);

        Optional<Review> updatedReview = reviewDbStorage.getById(review1.getReviewId());
        assertTrue(updatedReview.isPresent());
        assertEquals(0, updatedReview.get().getUseful());
    }

    @Test
    public void testRemoveDislike() {
        reviewDbStorage.addDislike(review1.getReviewId(), 2L);
        reviewDbStorage.removeDislike(review1.getReviewId(), 2L);

        Optional<Review> updatedReview = reviewDbStorage.getById(review1.getReviewId());
        assertTrue(updatedReview.isPresent());
        assertEquals(0, updatedReview.get().getUseful());
    }

    @Test
    public void testGetUserReviewRating() {
        assertNull(reviewDbStorage.getUserReviewRating(review1.getReviewId(), 2L));

        reviewDbStorage.addLike(review1.getReviewId(), 2L);
        assertTrue(reviewDbStorage.getUserReviewRating(review1.getReviewId(), 2L));

        reviewDbStorage.addDislike(review1.getReviewId(), 2L);
        assertFalse(reviewDbStorage.getUserReviewRating(review1.getReviewId(), 2L));
    }
}
