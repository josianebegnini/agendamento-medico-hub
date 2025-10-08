package com.example.msagenda.models;

import com.example.msagenda.enums.StatusAgenda;
import com.example.msagenda.enums.TipoConsulta;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    private TipoConsulta tipoConsulta;

    @Enumerated(EnumType.STRING)
    private StatusAgenda status;

    private Long medicoId;
    private Long pacienteId;
}
