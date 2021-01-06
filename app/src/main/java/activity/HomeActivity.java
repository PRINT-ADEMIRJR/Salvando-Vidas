package activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.almap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import adapter.AdapterEmpresa;
import helper.ConfiguracaoFirebase;
import listener.RecyclerItemClickListener;
import model.Empresa;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    MaterialSearchView searchView;
    private RecyclerView recycleEmpresas;
    private AdapterEmpresa adapterEmpresas;
    private List<Empresa> empresas= new ArrayList<>();
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        inicializarComponentes();
        firebaseRef= ConfiguracaoFirebase.getFirebase();
        autenticacao= ConfiguracaoFirebase.getReferenciaAutenticacao();

        //configuração toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Almap");
        setSupportActionBar(toolbar);

        //configurando recyclerview

        recycleEmpresas.setLayoutManager(new LinearLayoutManager(this));
        recycleEmpresas.setHasFixedSize(true);
        adapterEmpresas= new AdapterEmpresa(empresas);
        recycleEmpresas.setAdapter(adapterEmpresas);

        //recupera empresas para usuario
        recuperarEmpresas();

        //configuração do searchview
        searchView.setHint("Pesquisar igrejas");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //esse método é recomendado
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarEmpresas(newText);
                return true;
            }
        });

        //configurar evento de clique quando eu clicar na empresa vai mudar para a activity empresaspostagensactinity
        recycleEmpresas.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recycleEmpresas,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Empresa empresaSelecionada= empresas.get(position);
                                Intent i= new Intent(HomeActivity.this, EmpresasPostagensActivity.class);
                                i.putExtra("empresa", empresaSelecionada);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }
    private void pesquisarEmpresas(String pesquisa){

        //fazer manutenção para aparecer somente visitantes do estado ou cidade .....

        DatabaseReference empresaRef= firebaseRef.child("empresas");
        Query query= empresaRef.orderByChild("nomeFiltro")
                .startAt(pesquisa)
                .endAt(pesquisa + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                empresas.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    empresas.add(ds.getValue(Empresa.class));
                }

                adapterEmpresas.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void recuperarEmpresas(){// método recupera empresas
        DatabaseReference empresaRef= firebaseRef.child("empresas");
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                empresas.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    empresas.add(ds.getValue(Empresa.class));
                }
                adapterEmpresas.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();

        }

    }

    @Override// metodo subescrito que retorna o menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_usuario,menu);

        MenuItem item= menu.findItem(R.id.menuNovoPesquisa);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                deslogarUsuario();
                finish();
                break;
            case R.id.menuConfiguracoes:
                abrirConfiguracoes();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void inicializarComponentes(){
        searchView= findViewById(R.id.materialSearchView);
        recycleEmpresas= findViewById(R.id.recycleEmpresas);
    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(HomeActivity.this, ConfiguracoesUsuarioActivity.class));
    }

}
