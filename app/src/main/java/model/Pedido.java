package model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import helper.ConfiguracaoFirebase;

public class Pedido {

    private String idUsuario;
    private String idEmpresa;
    private String idRequisição;
    private String nome;
    private String sobrenome;
    private String estado;
    private String cidade;
    private String bairro;
    private String rua;
    private String numeroCasa;
    private String numeroTelefone;
    private String estatus= "Pendente";
    private String observacao;
    private List<ItensPedido> itens;

    public Pedido() {

    }
    public Pedido(String idUsu, String idEmp) {
        setIdUsuario(idUsu);
        setIdEmpresa(idEmp);
        DatabaseReference firebaseRef= ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef= firebaseRef
                .child("pedidos_usuario")
                .child(idEmp)
                .child(idUsu);
        setIdRequisição(pedidoRef.push().getKey());
    }

    public void salvar(){
        DatabaseReference firebaseRef= ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef= firebaseRef
                .child("pedidos_usuario")
                .child(idEmpresa)
                .child(idUsuario);
        pedidoRef.setValue(this);

    }


    public List<ItensPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItensPedido> itens) {
        this.itens = itens;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdRequisição() {
        return idRequisição;
    }

    public void setIdRequisição(String idRequisição) {
        this.idRequisição = idRequisição;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
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

    public String getNumeroCasa() {
        return numeroCasa;
    }

    public void setNumeroCasa(String numeroCasa) {
        this.numeroCasa = numeroCasa;
    }

    public String getNumeroTelefone() {
        return numeroTelefone;
    }

    public void setNumeroTelefone(String numeroTelefone) {
        this.numeroTelefone = numeroTelefone;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
