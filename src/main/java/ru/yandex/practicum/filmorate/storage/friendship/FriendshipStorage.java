package ru.yandex.practicum.filmorate.storage.friendship;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendshipStorage {
    void addFriend(long firstUserId, long secondUserId);

    void removeFriend(long firstUserId, long secondUserId);

    Collection<User> getUserFriends(long id);

    Collection<User> getCommonFriends(long firstUserId, long secondUserId);
}
