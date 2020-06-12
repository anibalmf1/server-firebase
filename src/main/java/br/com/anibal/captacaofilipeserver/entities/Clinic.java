package br.com.anibal.captacaofilipeserver.entities;

import br.com.anibal.captacaofilipeserver.firebase.Collections;
import br.com.anibal.captacaofilipeserver.firebase.FBEntity;
import br.com.anibal.captacaofilipeserver.firebase.FirebaseEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@FBEntity(Collections.CLINIC)
public class Clinic implements FirebaseEntity {
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
