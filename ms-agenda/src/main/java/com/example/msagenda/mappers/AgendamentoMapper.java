package com.example.msagenda.mappers;

import com.example.msagenda.dtos.AgendamentoResponseDTO;
import com.example.msagenda.models.Agenda;
import org.springframework.stereotype.Component;

@Component
public class AgendamentoMapper {

    public AgendamentoResponseDTO toResponse(Agenda agenda) {
        return AgendamentoResponseDTO.builder()
                .id(agenda.getId())
                .pacienteNome(
                        agenda.getPaciente() != null ? agenda.getPaciente().getNome() : "Paciente"
                )
                .medicoNome(
                        agenda.getMedico() != null ? agenda.getMedico().getNome() : "Médico"
                )
                .dataHora(agenda.getDataHora())
                .tipoConsulta(agenda.getTipoConsulta())
                .status(agenda.getStatus())
                .build();
    }
}
