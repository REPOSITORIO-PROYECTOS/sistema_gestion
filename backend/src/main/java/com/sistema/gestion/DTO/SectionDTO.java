package com.sistema.gestion.DTO;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SectionDTO {
    private String id;
    private String title;
    private String description;
    private List<SubSectionDTO> subSections;
}
