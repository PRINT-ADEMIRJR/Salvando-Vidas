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
import model.Empresa;

public class ConfiguracoesEmpresaActivity extends AppCompatActivity {
    private EditText editEmpresaNome, editEmpresaResponsavel,editEmpresaEstado,editEmpresaCidade,
    editEmpresaBRN;
    private ImageView imagePerfilEmpresa;
    private static final int SELECAO_GALERIA= 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String usuarioLogado;
    private  String urlImagemSilicionada= "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_empresa);

        //configuraçoes iniciais
        inicializarComponentes();
        storageReference= ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef= ConfiguracaoFirebase.getFirebase();
        usuarioLogado= UsuarioFirebase.getIdUsuario();

        //configuração toolbar novo produto
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //botão no activity novoproduto

        //evento ao clicar na imagem
        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
                Toast.makeText(ConfiguracoesEmpresaActivity.this,
                        " Após escolher a imagem aguarde a mensagem: "+"Sucesso ao fazer upload da imagem" ,
                        Toast.LENGTH_SHORT).show();
            }
        });
        //recupera Dados Da Empresa

        recuperarDadosDaEmpresa();

    }


    public EditText getEditEmpresaNome() {
        return editEmpresaNome;
    }

    public void recuperarDadosDaEmpresa() {
        DatabaseReference empresaRef = firebaseRef.child("empresas")
                .child(usuarioLogado);

        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null){
                    Empresa empresa= dataSnapshot.getValue(Empresa.class);
                    editEmpresaNome.setText(empresa.getNome());
                    editEmpresaResponsavel.setText(empresa.getResponsavel());
                    editEmpresaEstado.setText(empresa.getEstado());
                    editEmpresaCidade.setText(empresa.getCidade());
                    editEmpresaBRN.setText(empresa.getbRN());

                    urlImagemSilicionada = empresa.getUrlImagem();
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



    public void validarDadosEmpresa(View view){
        //valida se os campos foram preenchidos
        String nome= editEmpresaNome.getText().toString();
        String responsavel= editEmpresaResponsavel.getText().toString();
        String estado= editEmpresaEstado.getText().toString();
        String cidade= editEmpresaCidade.getText().toString();
        String BRN= editEmpresaBRN.getText().toString();

        if(!nome.isEmpty()){
            if(!responsavel.isEmpty()){
                if(!estado.isEmpty()){
                    if(!cidade.isEmpty()){
                        if(!BRN.isEmpty()){
                            Empresa empresa= new Empresa();
                            empresa.setIdUsuario(usuarioLogado);
                            empresa.setNome(nome);
                            empresa.setResponsavel(responsavel);
                            empresa.setEstado(estado);
                            empresa.setCidade(cidade);
                            empresa.setbRN(BRN);
                            empresa.setUrlImagem(urlImagemSilicionada);
                            empresa.salvar();//salvando dados

                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                    "Dados salvos com sucesso",
                                    Toast.LENGTH_SHORT).show();
                                finish();
                        }else{
                            exibirMensagem("Digite o bairro, rua e número ");

                        }

                    }else{
                        exibirMensagem("Digite a cidade");

                    }

                }else{
                    exibirMensagem("Digite o estado");

                }

            }else{
                exibirMensagem("Digite o nome do responsável");

            }

        }else{
            exibirMensagem("Digite o nome da denominação, ONG etc...");

        }

    }

    private void exibirMensagem(String texto){
        Toast.makeText(this,texto, Toast.LENGTH_SHORT).show();
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
                    StorageReference imagemRef= storageReference.child("imagens").child("empresas")
                            .child(usuarioLogado + "jpeg");
                    UploadTask uploadTask= imagemRef.putBytes(dadosImagem);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            urlImagemSilicionada= taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
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
        editEmpresaNome= findViewById(R.id.editEmpresaNome);
        editEmpresaResponsavel= findViewById(R.id.editEmpresaResponsavel);
        editEmpresaEstado= findViewById(R.id.editEmpresaEstado);
        editEmpresaCidade= findViewById(R.id.editEmpresaCidade);
        editEmpresaBRN= findViewById(R.id.editEmpresaBRN);
        imagePerfilEmpresa= findViewById(R.id.imagePerfilEmpresa);
    }
}
