package com.example.mypetsv2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.mypetsv2.modelo.Events;
import com.example.mypetsv2.modelo.Pets;
import com.example.mypetsv2.persistencia.EventsDatabase;
import com.example.mypetsv2.utils.UtilsGUI;

import java.util.List;

public class EventActivity extends AppCompatActivity {

    public static final String MODO    = "MODO";
    public static final String ID      = "ID";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;

    private EditText editTextNome;
    private EditText editTextData;

    private Spinner spinnerPet;
    private List<Pets> listaPets;

    private int    modo;
    private Events event;

    public static void nova(Activity activity, int requestCode){

        Intent intent = new Intent(activity, EventActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterar(Activity activity, int requestCode, Events event){

        Intent intent = new Intent(activity, EventActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, event.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextNome  = findViewById(R.id.editTextNome);
        editTextData = findViewById(R.id.editTextData);
        spinnerPet   = findViewById(R.id.spinnerPet);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        modo = bundle.getInt(MODO, NOVO);

        carregaTipos();

        if (modo == ALTERAR){

            setTitle(R.string.alterar_evento);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    int id = bundle.getInt(ID);

                    EventsDatabase database = EventsDatabase.getDatabase(EventActivity.this);

                    event = database.eventDao().queryForId(id);

                    EventActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editTextNome.setText(event.getNome());
                            editTextData.setText(String.valueOf(event.getDate()));

                            int posicao = posicaoTipo(event.getPetId());
                            spinnerPet.setSelection(posicao);
                        }
                    });
                }
            });

        }else{

            setTitle(R.string.novo_evento);

            event = new Events("");
        }
    }

    private int posicaoTipo(int tipoId){

        for (int pos = 0; pos < listaPets.size(); pos++){

            Pets t = listaPets.get(pos);

            if (t.getId() == tipoId){
                return pos;
            }
        }

        return -1;
    }

    private void carregaTipos(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                EventsDatabase database = EventsDatabase.getDatabase(EventActivity.this);

                listaPets = database.petDao().queryAll();

                EventActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<Pets> spinnerAdapter =
                                new ArrayAdapter<>(EventActivity.this,
                                        android.R.layout.simple_list_item_1,
                                        listaPets);

                        spinnerPet.setAdapter(spinnerAdapter);
                    }
                });
            }
        });
    }

    private void salvar(){

        String nome  = UtilsGUI.validaCampoTexto(this,
                editTextNome,
                R.string.nome_vazio);
        if (nome == null){
            return;
        }

        String txtData = UtilsGUI.validaCampoTexto(this,
                editTextData,
                R.string.data_vazia);
        if (txtData == null){
            return;
        }

        event.setNome(nome);
        event.setDate(txtData);

        Pets pet = (Pets) spinnerPet.getSelectedItem();
        if (pet != null){
            event.setPetId(pet.getId());
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                EventsDatabase database = EventsDatabase.getDatabase(EventActivity.this);

                if (modo == NOVO) {

                    database.eventDao().insert(event);

                } else {

                    database.eventDao().update(event);
                }

                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    private void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edition_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuSalvar:
                salvar();
                return true;
            case R.id.menuCancelar:
                cancelar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
