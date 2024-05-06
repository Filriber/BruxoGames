package com.bruxo.bruxo.models;

public class OpcaoFrete {
    private double valor;
    private String prazo;

    public OpcaoFrete(double valor, String prazo) {
        this.valor = valor;
        this.prazo = prazo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getPrazo() {
        return prazo;
    }

    public void setPrazo(String prazo) {
        this.prazo = prazo;
    }


}