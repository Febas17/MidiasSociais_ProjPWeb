package br.edu.eseg.midias_sociais.model;

public enum PerfilAcesso {
    ADMIN("Administrador"),
    ANALISTA("Analista de Mídia");

    private final String descricao;

    PerfilAcesso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
