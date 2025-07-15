package ru.yandex.practicum.filmorate.storage.reviews;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {
    private final JdbcTemplate jdbc;
    private final ReviewRowMapper reviewRowMapper;

    public ReviewDbStorage(JdbcTemplate jdbc, ReviewRowMapper reviewRowMapper) {
        super(jdbc, reviewRowMapper);
        this.jdbc = jdbc;
        this.reviewRowMapper = reviewRowMapper;
    }

    private static final String GET_BY_ID = "SELECT * FROM reviews WHERE review_id = ?";

    private static final String GET_BY_FILM_ID =
            "SELECT * FROM reviews " +
                    "WHERE (? IS NULL OR film_id = ?) " +
                    "ORDER BY useful DESC " +
                    "LIMIT ?";

    private static final String UPDATE = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
    private static final String DELETE = "DELETE FROM reviews WHERE review_id = ?";
    private static final String GET_USER_RATING = "SELECT is_positive FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String INSERT_LIKE = "INSERT INTO review_likes VALUES (?, ?, ?)";
    private static final String UPDATE_LIKE = "UPDATE review_likes SET is_positive = ? WHERE review_id = ? AND user_id = ?";
    private static final String UPDATE_USEFUL = "UPDATE reviews SET useful = useful + ? WHERE review_id = ?";
    private static final String DELETE_RATING = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_positive = ?";

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbc)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        Map<String, Object> params = Map.of(
                "content", review.getContent(),
                "is_positive", review.getIsPositive(),
                "user_id", review.getUserId(),
                "film_id", review.getFilmId(),
                "useful", 0
        );

        Long id = insert.executeAndReturnKey(params).longValue();
        review.setReviewId(id);
        review.setUseful(0);
        return review;
    }

    @Override
    public Review update(Review review) {
        update(UPDATE,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        return getById(review.getReviewId()).orElseThrow();
    }

    @Override
    public void delete(Long id) {
        deleteById(DELETE, id);
    }

    @Override
    public Optional<Review> getById(Long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(GET_BY_ID, reviewRowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        return jdbc.query(GET_BY_FILM_ID, reviewRowMapper,
                filmId,
                filmId,
                count
        );
    }

    @Override
    public Boolean getUserReviewRating(Long reviewId, Long userId) {
        try {
            return jdbc.queryForObject(GET_USER_RATING, Boolean.class, reviewId, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        updateReviewRating(reviewId, userId, true, 1);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        updateReviewRating(reviewId, userId, false, -1);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        removeRating(reviewId, userId, true);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        removeRating(reviewId, userId, false);
    }

    private void updateReviewRating(Long reviewId, Long userId, boolean newRating, int usefulDelta) {
        Boolean currentRating = getUserReviewRating(reviewId, userId);

        if (currentRating == null) {
            jdbc.update(INSERT_LIKE, reviewId, userId, newRating);
            jdbc.update(UPDATE_USEFUL, usefulDelta, reviewId);
        } else if (currentRating != newRating) {
            jdbc.update(UPDATE_LIKE, newRating, reviewId, userId);
            jdbc.update(UPDATE_USEFUL, usefulDelta * 2, reviewId);
        }
    }

    private void removeRating(Long reviewId, Long userId, boolean isLike) {
        int deleted = jdbc.update(DELETE_RATING, reviewId, userId, isLike);

        if (deleted > 0) {
            int usefulDelta = isLike ? -1 : 1;
            jdbc.update(UPDATE_USEFUL, usefulDelta, reviewId);
        }
    }
}
