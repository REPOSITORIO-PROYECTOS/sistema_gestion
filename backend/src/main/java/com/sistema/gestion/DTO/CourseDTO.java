package com.sistema.gestion.DTO;

import java.util.Set;

import com.sistema.gestion.Utils.CourseStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CourseDTO {
    
    private String id;
    private String title;
    private String description;
    private CourseStatus status;
    private Double monthlyPrice;
    private Set<IdAndNameDTO> studentsIds;
    private IdAndNameDTO teacherId;

}
