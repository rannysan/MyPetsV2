package com.example.mypetsv2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.mypetsv2.modelo.Events;
import com.example.mypetsv2.persistencia.EventsDatabase;
import com.example.mypetsv2.utils.UtilsGUI;

import java.util.List;

public class EventsActivity extends AppCompatActivity {
    private static final int REQUEST_NOVA_PESSOA    = 1;
    private static final int REQUEST_ALTERAR_PESSOA = 2;

    private ListView listViewEvent;
    private ArrayAdapter<Events> listaAdapter;
    private List<Events> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listViewEvent = findViewById(R.id.listViewItens);

        listViewEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Events event = (Events) parent.getItemAtPosition(position);

                EventActivity.alterar(EventsActivity.this,
                        REQUEST_ALTERAR_PESSOA,
                        event);
            }
        });

        carregaEvents();

        registerForContextMenu(listViewEvent);
    }

    private void carregaEvents(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                EventsDatabase database = EventsDatabase.getDatabase(EventsActivity.this);

                lista = database.eventDao().queryAll();

                EventsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listaAdapter = new ArrayAdapter<>(EventsActivity.this,
                                android.R.layout.simple_list_item_1,
                                lista);

                        listViewEvent.setAdapter(listaAdapter);
                    }
                });
            }
        });
    }

    private void excluirPessoa(final Events event){

        String mensagem = getString(R.string.confirmar_apagar)
                + "\n" + event.getNome();

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
                                                EventsDatabase.getDatabase(EventsActivity.this);

                                        database.eventDao().delete(event);

                                        EventsActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listaAdapter.remove(event);
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

        if ((requestCode == REQUEST_NOVA_PESSOA || requestCode == REQUEST_ALTERAR_PESSOA)
                && resultCode == Activity.RESULT_OK){

            carregaEvents();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_list_menu, menu);
        return true;
    }

    private void verificaTipos(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                EventsDatabase database = EventsDatabase.getDatabase(EventsActivity.this);

                int total = database.petDao().total();

                if (total == 0){

                    EventsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UtilsGUI.avisoErro(EventsActivity.this, R.string.nenhum_pet);
                        }
                    });

                    return;
                }

                EventActivity.nova(EventsActivity.this, REQUEST_NOVA_PESSOA);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuNovo:
                verificaTipos();
                return true;

            case R.id.menuPets:
                PetsActivity.abrir(this);
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

        Events event = (Events) listViewEvent.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menuAbrir:
                EventActivity.alterar(this,
                        REQUEST_ALTERAR_PESSOA,
                        event);
                return true;

            case R.id.menuApagar:
                excluirPessoa(event);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
