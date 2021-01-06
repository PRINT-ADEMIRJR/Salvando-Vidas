package activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.almap.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import helper.ConfiguracaoFirebase;
import helper.UsuarioFirebase;
import model.Usuario;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {

    private EditText editPerfilUsuarioNome, editPerfilUsuarioSobreNome,editPerfilUsuarioEstado,editPerfilUsuarioCidade,
            editPerfilUsuarioBairro,editPerfilUsuarioRua,editPerfilUsuarioNumero;
    private ImageView imagePerfilEmpresa;
    private String usuarioLogado;
    private String urlImagemSilicionada;
    private static final int SELECAO_GALERIA= 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);

        //inicializando componentes
        inicializarComponentes();
        storageReference= ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef= ConfiguracaoFirebase.getFirebase();
        usuarioLogado= UsuarioFirebase.getIdUsuario();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações - Usuário");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //botão no activity config. usuario

        //evento ao clicar na imagem
        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);

                }
                Toast.makeText(ConfiguracoesUsuarioActivity.this,
                        " Após escolher a imagem aguarde a mensagem: "+"Sucesso ao fazer upload da imagem" ,
                        Toast.LENGTH_SHORT).show();
            }
        });

        //recupera Dados Da Empresa
        recuperarDadosDaEmpresa();

    }


    public void recuperarDadosDaEmpresa() {
        DatabaseReference empresaRef = firebaseRef.child("usuarios")
                .child(usuarioLogado);

        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null){
                    Usuario usuario= dataSnapshot.getValue(Usuario.class);
                    editPerfilUsuarioNome.setText(usuario.getNome());
                    editPerfilUsuarioSobreNome.setText(usuario.getSobrenome());
                    editPerfilUsuarioEstado.setText(usuario.getEstado());
                    editPerfilUsuarioCidade.setText(usuario.getCidade());
                    editPerfilUsuarioBairro.setText(usuario.getBairro());
                    editPerfilUsuarioRua.setText(usuario.getRua());
                    editPerfilUsuarioNumero.setText(usuario.getNumero());

                    urlImagemSilicionada = usuario.getUrlImagem();
                    if(urlImagemSilicionada != ""){
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

            //==================================================================================================
    public void validarDadosPerfilUsuario(View view){

        //valida se os campos foram preenchidos
        String nome = editPerfilUsuarioNome.getText().toString();
        String sobrenome = editPerfilUsuarioSobreNome.getText().toString();
        String estado = editPerfilUsuarioEstado.getText().toString();
        String cidade = editPerfilUsuarioCidade.getText().toString();
        String bairro = editPerfilUsuarioBairro.getText().toString();
        String rua = editPerfilUsuarioRua.getText().toString();
        String numero = editPerfilUsuarioNumero.getText().toString();

        if (!nome.isEmpty()) {
            if (!sobrenome.isEmpty()) {
                if (!estado.isEmpty()) {
                    if (!cidade.isEmpty()) {
                        if (!bairro.isEmpty()) {
                            if (!rua.isEmpty()) {
                                if (!numero.isEmpty()) {

                                    Usuario usuario = new Usuario();
                                    usuario.setIdUsuario(usuarioLogado);
                                    usuario.setNome(nome);
                                    usuario.setSobrenome(sobrenome);
                                    usuario.setEstado(estado);
                                    usuario.setCidade(cidade);
                                    usuario.setBairro(bairro);
                                    usuario.setRua(rua);
                                    usuario.setNumero(numero);
                                    usuario.setUrlImagem(urlImagemSilicionada);
                                    //usuario.getNomeFiltro();
                                    usuario.salvar();//salvando dados

                                    Toast.makeText(ConfiguracoesUsuarioActivity.this,
                                            "Dados salvos com sucesso",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    exibirMensagem("Digite o número da sua residência ");

                                }

                            } else {
                                exibirMensagem("Digite o nome da rua");

                            }

                        } else {
                            exibirMensagem("Digite o nome do bairro");

                        }

                    } else {
                        exibirMensagem("Digite a cidade onde você mora");

                    }

                } else {
                    exibirMensagem("Digite o estado onde você mora");

                }


            } else {
                exibirMensagem("Digite seu sobrenome");
            }
        } else {
            exibirMensagem("Digite seu nome");
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bitmap imagem= null;

            try {
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagem= data.getData();
                        imagem= MediaStore.Images.Media.getBitmap(
                                getContentResolver(), localImagem
                        );
                        break;
                }
                if(imagem != null){// caso imagem for diferente de null
                    imagePerfilEmpresa.setImageBitmap(imagem);

                    ByteArrayOutputStream baos= new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem= baos.toByteArray();
                    StorageReference imagemRef= storageReference.child("imagens").child("usuarios")
                            .child(usuarioLogado + "jpeg");
                    UploadTask uploadTask= imagemRef.putBytes(dadosImagem);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(ConfiguracoesUsuarioActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            urlImagemSilicionada= taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(ConfiguracoesUsuarioActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //=================================================================
    private void exibirMensagem(String texto){
        Toast.makeText(this,texto, Toast.LENGTH_SHORT).show();
    }
    //==================================================================
    private void inicializarComponentes(){

        editPerfilUsuarioNome= findViewById(R.id.editPerfilUsuarioNome);
        editPerfilUsuarioSobreNome= findViewById(R.id.editPerfilUsuarioSobreNome);
        editPerfilUsuarioEstado= findViewById(R.id.editPerfilUsuarioEstado);
        editPerfilUsuarioCidade= findViewById(R.id.editPerfilUsuarioCidade);
        editPerfilUsuarioBairro= findViewById(R.id.editPerfilUsuarioBairro);
        editPerfilUsuarioRua= findViewById(R.id.editPerfilUsuarioRua);
        editPerfilUsuarioNumero= findViewById(R.id.editPerfilUsuarioNumero);
        imagePerfilEmpresa= findViewById(R.id.imagePerfilUsuario);
    }

}
