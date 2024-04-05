package com.bruxo.bruxo.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

//Data Transfer Object (DTO)
public class UsuarioDto {
    private Integer id;
    
    @NotEmpty(message = "O campo de nome não pode estar vazio")
    private String nome;

    @NotEmpty(message = "O campo de email não pode estar vazio")
    @Pattern(regexp = ".+@.+\\..+", message = "Insira um email válido, por favor")
    @Email
    private String email;

    @NotEmpty(message = "Necessário inserir o CPF")
    @Size(min = 11, message = "Insira um CPF Valido. ")
    @Size(max = 11, message = "Insira um CPF Valido")
    private String cpf;

    @NotEmpty(message = "A senha não pode estar vazia")
    @Size(min = 6, message = "A senha deve ter mais de 6 caracteres")
    private String senha;

    @NotEmpty(message = "A confirmação de senha não pode estar vazia")
    @Size(min = 6, message = "A senha deve ter mais de 6 caracteres")
    private String confirmaSenha;

    private String status = "Ativo";

    private String grupo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() { return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmaSenha() {
        return confirmaSenha;
    }

    public void setConfirmaSenha(String confirmaSenha) {
        this.confirmaSenha = confirmaSenha;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

}

