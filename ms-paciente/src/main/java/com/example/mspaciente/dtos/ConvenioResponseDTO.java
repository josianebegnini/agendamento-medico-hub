package com.example.mspaciente.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConvenioResponseDTO {

    private Long id;

    private String nome;

    private String cobertura;

    private String telefoneContato;
}