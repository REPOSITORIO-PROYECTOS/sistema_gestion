package com.sistema.gestion.DTO;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.units.qual.N;

import com.sistema.gestion.Models.Admin.Management.VirtualCampus.File;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubSectionDTO {
    private String id;
    private String body;
    private List<File> files;
}
