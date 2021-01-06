package activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.almap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import adapter.AdapterProduto;
import helper.ConfiguracaoFirebase;
import helper.UsuarioFirebase;
import listener.RecyclerItemClickListener;
import model.Empresa;
import model.HorarioAgendar;
import model.Posts;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclePosts;
    private AdapterProduto adapterProduto;
    private List<Posts> posts= new ArrayList<>();
    private List<HorarioAgendar> agendamento= new ArrayList<>();
    private DatabaseReference firebaseRef,firebaseRef2;
    private String idUsuarioLogado;
    private TextView textViewNomeEmpresaEmpresa;
    private  String urlImagemSilicionada= "",urlImagemSilicionadaPost= "";
    private ImageView imagePerfilEmpresa, imagePerfilEmpresaPost ;




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        inicializarComponentes();

        autenticacao= ConfiguracaoFirebase.getReferenciaAutenticacao();//inicializando autenticação
        firebaseRef= ConfiguracaoFirebase.getFirebase();//recuperar dados do firebase
        firebaseRef2= ConfiguracaoFirebase.getFirebase();//recupera imagem;
        idUsuarioLogado= UsuarioFirebase.getIdUsuario();
        //configuração toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Almap - Missão");
        setSupportActionBar(toolbar);

        //configurando recyclerview

        recyclePosts.setLayoutManager(new LinearLayoutManager(this));
        recyclePosts.setHasFixedSize(true);
        adapterProduto= new AdapterProduto(posts, this);
        recyclePosts.setAdapter(adapterProduto);
        EmpresaInicioLayout();

        //recupera posts para empresa
        recuperarProdutos();

        //adiciona evento de clique no recycleview
        recyclePosts.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this, recyclePosts,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                Posts postSelecionado= posts.get(position);
                                postSelecionado.remover();

                                Toast.makeText(EmpresaActivity.this,
                                        "Sucesso ao excluir post",
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }

    private void EmpresaInicioLayout(){//esse método recupera o nome da em presa e joga para o textview inicial

//=================================================================================================
        DatabaseReference postsRef= firebaseRef
                .child("empresas")
                .child(idUsuarioLogado);
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null) {
                    Empresa empresa = dataSnapshot.getValue(Empresa.class);

                    textViewNomeEmpresaEmpresa.setText(empresa.getNome());

                    urlImagemSilicionada = empresa.getUrlImagem();

                    if(urlImagemSilicionada != null){

                        Picasso.get()
                                .load(urlImagemSilicionada)
                                .into(imagePerfilEmpresa);

                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

//===================aqui tinha o metodo recuperar imagens do post;

    private void recuperarProdutos(){

        DatabaseReference postsRef= firebaseRef
                .child("postagens")
                .child(idUsuarioLogado);
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    posts.add(ds.getValue(Posts.class));
                }
                adapterProduto.notifyDataSetChanged();
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

    @SuppressLint("WrongViewCast")
    private void inicializarComponentes(){

        recyclePosts= findViewById(R.id.recyclePosts);
        textViewNomeEmpresaEmpresa= findViewById(R.id.textViewNomeEmpresaEmpresa);
        imagePerfilEmpresa= findViewById(R.id.imagePostEmpresa3);
        imagePerfilEmpresaPost= findViewById(R.id.imagePostEmpresa2);

    }

    @Override// metodo subescrito que retorna o menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_empresa,menu);
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
            case R.id.menuNovoProduto:
                //testando....código
                //preciso desses alert para fazer agendamento
                final  AlertDialog.Builder builderCalendario= new AlertDialog.Builder(this);//primeiro alert                                                                                              //firebaseRef2
                final  AlertDialog.Builder builder= new AlertDialog.Builder(this);//segundo alert
                View viewAgendarVisita= getLayoutInflater().inflate(R.layout.mensagem_para_empresa_post1,null);
                final RadioButton radioButtonSim=(RadioButton) viewAgendarVisita.findViewById(R.id.radioButtonOpcEmpresaSim);
                final RadioButton radioButtonNiao=(RadioButton) viewAgendarVisita.findViewById(R.id.radioButtonOpcEmpresaNao);
                //primeira msg escolha radiobutton sim ou não
                //segunda msg com o calendario
                final View viewAgendarCalendario= getLayoutInflater().inflate(R.layout.mensagem_para_empresa_post2calen,null);

                //------------------------------------------------------------------------------
                builder.setView(viewAgendarVisita);
                builder.setPositiveButton("Seguir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (radioButtonSim.isChecked()){

                            builderCalendario.setView(viewAgendarCalendario);

                            builderCalendario.setPositiveButton("Seguir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,   int which) {
                                    abrirNovoProduto();
                                }
                            });

                                Dialog dialogCalendario= builderCalendario.create();
                                dialogCalendario.show();

                            //======================================================================
                            builderCalendario.setNegativeButton("Canselar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                }
                            });
                        }



                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                Dialog dialog= builder.create();
                dialog.show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(EmpresaActivity.this, ConfiguracoesEmpresaActivity.class));
    }

    private void abrirNovoProduto(){
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }


    //===========get e set


    public ImageView getImagePerfilEmpresaPost() {
        return imagePerfilEmpresaPost;
    }

    public String getUrlImagemSilicionadaPost() {
        return urlImagemSilicionadaPost;
    }

    public DatabaseReference getFirebaseRef2() {
        return firebaseRef2;
    }

    public String getIdUsuarioLogado() {
        return idUsuarioLogado;
    }
}
