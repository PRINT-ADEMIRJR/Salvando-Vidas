package model;

import com.google.firebase.database.DatabaseReference;

import helper.ConfiguracaoFirebase;

public class IndicacaoVisitaDoUsuario {

    private String idUsuario;
    private String nome;
    private String estado;
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;
    private String numeroTelefone;
    private  String observacao;

   public  IndicacaoVisitaDoUsuario(){

    }

    public void salvar(){
        DatabaseReference firebaseref = ConfiguracaoFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseref.child("OutrosUsuarios").child(getIdUsuario());
        empresaRef.setValue(this);
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
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

    public String getNumeroTelefone() {
        return numeroTelefone;
    }

    public void setNumeroTelefone(String numeroTelefone) {
        this.numeroTelefone = numeroTelefone;
    }
}
