package com.bibliotech.DTOs;

public class LoginResponse {

    private boolean sucesso;
    private String mensagem;
    private UsuarioDTO usuario;
    private String token;

    public LoginResponse(boolean sucesso, String mensagem) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
    }

    public LoginResponse(boolean sucesso, String mensagem, UsuarioDTO usuario) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.usuario = usuario;
    }

    public LoginResponse(boolean sucesso, String mensagem, UsuarioDTO usuario, String token) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.usuario = usuario;
        this.token = token;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}