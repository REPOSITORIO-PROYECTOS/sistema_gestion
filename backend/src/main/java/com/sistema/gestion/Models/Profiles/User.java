package com.sistema.gestion.Models.Profiles;

import org.springframework.data.annotation.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends ProfileClass {
    @Id
    private String id;
}