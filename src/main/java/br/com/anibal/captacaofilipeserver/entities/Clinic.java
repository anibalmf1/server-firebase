package br.com.anibal.captacaofilipeserver.entities;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class Clinic {
    private String id;

    @NotNull
    private String nome;
    @NotNull
    private String descricao;
    @NotNull
    private String tipo;
    @NotNull
    private String cidade;

    private List<String> imagens;
}
