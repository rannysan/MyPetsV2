package com.example.mypetsv2.modelo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "pets",
        indices = @Index(value = {"name"}, unique = true))
public class Pets {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    public Pets(String name){
        setName(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return getName();
    }
}
