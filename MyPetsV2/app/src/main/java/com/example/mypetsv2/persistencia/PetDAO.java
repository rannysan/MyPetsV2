package com.example.mypetsv2.persistencia;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.mypetsv2.modelo.Pets;

import java.util.List;

@Dao
public interface PetDAO {
    @Insert
    long insert(Pets pet);

    @Delete
    void delete(Pets pet);

    @Update
    void update(Pets pet);

    @Query("SELECT * FROM pets WHERE id = :id")
    Pets queryForId(long id);

    @Query("SELECT * FROM pets ORDER BY name ASC")
    List<Pets> queryAll();

    @Query("SELECT * FROM pets WHERE name = :name ORDER BY name ASC")
    List<Pets> queryForName(String name);

    @Query("SELECT count(*) FROM pets")
    int total();
}
