package activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.almap.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import adapter.AdapterProduto;
import dmax.dialog.SpotsDialog;
import helper.ConfiguracaoFirebase;
import helper.UsuarioFirebase;
import listener.RecyclerItemClickListener;
import model.Empresa;
import model.IndicacaoVisitaDoUsuario;
import model.ItensPedido;
import model.Pedido;
import model.Posts;
import model.Usuario;

public class EmpresasPostagensActivity extends AppCompatActivity {

    private RecyclerView recyclerPostagensEmpresas;
    private ImageView imagePostagensEmpresas;
    private TextView textViewPostagensEmpresas;
    private Empresa empresaSelecionada;

    private AdapterProduto adapterProduto;
    private List<Posts> posts= new ArrayList<>();
    private List<ItensPedido> itensPedidoPosts= new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idEmpresa;
    private  String idUsuarioLogado;
    private Usuario usuario;
    private AlertDialog dialog, dialog2,dialog3;
    private Pedido pedidoRecuperado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresas_postagens);
        //configurações iniciais
        inicializarComponentes();
        firebaseRef= ConfiguracaoFirebase.getFirebase();//recuperar dados do firebase
        idUsuarioLogado= UsuarioFirebase.getIdUsuario();

        //recuperar empresa selecionada
        Bundle bundle= getIntent().getExtras();
        if (bundle != null){
            empresaSelecionada= (Empresa) bundle.getSerializable("empresa");
            textViewPostagensEmpresas.setText(empresaSelecionada.getNome());
            idEmpresa= empresaSelecionada.getIdUsuario();
            String url= empresaSelecionada.getUrlImagem();
            Picasso.get().load(url).into(imagePostagensEmpresas);

        }


        //configuração toolbar postagens das empresas
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Postagens");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //botão no activity novoproduto

        //configurando recyclerview
        recyclerPostagensEmpresas.setLayoutManager(new LinearLayoutManager(this));
        recyclerPostagensEmpresas.setHasFixedSize(true);
        adapterProduto= new AdapterProduto(posts, this);
        recyclerPostagensEmpresas.setAdapter(adapterProduto);

        //CONFIGURANDO RECYCLEVIEW
        recyclerPostagensEmpresas.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerPostagensEmpresas,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                clicarRequisitar(position);
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

        //recupera posts da empresa para usuario
        recuperarProdutos();
        recuperarDadosDoUsuario();

    }

    private void clicarRequisitar(final int position) {
        final  AlertDialog.Builder builder= new AlertDialog.Builder(this);//primeiro alert
        final AlertDialog.Builder builder2= new AlertDialog.Builder(this);//segundo alert opção
        final AlertDialog.Builder builder3= new AlertDialog.Builder(this);//terceiro alert erro no preenchimento
        builder.setMessage("Esta requisição é para você?");//pergunta do primeiro alert
        //adicionando layout.xml no alert builder
        View view= getLayoutInflater().inflate(R.layout.mensagem_visita_e_pra_mim,null);
        //armazenando em variaveis para manipular o radiobutton da minha alert builder
        final RadioButton radioButtonSim=(RadioButton) view.findViewById(R.id.radioButtonSim);
        final RadioButton radioButtonNiao=(RadioButton) view.findViewById(R.id.radioButton2Nao);

        builder.setPositiveButton("Seguir", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (radioButtonSim.isChecked()){//se a visita for pra mim faça.....

                    //atenção pois esse código é importante
//==================================================================================================
                    builder2.setMessage("Digite mais algumas informações");
                    dialog2= builder2.create();
                    dialog2.show();
                    //---------------------------------------------------------------------------
                    Posts postagens= posts.get(position);//lista que recupera a posição dos posts
                    ItensPedido itensPedido= new ItensPedido();
                    itensPedido.setIdPedido(postagens.getIdPost());
                    itensPedido.setDescricao(postagens.getDescricao());
                    itensPedido.setNomePedido(postagens.getNomePost());
                    //itensPedido.setIdPedido(postagens.getIdPost());
                    itensPedidoPosts.add(itensPedido);//recupera onde foi clicado e armazena no array

                    if (pedidoRecuperado==null){
                        pedidoRecuperado= new Pedido(idUsuarioLogado,idEmpresa);
                    }
                    pedidoRecuperado.setNome(usuario.getNome());
                    pedidoRecuperado.setSobrenome(usuario.getSobrenome());
                    pedidoRecuperado.setEstado(usuario.getEstado());
                    pedidoRecuperado.setCidade(usuario.getCidade());
                    pedidoRecuperado.setBairro(usuario.getBairro());
                    pedidoRecuperado.setRua(usuario.getRua());
                    pedidoRecuperado.setNumeroCasa(usuario.getNumero());
                    pedidoRecuperado.setNumeroTelefone(usuario.getNumeroTelefone());
                    pedidoRecuperado.setObservacao(usuario.getObservacao());
                    pedidoRecuperado.setItens(itensPedidoPosts);
                    pedidoRecuperado.salvar();

//====================================================================================================


                }else if (radioButtonNiao.isChecked()){//se a visita nã for pra mim faça....
                    builder2.setMessage("Digite algumas informações sobre a pessoa que ira receber a visita");
                    View view1Cadastro= getLayoutInflater().inflate(R.layout.cadastro_outra_pessoa,null);

                    //armazenando dados de cadastro de usuarios nas variaveis
                    final EditText editPerfilOutroUsuarioNome=(EditText) view1Cadastro
                            .findViewById(R.id.editPerfilOutroUsuarioNome);
                    final EditText editPerfilOutroUsuarioEstado=(EditText) view1Cadastro
                            .findViewById(R.id.editPerfilOutroUsuarioEstado);
                    final EditText editPerfilOutroUsuarioCidade=(EditText) view1Cadastro
                            .findViewById(R.id.editPerfilOutroUsuarioCidade);
                    final EditText editPerfilOutroUsuarioBairro=(EditText) view1Cadastro
                            .findViewById(R.id.editPerfilOutroUsuarioBairro);
                    final EditText editPerfilOutroUsuarioRua=(EditText) view1Cadastro
                            .findViewById(R.id.editPerfilOutroUsuarioRua);
                    final EditText editPerfilOutroUsuarioNumero=(EditText) view1Cadastro
                            .findViewById(R.id.editPerfilOutroUsuarioNumero);
                    final EditText editPerfilOutroUsuarioNumeroTelefone=(EditText) view1Cadastro
                            .findViewById(R.id.editPerfilOutroUsuarioNumeroTelefone);
                    final EditText editPerfilOutroUsuarioObservacao=(EditText) view1Cadastro
                            .findViewById(R.id.editPerfilOutroUsuarioObservacao);

                        builder2.setPositiveButton("Seguir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //vou escrever aqui a logica para  recuperar e salvar no banco de dados
                                String nome = editPerfilOutroUsuarioNome.getText().toString();
                                String estado = editPerfilOutroUsuarioEstado.getText().toString();
                                String cidade = editPerfilOutroUsuarioCidade.getText().toString();
                                String bairro = editPerfilOutroUsuarioBairro.getText().toString();
                                String rua = editPerfilOutroUsuarioRua.getText().toString();
                                String numero = editPerfilOutroUsuarioNumero.getText().toString();
                                String numeroTelefone = editPerfilOutroUsuarioNumeroTelefone.getText().toString();
                                String observacao = editPerfilOutroUsuarioObservacao.getText().toString();

                                if (!nome.isEmpty()) {
                                    if (!estado.isEmpty()) {
                                        if (!cidade.isEmpty()) {
                                            if (!bairro.isEmpty()) {
                                                if (!rua.isEmpty()) {
                                                    if (!numero.isEmpty()) {
                                                        if (!numeroTelefone.isEmpty()) {

                                                            IndicacaoVisitaDoUsuario usuario = new IndicacaoVisitaDoUsuario();
                                                            usuario.setIdUsuario(idUsuarioLogado);
                                                            usuario.setNome(nome);
                                                            usuario.setEstado(estado);
                                                            usuario.setCidade(cidade);
                                                            usuario.setBairro(bairro);
                                                            usuario.setRua(rua);
                                                            usuario.setNumero(numero);
                                                            usuario.setNumeroTelefone(numeroTelefone);
                                                            usuario.setObservacao(observacao);
                                                            //usuario.getNomeFiltro();
                                                            usuario.salvar();//salvando dados

                                                            Toast.makeText(EmpresasPostagensActivity.this,
                                                                    "Dados salvos com sucesso",
                                                                    Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            exibirMensagem("Digite o número do telefone");
                                                            View viewErro= getLayoutInflater().inflate(R.layout.mensagem_cadastro_outro_usu,null);
                                                            builder3.setPositiveButton("Voltar", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog2.show();
                                                                }
                                                            });
                                                            builder3.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    finish();
                                                                }
                                                            });
                                                            builder3.setView(viewErro);
                                                            dialog3= builder3.create();
                                                            dialog3.show();
                                                        }

                                                    } else {
                                                        exibirMensagem("Digite o número da residência");
                                                        View viewErro= getLayoutInflater().inflate(R.layout.mensagem_cadastro_outro_usu,null);
                                                        builder3.setPositiveButton("Voltar", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog2.show();
                                                            }
                                                        });
                                                        builder3.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                finish();
                                                            }
                                                        });
                                                        builder3.setView(viewErro);
                                                        dialog3= builder3.create();
                                                        dialog3.show();

                                                    }

                                                } else {
                                                    exibirMensagem("Digite a rua");
                                                    View viewErro= getLayoutInflater().inflate(R.layout.mensagem_cadastro_outro_usu,null);
                                                    builder3.setPositiveButton("Voltar", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog2.show();
                                                        }
                                                    });
                                                    builder3.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            finish();
                                                        }
                                                    });
                                                    builder3.setView(viewErro);
                                                    dialog3= builder3.create();
                                                    dialog3.show();



                                                }

                                            } else {
                                                exibirMensagem("Digite o bairro");
                                                View viewErro= getLayoutInflater().inflate(R.layout.mensagem_cadastro_outro_usu,null);
                                                builder3.setPositiveButton("Voltar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog2.show();
                                                    }
                                                });
                                                builder3.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }
                                                });
                                                builder3.setView(viewErro);
                                                dialog3= builder3.create();
                                                dialog3.show();


                                            }

                                        } else {
                                            exibirMensagem("Digite a cidade");
                                            View viewErro= getLayoutInflater().inflate(R.layout.mensagem_cadastro_outro_usu,null);
                                            builder3.setPositiveButton("Voltar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog2.show();
                                                }
                                            });
                                            builder3.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            });
                                            builder3.setView(viewErro);
                                            dialog3= builder3.create();
                                            dialog3.show();


                                        }


                                    } else {
                                        exibirMensagem("Digite o estado");
                                        View viewErro= getLayoutInflater().inflate(R.layout.mensagem_cadastro_outro_usu,null);
                                        builder3.setPositiveButton("Voltar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog2.show();
                                            }
                                        });
                                        builder3.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        });
                                        builder3.setView(viewErro);
                                        dialog3= builder3.create();
                                        dialog3.show();


                                    }
                                } else {
                                    exibirMensagem("Digite seu nome completo");
                                    View viewErro= getLayoutInflater().inflate(R.layout.mensagem_cadastro_outro_usu,null);
                                    builder3.setPositiveButton("Voltar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog2.show();
                                        }
                                    });
                                    builder3.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                                    builder3.setView(viewErro);
                                    dialog3= builder3.create();
                                    dialog3.show();

                                }

                            }

                        });
                    builder2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });



                    builder2.setView(view1Cadastro);
                    dialog2= builder2.create();
                    dialog2.show();

                }


            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setView(view);
        AlertDialog dialog= builder.create();
        dialog.show();




    }

    private void recuperarDadosDoUsuario(){

        dialog= new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference usuarioRef= firebaseRef
                .child("usuarios")
                .child(idUsuarioLogado);
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!= null){
                    usuario= dataSnapshot.getValue(Usuario.class);
                }

                recuperarPedido();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void recuperarPedido() {
        dialog.dismiss();
    }

    private void recuperarProdutos(){

        DatabaseReference postsRef= firebaseRef
                .child("postagens")
                .child(idEmpresa);
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

    @Override// metodo subescrito que retorna o menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_requisicao_do_usuario,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuRequisicao:

                finish();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exibirMensagem(String texto){
        Toast.makeText(this,texto, Toast.LENGTH_SHORT).show();
    }

    public void inicializarComponentes(){
        recyclerPostagensEmpresas= findViewById(R.id.recyclerViewEmpresaPost);
        imagePostagensEmpresas= findViewById(R.id.imageEmpresaPost);
        textViewPostagensEmpresas= findViewById(R.id.textNomeEmpresapost);

    }

}
