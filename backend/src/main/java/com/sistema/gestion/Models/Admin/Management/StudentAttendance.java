package com.sistema.gestion.Models.Admin.Management;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.sistema.gestion.Models.ModelClass;
import com.sistema.gestion.Utils.AttendanceStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentAttendance extends ModelClass {
    @Id
    private String id;

    @NotNull
    private String courseId;

    @NotEmpty(message = "El conjunto de IDs de estudiantes no puede estar vacío.")
    private Set<String> studentsIds;

    @NotNull(message = "El mapa de estado de asistencia no puede ser nulo.")
    private Map<String, AttendanceStatus> attendanceStatus;

    @NotNull(message = "La fecha de asistencia no puede ser nula.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm")
    private LocalDateTime attendanceDate;
}