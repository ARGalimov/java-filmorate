package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) throws ValidationException {
        return userStorage.create(user);
    }

    public User update(User user) throws ValidationException, NoDataException {
        return userStorage.update(user);
    }

    public List<User> findAll() {
        return userStorage.getUserList();
    }

    public User getById(Integer id) throws NoDataException {
        return userStorage.getById(id);
    }

    public void addFriend(Integer userId, Integer friendId) {
        User user = userStorage.getById(userId);
        userStorage.getById(friendId);
        Map<Integer, Boolean> userFriends = user.getFriends();

        userFriends.put(friendId, false);
        user.setFriends(userFriends);
        userStorage.update(user);
    }

    public void deleteFriend(Integer userId, Integer friendId) throws NoDataException {
        User user = userStorage.getById(userId);
        Map<Integer, Boolean> userFriends = user.getFriends();
        userFriends.remove(friendId);
        user.setFriends(userFriends);
        userStorage.update(user);
    }

    public List<User> getFriends(Map<Integer, Boolean> ids) throws NoDataException {
        List<User> friends = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> entry : ids.entrySet()) {
            friends.add(userStorage.getById(entry.getKey()));
        }
        return friends;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) throws NoDataException {
        Map<Integer, Boolean> userFriendsIds = userStorage.getById(userId).getFriends();
        Map<Integer, Boolean> otherIdFriendsIds = userStorage.getById(otherId).getFriends();
        if (Objects.nonNull(userFriendsIds) && Objects.nonNull(otherIdFriendsIds)) {
            return addCommonFriends(userFriendsIds, otherIdFriendsIds);
        }
        return new ArrayList<>();
    }

    private List<User> addCommonFriends(Map<Integer, Boolean> userFriendsIds, Map<Integer, Boolean> otherIdFriendsIds) {
        List<User> commonFriends = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> entry : userFriendsIds.entrySet()) {
            if (otherIdFriendsIds.containsKey(entry.getKey())) {
                commonFriends.add(userStorage.getById(entry.getKey()));
            }
        }
        return commonFriends;
    }
}