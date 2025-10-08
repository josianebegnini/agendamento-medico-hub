package com.example.msagenda.services;

import com.example.msagenda.client.MedicoClient;
import com.example.msagenda.client.PacienteClient;
import com.example.msagenda.dtos.AgendamentoRequestDTO;
import com.example.msagenda.dtos.MedicoResponseDTO;
import com.example.msagenda.dtos.PacienteResponseDTO;
import com.example.msagenda.enums.StatusAgenda;
import com.example.msagenda.enums.TipoConsulta;
import com.example.msagenda.exceptions.ResourceNotFoundException;
import com.example.msagenda.models.Agenda;
import com.example.msagenda.repositories.AgendaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendaService {
    private final AgendaRepository agendaRepository;
    private final MedicoClient medicoClient;
    private final PacienteClient pacienteClient;

    public AgendaService(AgendaRepository agendaRepository, MedicoClient medicoClient, PacienteClient pacienteClient) {
        this.agendaRepository = agendaRepository;
        this.medicoClient = medicoClient;
        this.pacienteClient = pacienteClient;
    }

    public List<Agenda> listar() {
        return agendaRepository.findAll();
    }

    /**
     * Busca agenda por ID.
     */
    public Agenda buscarPorId(Long id) {
        return agendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado."));
    }

    /**
     * Agenda uma nova consulta com validações.
     */
    public Agenda agendar(AgendamentoRequestDTO agendamentoDTO) {

        // verifica se o médico e o paciente existem
        MedicoResponseDTO medico = medicoClient.buscarPorId(agendamentoDTO.getMedicoId());
        PacienteResponseDTO paciente = pacienteClient.getPacienteById(agendamentoDTO.getPacienteId());

        if(medico == null){
            throw new ResourceNotFoundException("Médico não encontrado.");
        }
        if(paciente == null){
            throw new ResourceNotFoundException("Paciente não encontrado.");
        }

        boolean ocupado = agendaRepository.existsByMedicoAndDataHora(medico.getId(), agendamentoDTO.getDataHora());
        if (ocupado) {
            throw new IllegalArgumentException("O médico já possui um agendamento neste horário.");
        }

        Agenda novaAgenda = Agenda.builder()
                .pacienteId(paciente.getId())
                .medicoId(medico.getId())
                .dataHora(agendamentoDTO.getDataHora())
                .tipoConsulta(agendamentoDTO.getTipoConsulta())
                .status(StatusAgenda.AGENDADA)
                .build();

        return agendaRepository.save(novaAgenda);
    }

    /**
     * Reagenda uma consulta (altera data/hora e tipo de consulta).
     */
    public Agenda remarcar(Long idAgenda, LocalDateTime novaDataHora, TipoConsulta novoTipo) {
        Agenda agenda = buscarPorId(idAgenda);

        boolean ocupado = agendaRepository.existsByMedicoAndDataHora(agenda.getMedicoId(), novaDataHora);
        if (ocupado) {
            throw new IllegalArgumentException("O médico já possui um agendamento neste horário.");
        }

        agenda.setDataHora(novaDataHora);
        agenda.setTipoConsulta(novoTipo);
        agenda.setStatus(StatusAgenda.AGENDADA);

        return agendaRepository.save(agenda);
    }

    /**
     * Cancela uma consulta existente.
     */
    public Agenda cancelar(Long idAgenda) {
        Agenda agenda = buscarPorId(idAgenda);
        agenda.setStatus(StatusAgenda.CANCELADA);
        return agendaRepository.save(agenda);
    }

    /**
     * Remove uma consulta do sistema.
     */
    public void deletar(Long idAgenda) {
        agendaRepository.delete(buscarPorId(idAgenda));
    }
}
