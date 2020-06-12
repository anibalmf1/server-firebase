package br.com.anibal.captacaofilipeserver.entities;

import br.com.anibal.captacaofilipeserver.firebase.Collections;
import br.com.anibal.captacaofilipeserver.firebase.FBEntity;
import br.com.anibal.captacaofilipeserver.firebase.FirebaseEntity;
import lombok.Data;

@Data
@FBEntity(Collections.USER)
public class User implements FirebaseEntity {

    private String id;
    private String username;
    private String password;
    private Boolean active;
    private String roles;
}
