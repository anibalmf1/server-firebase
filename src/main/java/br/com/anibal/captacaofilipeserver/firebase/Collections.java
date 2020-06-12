package br.com.anibal.captacaofilipeserver.firebase;

public enum Collections {
    CLINIC("clinica"),
    USER("user");

    private String name;

    Collections(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
