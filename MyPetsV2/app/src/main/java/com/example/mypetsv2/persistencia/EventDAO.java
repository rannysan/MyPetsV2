package com.example.mypetsv2.persistencia;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.mypetsv2.modelo.Events;

import java.util.List;

@Dao
public interface EventDAO {
    @Insert
    long insert(Events events);

    @Delete
    void delete(Events events);

    @Update
    void update(Events events);

    @Query("SELECT * FROM events WHERE id = :id")
    Events queryForId(long id);

    @Query("SELECT * FROM events ORDER BY nome ASC")
    List<Events> queryAll();

    @Query("SELECT * FROM events WHERE petId = :id ORDER BY nome ASC")
    List<Events> queryForPetId(long id);
}
