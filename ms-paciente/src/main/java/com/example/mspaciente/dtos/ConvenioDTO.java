package com.example.mspaciente.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvenioDTO {
    private Long id;
    private String nome;
    private String cobertura;
    private String telefoneContato;
}
