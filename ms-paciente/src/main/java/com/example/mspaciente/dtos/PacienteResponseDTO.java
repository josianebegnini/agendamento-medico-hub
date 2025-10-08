package com.example.mspaciente.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacienteResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private ConvenioResponseDTO convenio;
}