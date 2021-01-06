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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.almap.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import helper.ConfiguracaoFirebase;
import helper.UsuarioFirebase;
import model.HorarioAgendar;
import model.Posts;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editPostNome, editPostDescricao;
    private ImageView imagePostEmpresa;
    private static final int SELECAO_GALERIA= 200;
    private  String urlImagemSilicionada= "";
    private StorageReference storageReference;
    private String idUsuarioLogado;
    private Spinner opcHorario;
    private List<HorarioAgendar> agendars= new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        //configuraçoes iniciais
        inicializarComponentes();

        /*
        ArrayList<String> arrayHorario = new ArrayList<String>() ;
        arrayHorario.add("8:00");
        arrayHorario.add("9:00");
        arrayHorario.add("10:00");
        arrayHorario.add("11:00");
        arrayHorario.add("12:00");

        ArrayAdapter adapter= new ArrayAdapter(this,android.R.layout.simple_spinner_item,arrayHorario);
        opcHorario= (Spinner) findViewById(R.id.spinner);
        opcHorario.setAdapter(adapter);*/


        storageReference= ConfiguracaoFirebase.getFirebaseStorage();
        idUsuarioLogado= UsuarioFirebase.getIdUsuario();
        //configuração toolbar novo produto
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(" Add - Postagem");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //botão no activity novoproduto

        imagePostEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);

                }
            }
        });
                //=========colocar metodo

    }

    public void validarDadosPost(View view){
        //valida se os campos foram preenchidos
        String nomePost= editPostNome.getText().toString();
        String descricao= editPostDescricao.getText().toString();

        HorarioAgendar horarioAgendar= new HorarioAgendar();
        horarioAgendar.setHora("12:00");
        horarioAgendar.setData("12/09");
        agendars.add(horarioAgendar);



        if(!nomePost.isEmpty()){
            if(!descricao.isEmpty()){

                Posts posts= new Posts();
                posts.setIdUsuario(idUsuarioLogado);
                posts.setNomePost(nomePost);
                posts.setDescricao(descricao);
                posts.setUrlImagemPost(urlImagemSilicionada);
                posts.setHorarioAgendar(agendars);
                posts.salvar();//salvando dados

                Toast.makeText(NovoProdutoEmpresaActivity.this,
                        "Dados salvos com sucesso",
                        Toast.LENGTH_SHORT).show();
                finish();

            }else{
                exibirMensagem("Faça uma descrição");

            }

        }else{
            exibirMensagem("Digite um nome para seu post");

        }

    }

    private void exibirMensagem(String texto){
        Toast.makeText(this,texto, Toast.LENGTH_SHORT).show();
    }
    //----------------------------------------------------------------------------

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
                    imagePostEmpresa.setImageBitmap(imagem);
                    Posts idPost= new Posts();
                    ByteArrayOutputStream baos= new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem= baos.toByteArray();
                    StorageReference imagemRef= storageReference.child("imagens").child("postagens")
                            .child(idPost.getIdPost() + "jpeg");// utilizei o idPost.getIdPost para recuperar imagens do storage.
                    UploadTask uploadTask= imagemRef.putBytes(dadosImagem);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(NovoProdutoEmpresaActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            urlImagemSilicionada= taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(NovoProdutoEmpresaActivity.this,
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


    public void inicializarComponentes(){
        editPostNome= findViewById(R.id.editPostNome);
        editPostDescricao= findViewById(R.id.editPostDescricao);
        imagePostEmpresa= findViewById(R.id.imagePostEmpresa);
    }

}
