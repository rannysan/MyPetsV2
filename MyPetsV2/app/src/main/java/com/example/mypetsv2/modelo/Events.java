package com.example.mypetsv2.modelo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.mypetsv2.PetsActivity;
import com.example.mypetsv2.persistencia.EventsDatabase;
import com.example.mypetsv2.persistencia.PetDAO;

@Entity(tableName = "events",
        foreignKeys = @ForeignKey(entity = Pets.class,
                parentColumns = "id",
                childColumns  = "petId"))
public class Events {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String nome;

    private String date;

    @ColumnInfo(index = true)
    private int petId;

    public Events(String nome){
        setNome(nome);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getNome() {
        return nome;
    }

    public void setNome(@NonNull String nome) {
        this.nome = nome;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    @Override
    public String toString(){
        String eventView = getNome() + " - " + getDate();

        return eventView;
    }
}
