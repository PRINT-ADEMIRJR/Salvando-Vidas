package activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.almap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import helper.ConfiguracaoFirebase;
import helper.UsuarioFirebase;

public class AutenticacaoActivity extends AppCompatActivity {

    private Button botaoAcesso;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso ,tipoUsuario;
    private LinearLayout linearLayoutIipoUsuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao2);
        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getReferenciaAutenticacao();

        verificaUsuarioLogado();

        //progamando switch tipo acesso
        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){//missão
                    linearLayoutIipoUsuario.setVisibility(View.VISIBLE);

                }else{//usuario
                    linearLayoutIipoUsuario.setVisibility(View.GONE);
                }
            }
        });

        //aou clicar no botao acessar do meu app
        botaoAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if (!senha.isEmpty()) { // se a aenha nao esta vazia
                    if (!email.isEmpty()) { // se o email nao esta vazio

                        // verificar o estado do switch
                        if (tipoAcesso.isChecked()) {// caso esteja ativado (no cadastrar)
                            autenticacao.createUserWithEmailAndPassword(email, senha).
                                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {//se ouver sucesso no logim redireciona tela principal
                                            //========
                                            Toast.makeText(AutenticacaoActivity.this,
                                                    "Cadastro realizado com sucesso!",
                                                    Toast.LENGTH_SHORT).show();
                                            String tipoUsuario= getTipoUsuario();

                                            UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);

                                            abrirTelaPrincipal(tipoUsuario);


                                        } else {// caso contrario tera erro

                                            String erroexcecao = "";
                                            try {
                                                throw task.getException();

                                            } catch (FirebaseAuthWeakPasswordException e) {
                                                erroexcecao = "Digite uma senha mais forte!";
                                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                                erroexcecao = "Por favor, digite um e-mail válido";
                                            } catch (FirebaseAuthUserCollisionException e) {
                                                erroexcecao = "Esta conta já foi cadastrada";
                                            } catch (Exception e) {
                                                erroexcecao = "ao cadastrar usuário: " + e.getMessage();
                                                e.printStackTrace();
                                            }
                                            Toast.makeText(AutenticacaoActivity.this,
                                                    "Erro: " + erroexcecao,
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                });

                    }else{ //login switch opção

                            autenticacao.signInWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){//se ja foi autenticado
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Logado com sucesso",
                                                Toast.LENGTH_SHORT).show();

                                        String tipoUsuario= task.getResult().getUser().getDisplayName();

                                        abrirTelaPrincipal(tipoUsuario);

                                    }else{//caso não foi autenticado
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Erro ao fazer login : " + task.getException(),
                                                Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });


                        }//final else login

                    }else {
                        Toast.makeText(AutenticacaoActivity.this,
                                "Preencha o E-mail!",
                                Toast.LENGTH_SHORT).show();

                    }

                }else {
                    Toast.makeText(AutenticacaoActivity.this,
                            "Preencha a Senha!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //switch de opção de cadastro usuario ou missão;
    private String getTipoUsuario(){
        return tipoUsuario.isChecked() ? "E" : "U";
    }

    private void verificaUsuarioLogado(){
        FirebaseUser usuarioAtual= autenticacao.getCurrentUser();
        if(usuarioAtual!= null){
           String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }

    }

    private void abrirTelaPrincipal(String tipoUsuario){ //depois de logar esse metodo vai abrir uma das telas

        if(tipoUsuario.equals("E")){//tela missao
            startActivity(new Intent(getApplicationContext(),EmpresaActivity.class));

        }else{//tela usuariom
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }
    }

    private void inicializarComponentes() {

        botaoAcesso = findViewById(R.id.buttonAcesso);
        campoEmail = findViewById(R.id.editEmpresaNome);
        campoSenha = findViewById(R.id.editEmpresaResponsavel);
        tipoAcesso = findViewById(R.id.switch1Acesso);
        tipoUsuario= findViewById(R.id.switch1TipoUsuario);
        linearLayoutIipoUsuario= findViewById(R.id.linearLayoutTipoUsuario);

    }

}
