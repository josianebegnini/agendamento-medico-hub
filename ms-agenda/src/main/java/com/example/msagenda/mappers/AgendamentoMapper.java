package com.example.msagenda.mappers;

import com.example.msagenda.dtos.AgendamentoResponseDTO;
import com.example.msagenda.models.Agenda;
import org.springframework.stereotype.Component;

@Component
public class AgendamentoMapper {

    public AgendamentoResponseDTO toResponse(Agenda agenda, String pacienteNome, String medicoNome) {
        return AgendamentoResponseDTO.builder()
                .id(agenda.getId())
                .pacienteNome(pacienteNome != null ? pacienteNome : "Paciente")
                .medicoNome(medicoNome != null ? medicoNome : "Médico")
                .dataHora(agenda.getDataHora())
                .tipoConsulta(agenda.getTipoConsulta())
                .status(agenda.getStatus())
                .build();
    }
}
