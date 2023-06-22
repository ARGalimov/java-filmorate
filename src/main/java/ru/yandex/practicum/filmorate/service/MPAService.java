package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.List;

@Service
public class MPAService {
    private final MPAStorage mPAStorage;

    @Autowired
    public MPAService(@Qualifier("MPADbStorage") MPAStorage mPAStorage) {
        this.mPAStorage = mPAStorage;
    }

    public List<MPA> getAllRatings() {
        return mPAStorage.getAllRatings();
    }

    public MPA getRatingById(Integer id) {
        return mPAStorage.getRatingById(id);
    }
}