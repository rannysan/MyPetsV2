package com.example.mypetsv2.persistencia;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.mypetsv2.R;
import com.example.mypetsv2.modelo.Events;
import com.example.mypetsv2.modelo.Pets;

import java.util.concurrent.Executors;

@Database(entities = {Events.class, Pets.class}, version = 1)
public abstract class EventsDatabase extends RoomDatabase {
    public abstract EventDAO eventDao();

    public abstract PetDAO petDao();

    private static EventsDatabase instance;

    public static EventsDatabase getDatabase(final Context context) {

        if (instance == null) {

            synchronized (EventsDatabase.class) {
                if (instance == null) {
                    RoomDatabase.Builder builder =  Room.databaseBuilder(context,
                            EventsDatabase.class,
                            "events.db");

                    builder.addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    carregaPetsIniciais(context);
                                }
                            });
                        }
                    });

                    instance = (EventsDatabase) builder.build();
                }
            }
        }

        return instance;
    }

    private static void carregaPetsIniciais(final Context context){

        String[] names = context.getResources().getStringArray(R.array.pets_iniciais);

        for (String name : names) {

            Pets pet = new Pets(name);

            instance.petDao().insert(pet);
        }
    }
}
