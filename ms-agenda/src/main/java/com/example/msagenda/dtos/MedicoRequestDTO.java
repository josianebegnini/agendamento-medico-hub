package com.example.msagenda.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicoRequestDTO {
    private Long id;
    private String nome;
    private String crm;
    private String endereco;
    private List<Long> especialidades;
}
