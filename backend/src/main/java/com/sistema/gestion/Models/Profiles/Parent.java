package com.sistema.gestion.Models.Profiles;

import java.util.Set;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Parent extends ProfileClass {
    @Id
    private String id;

    private Set<String> children;
}
