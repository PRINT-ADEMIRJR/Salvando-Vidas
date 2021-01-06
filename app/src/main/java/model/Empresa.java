package model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import helper.ConfiguracaoFirebase;

public class Empresa implements Serializable {
    //essa classe ira fazer a configuração para salvar no banco

    private String idUsuario;
    private String urlImagem;
    private String nome;
    private String nomeFiltro;
    private String responsavel;
    private String estado;
    private String cidade;
    private String bRN;

    public Empresa() {
    }

    public void salvar() {//metodo para salvar no firebase

        if(getUrlImagem()!="") {
            DatabaseReference firebaseref = ConfiguracaoFirebase.getFirebase();
            DatabaseReference empresaRef = firebaseref.child("empresas").child(getIdUsuario());
            empresaRef.setValue(this);
            getNomeFiltro();
        }else{
            urlImagem= "https://firebasestorage.googleapis.com/v0/b/almap-de0d0.appspot.com/o/imagens%2Fpostagens%2Fpadrao.jpg?alt=media&token=7f0caf92-04e1-41a1-8360-3a8b692831cd";
            DatabaseReference firebaseref = ConfiguracaoFirebase.getFirebase();
            DatabaseReference empresaRef = firebaseref.child("empresas").child(getIdUsuario());
            empresaRef.setValue(this);
            getNomeFiltro();
        }
    }

    public String getNomeFiltro() {
        return getNome().toUpperCase();
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario (String idUsuario){
        this.idUsuario = idUsuario;
    }

    public String getUrlImagem () {
        return urlImagem;
    }

    public void setUrlImagem (String urlImagem){
        this.urlImagem = urlImagem;
    }

    public String getNome () {
        return nome;
    }

    public void setNome (String nome){
        this.nome = nome;
    }

    public String getResponsavel () {
        return responsavel;
    }

    public void setResponsavel (String responsavel){
        this.responsavel = responsavel;
    }

    public String getEstado () {
        return estado;
    }

    public void setEstado (String estado){
        this.estado = estado;
    }

    public String getCidade () {
        return cidade;
    }

    public void setCidade (String cidade){
        this.cidade = cidade;
    }

    public String getbRN () {
        return bRN;
    }

    public void setbRN (String bRN){
        this.bRN = bRN;
    }

}
