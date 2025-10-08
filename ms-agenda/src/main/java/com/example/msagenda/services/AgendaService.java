package com.example.msagenda.services;

import com.example.msagenda.enums.StatusAgenda;
import com.example.msagenda.enums.TipoConsulta;
import com.example.msagenda.exceptions.ResourceNotFoundException;
import com.example.msagenda.models.Agenda;
import com.example.msagenda.models.Medico;
import com.example.msagenda.models.Paciente;
import com.example.msagenda.repositories.AgendaRepository;
import com.example.msagenda.repositories.MedicoRepository;
import com.example.msagenda.repositories.PacienteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendaService {
    private final AgendaRepository agendaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public AgendaService(AgendaRepository agendaRepository,
                         PacienteRepository pacienteRepository,
                         MedicoRepository medicoRepository) {
        this.agendaRepository = agendaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
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
    public Agenda agendar(Long pacienteId, Long medicoId, LocalDateTime dataHora,
                          TipoConsulta tipoConsulta) {

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado."));

        if (paciente.getConvenio() == null) {
            throw new IllegalArgumentException("Paciente não possui convênio vinculado.");
        }

        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado."));

        boolean ocupado = agendaRepository.existsByMedicoAndDataHora(medico, dataHora);
        if (ocupado) {
            throw new IllegalArgumentException("O médico já possui um agendamento neste horário.");
        }

        Agenda novaAgenda = Agenda.builder()
                .paciente(paciente)
                .medico(medico)
                .dataHora(dataHora)
                .tipoConsulta(tipoConsulta)
                .status(StatusAgenda.AGENDADA)
                .build();

        return agendaRepository.save(novaAgenda);
    }

    /**
     * Reagenda uma consulta (altera data/hora e tipo de consulta).
     */
    public Agenda remarcar(Long idAgenda, LocalDateTime novaDataHora, TipoConsulta novoTipo) {
        Agenda agenda = buscarPorId(idAgenda);

        boolean ocupado = agendaRepository.existsByMedicoAndDataHora(agenda.getMedico(), novaDataHora);
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
