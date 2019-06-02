package com.example.mypetsv2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.mypetsv2.modelo.Events;
import com.example.mypetsv2.modelo.Pets;
import com.example.mypetsv2.persistencia.EventsDatabase;
import com.example.mypetsv2.utils.UtilsGUI;

import java.util.List;

public class PetsActivity extends AppCompatActivity {
    private static final int REQUEST_NOVO_TIPO    = 1;
    private static final int REQUEST_ALTERAR_TIPO = 2;

    private ListView listViewPets;
    private ArrayAdapter<Pets> listaAdapter;
    private List<Pets> lista;

    public static void abrir(Activity activity){

        Intent intent = new Intent(activity, PetsActivity.class);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listViewPets = findViewById(R.id.listViewItens);

        listViewPets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Pets tipo = (Pets) parent.getItemAtPosition(position);

                PetActivity.alterar(PetsActivity.this,
                        REQUEST_ALTERAR_TIPO,
                        tipo);
            }
        });

        carregarPets();

        registerForContextMenu(listViewPets);

        setTitle(R.string.pets);
    }

    private void carregarPets(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                EventsDatabase database = EventsDatabase.getDatabase(PetsActivity.this);

                lista = database.petDao().queryAll();

                PetsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listaAdapter = new ArrayAdapter<>(PetsActivity.this,
                                android.R.layout.simple_list_item_1,
                                lista);

                        listViewPets.setAdapter(listaAdapter);
                    }
                });
            }
        });
    }

    private void verificarUsoPet(final Pets pet){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                EventsDatabase database = EventsDatabase.getDatabase(PetsActivity.this);

                List<Events> lista = database.eventDao().queryForPetId(pet.getId());

                if (lista != null && lista.size() > 0){

                    PetsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UtilsGUI.avisoErro(PetsActivity.this, R.string.pet_usado);
                        }
                    });

                    return;
                }

                PetsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        excluirPet(pet);
                    }
                });
            }
        });
    }

    private void excluirPet(final Pets pet){

        String mensagem = getString(R.string.confirmar_apagar) + "\n" + pet.getName();

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        EventsDatabase database =
                                                EventsDatabase.getDatabase(PetsActivity.this);

                                        database.petDao().delete(pet);

                                        PetsActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listaAdapter.remove(pet);
                                            }
                                        });
                                    }
                                });

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

        UtilsGUI.confirmaAcao(this, mensagem, listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode == REQUEST_NOVO_TIPO || requestCode == REQUEST_ALTERAR_TIPO)
                && resultCode == Activity.RESULT_OK){

            carregarPets();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pet_list_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemNovo:
                PetActivity.novo(this, REQUEST_NOVO_TIPO);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.select_item, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final Pets pet = (Pets) listViewPets.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menuAbrir:
                PetActivity.alterar(this,
                        REQUEST_ALTERAR_TIPO,
                        pet);
                return true;

            case R.id.menuApagar:
                verificarUsoPet(pet);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

}
