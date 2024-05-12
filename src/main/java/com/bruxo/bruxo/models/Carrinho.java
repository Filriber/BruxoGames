package com.bruxo.bruxo.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Carrinho {
    private List<ItemPedido> itens;
    private BigDecimal total;
    private BigDecimal frete;
    private FormasDePagamento formaDePagamento;


    public BigDecimal getFrete() {
        return frete;
    }

    public void setFrete(BigDecimal frete) {
        this.frete = frete;
    }

    public Carrinho() {
        this.itens = new ArrayList<>();
        this.total = BigDecimal.ZERO;
        this.frete = BigDecimal.ZERO;
    }

    public void adicionarItem(Produto produto, int quantidade) {
        for (ItemPedido item : itens) {
            if (item.getProduto().getId() == (produto.getId())) {
                item.setQuantidade(item.getQuantidade() + quantidade);
                return;
            }
        }
        ItemPedido novoItem = new ItemPedido();
        novoItem.setProduto(produto);
        novoItem.setQuantidade(quantidade);
        itens.add(novoItem);
    }

    public void aumentaQuantidade(Produto produto) {
        for (ItemPedido item : itens) {
            if (item.getProduto().getId() == (produto.getId())) {
                item.setQuantidade(item.getQuantidade() + 1);
                return;
            }
        }
    }

    public void diminuiQuantidade(Produto produto) {
        for (ItemPedido item : itens) {
            if (item.getProduto().getId() == (produto.getId())) {
                int novaQuantidade = item.getQuantidade() - 1;

                if (novaQuantidade <= 0) {
                    itens.remove(item);
                } else {
                    item.setQuantidade(novaQuantidade);
                }
                return;
            }
        }
    }

    public void removerItem(Produto produto) {
        itens.removeIf(item -> item.getProduto().getId() == (produto.getId()));
    }

    public void atualizarQuantidade(Produto produto, int novaQuantidade) {
        for (ItemPedido item : itens) {
            if (item.getProduto().getId() == (produto.getId())) {
                item.setQuantidade(novaQuantidade);
                return;
            }
        }
    }

    public BigDecimal calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : itens) {
            BigDecimal subtotal = item.getProduto().getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));
            total = total.add(subtotal);
        }
        if (frete != null) {
            total = total.add(frete);
        }
        return total;
    }

    public BigDecimal calcularProdutos() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : itens) {
            BigDecimal subtotal = item.getProduto().getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));
            total = total.add(subtotal);
        }

        return total;
    }

    public void limpar() {
        this.itens.clear();
        this.formaDePagamento = null;
        this.frete = null;
    }

    public FormasDePagamento getFormaDePagamento() {
        return formaDePagamento;
    }

    public void setFormaDePagamento(FormasDePagamento formaDePagamento) {
        this.formaDePagamento = formaDePagamento;
    }


    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

}