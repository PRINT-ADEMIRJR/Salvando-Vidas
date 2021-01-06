package model;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import helper.ConfiguracaoFirebase;

public class Posts {

    private String idUsuario;
    private  String idPost;
    private String nomePost;
    private String descricao;
    private String urlImagemPost;
    private List<HorarioAgendar> horarioAgendar;

    public Posts() {
        DatabaseReference fireBaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference postRef = fireBaseRef
                .child("postagens");
        setIdPost(postRef.push().getKey());
    }

    public void salvar() {

        if(getUrlImagemPost()!="") {
            DatabaseReference fireBaseRef = ConfiguracaoFirebase.getFirebase();
            DatabaseReference postRef = fireBaseRef
                    .child("postagens")
                    .child(getIdUsuario())
                    .child(getIdPost());
            postRef.setValue(this);

        }else{
            urlImagemPost= "https://firebasestorage.googleapis.com/v0/b/almap-de0d0.appspot.com/o/imagens%2Fpostagens%2Fpadrao.jpg?alt=media&token=7f0caf92-04e1-41a1-8360-3a8b692831cd";
            DatabaseReference fireBaseRef = ConfiguracaoFirebase.getFirebase();
            DatabaseReference postRef = fireBaseRef
                    .child("postagens")
                    .child(getIdUsuario())
                    .child(getIdPost());
            postRef.setValue(this);
        }

    }
    public void remover(){
        DatabaseReference fireBaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference postRef = fireBaseRef
                .child("postagens")
                .child(getIdUsuario())
                .child(getIdPost());

        postRef.removeValue();

    }

    public List<HorarioAgendar> getHorarioAgendar() {
        return horarioAgendar;
    }

    public void setHorarioAgendar(List<HorarioAgendar> horarioAgendar) {
        this.horarioAgendar = horarioAgendar;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomePost() {
        return nomePost;
    }

    public void setNomePost(String nomePost) {
        this.nomePost = nomePost;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUrlImagemPost() {
        return urlImagemPost;
    }

    public void setUrlImagemPost(String urlImagemPost) {
        this.urlImagemPost = urlImagemPost;
    }
}
