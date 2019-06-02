package com.example.mypetsv2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.mypetsv2.modelo.Pets;
import com.example.mypetsv2.persistencia.EventsDatabase;
import com.example.mypetsv2.utils.UtilsGUI;

import java.util.List;

public class PetActivity extends AppCompatActivity {

    public static final String MODO    = "MODO";
    public static final String ID      = "ID";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;

    private EditText editTexName;
        private int  modo;
    private Pets pet;

    public static void novo(Activity activity, int requestCode) {

        Intent intent = new Intent(activity, PetActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterar(Activity activity, int requestCode, Pets pet){

        Intent intent = new Intent(activity, PetActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, pet.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTexName = findViewById(R.id.editTextName);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        if (bundle != null){
            modo = bundle.getInt(MODO, NOVO);
        }else{
            modo = NOVO;
        }

        if (modo == ALTERAR){

            setTitle(R.string.alterar_pet);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    int id = bundle.getInt(ID);

                    EventsDatabase database = EventsDatabase.getDatabase(PetActivity.this);

                    pet = database.petDao().queryForId(id);

                    PetActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editTexName.setText(pet.getName());
                        }
                    });
                }
            });

        }else{

            setTitle(R.string.novo_pet);

            pet = new Pets("");
        }
    }

    private void salvar(){

        final String name  = UtilsGUI.validaCampoTexto(this,
                editTexName,
                R.string.nome_vazio);
        if (name == null){
            return;
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                EventsDatabase database = EventsDatabase.getDatabase(PetActivity.this);

                List<Pets> lista = database.petDao().queryForName(name);

                if (modo == NOVO) {

                    if (lista.size() > 0){

                        PetActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UtilsGUI.avisoErro(PetActivity.this, R.string.name_usado);
                            }
                        });

                        return;
                    }

                    pet.setName(name);

                    database.petDao().insert(pet);

                } else {

                    if (!name.equals(pet.getName())){

                        if (lista.size() >= 1){

                            PetActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UtilsGUI.avisoErro(PetActivity.this, R.string.name_usado);
                                }
                            });

                            return;
                        }

                        pet.setName(name);

                        database.petDao().update(pet);
                    }
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
