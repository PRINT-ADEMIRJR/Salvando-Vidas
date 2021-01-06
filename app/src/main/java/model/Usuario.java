package model;

import com.google.firebase.database.DatabaseReference;

import helper.ConfiguracaoFirebase;

public class Usuario {

    private String idUsuario;
    private String urlImagem;
    private String nome;
   // private String nomeFiltro;
    private String sobrenome;
    private String estado;
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;
    private String numeroTelefone;
    private String Observacao;



    public void salvar() {//metodo para salvar no firebase

        if (getUrlImagem() != ""){
            DatabaseReference firebaseref = ConfiguracaoFirebase.getFirebase();
            DatabaseReference empresaRef = firebaseref.child("usuarios").child(getIdUsuario());
            empresaRef.setValue(this);
        }else{
            setUrlImagem("https://firebasestorage.googleapis.com/v0/b/almap-de0d0.appspot.com/o/imagens%2Fpostagens%2Fpadrao.jpg?alt=media&token=7f0caf92-04e1-41a1-8360-3a8b692831cd");
            DatabaseReference firebaseref = ConfiguracaoFirebase.getFirebase();
            DatabaseReference empresaRef = firebaseref.child("usuarios").child(getIdUsuario());
            empresaRef.setValue(this);

        }
    }

    /*public String getNomeFiltro() {
        return getNome().toUpperCase();
    }*/

    public String getNumeroTelefone() {
        return numeroTelefone;
    }

    public void setNumeroTelefone(String numeroTelefone) {
        this.numeroTelefone = numeroTelefone;
    }

    public String getObservacao() {
        return Observacao;
    }

    public void setObservacao(String observacao) {
        Observacao = observacao;
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


    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
