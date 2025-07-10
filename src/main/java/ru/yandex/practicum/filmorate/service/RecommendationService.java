package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    private final LikeStorage likeStorage;
    private final FilmStorage filmStorage;

    public List<Film> getRecommendations(long userId) {
        log.info("Запрос на получение списка рекомендованных фильмов для пользователя с id {}", userId);
        Map<Long, Set<Long>> allUserLikes = likeStorage.getAllUserLikes(); // выгрузка таблицы likes в нужном формате

        Set<Long> userLikes = allUserLikes.getOrDefault(userId, Collections.emptySet()); // проверка на наличие интересующего пользователя и его лайков
        if (userLikes.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> commonLikesCount = countCommonLikes(allUserLikes, userId, userLikes); // подсчёт общих лайков
        Optional<Long> mostSimilarUser = findSimilarUser(commonLikesCount); // определение наиболее похожего пользователя
        if (mostSimilarUser.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> recommendedFilms = getRecommendedFilms(allUserLikes, userLikes, mostSimilarUser.get());
        return recommendedFilms.stream().map(filmStorage::getById).toList();
    }

    private Map<Long, Long> countCommonLikes(Map<Long, Set<Long>> allUserLikes, long userId, Set<Long> userLikes) {
        Map<Long, Long> counts = new HashMap<>(); // другой пользователь, общие лайки
        allUserLikes.forEach((otherUserId, otherUserLikes) -> {
            if (otherUserId != userId) {
                long commonCount = otherUserLikes.stream()
                        .filter(userLikes::contains)
                        .count();
                if (commonCount > 0) {
                    counts.put(otherUserId, commonCount);
                }
            }
        });
        return counts;
    }

    private Optional<Long> findSimilarUser(Map<Long, Long> commonLikesCount) {
        return commonLikesCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .filter(entry -> entry.getValue() > 0)
                .map(Map.Entry::getKey);
    }

    private List<Long> getRecommendedFilms(Map<Long, Set<Long>> allUserLikes, Set<Long> userLikes, long similarUserId) {
        Set<Long> similarUserLikes = new HashSet<>(allUserLikes.get(similarUserId));
        similarUserLikes.removeAll(userLikes);
        return new ArrayList<>(similarUserLikes);
    }
}
