package br.com.anibal.captacaofilipeserver.entities;

import lombok.Data;

@Data
public class User {

    private String username;
    private String password;
    private Boolean active;
    private String roles;
}
