package com.example.msagenda.services;

import com.example.msagenda.client.MedicoClient;
import com.example.msagenda.client.PacienteClient;
import com.example.msagenda.dtos.AgendamentoRequestDTO;
import com.example.msagenda.dtos.AgendamentoResponseDTO;
import com.example.msagenda.dtos.MedicoResponseDTO;
import com.example.msagenda.dtos.PacienteResponseDTO;
import com.example.msagenda.enums.StatusAgenda;
import com.example.msagenda.enums.TipoConsulta;
import com.example.msagenda.exceptions.ResourceNotFoundException;
import com.example.msagenda.mappers.AgendamentoMapper;
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
    private final AgendamentoMapper agendamentoMapper;

    public AgendaService(AgendaRepository agendaRepository,
                         MedicoClient medicoClient,
                         PacienteClient pacienteClient,
                         AgendamentoMapper agendamentoMapper) {
        this.agendaRepository = agendaRepository;
        this.medicoClient = medicoClient;
        this.pacienteClient = pacienteClient;
        this.agendamentoMapper = agendamentoMapper;
    }

    public List<AgendamentoResponseDTO> listarComNomes() {
        return agendaRepository.findAll().stream()
                .map(this::toResponseComNomes)
                .toList();
    }

    public AgendamentoResponseDTO buscarPorIdComNomes(Long id) {
        Agenda agenda = buscarPorId(id);
        return toResponseComNomes(agenda);
    }

    public AgendamentoResponseDTO agendarComNomes(AgendamentoRequestDTO dto) {
        Agenda agenda = agendar(dto);
        return toResponseComNomes(agenda);
    }

    public AgendamentoResponseDTO remarcarComNomes(Long id, LocalDateTime novaDataHora, TipoConsulta tipoConsulta) {
        Agenda agenda = remarcar(id, novaDataHora, tipoConsulta);
        return toResponseComNomes(agenda);
    }

    public AgendamentoResponseDTO cancelarComNomes(Long id) {
        Agenda agenda = cancelar(id);
        return toResponseComNomes(agenda);
    }

    /* Métodos antigos de CRUD já existentes */

    public List<Agenda> listar() {
        return agendaRepository.findAll();
    }

    public Agenda buscarPorId(Long id) {
        return agendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado."));
    }

    public Agenda agendar(AgendamentoRequestDTO dto) {
        MedicoResponseDTO medico = medicoClient.buscarPorId(dto.getMedicoId());
        PacienteResponseDTO paciente = pacienteClient.getPacienteById(dto.getPacienteId());

        if (medico == null) throw new ResourceNotFoundException("Médico não encontrado.");
        if (paciente == null) throw new ResourceNotFoundException("Paciente não encontrado.");

        boolean ocupado = agendaRepository.existsByMedicoIdAndDataHora(medico.getId(), dto.getDataHora());
        if (ocupado) throw new IllegalArgumentException("O médico já possui um agendamento neste horário.");

        Agenda novaAgenda = Agenda.builder()
                .medicoId(medico.getId())
                .pacienteId(paciente.getId())
                .dataHora(dto.getDataHora())
                .tipoConsulta(dto.getTipoConsulta())
                .status(StatusAgenda.AGENDADA)
                .build();

        return agendaRepository.save(novaAgenda);
    }

    public Agenda remarcar(Long idAgenda, LocalDateTime novaDataHora, TipoConsulta tipoConsulta) {
        Agenda agenda = buscarPorId(idAgenda);
        boolean ocupado = agendaRepository.existsByMedicoIdAndDataHora(agenda.getMedicoId(), novaDataHora);
        if (ocupado) throw new IllegalArgumentException("O médico já possui um agendamento neste horário.");

        agenda.setDataHora(novaDataHora);
        agenda.setTipoConsulta(tipoConsulta);
        agenda.setStatus(StatusAgenda.AGENDADA);

        return agendaRepository.save(agenda);
    }

    public Agenda cancelar(Long idAgenda) {
        Agenda agenda = buscarPorId(idAgenda);
        agenda.setStatus(StatusAgenda.CANCELADA);
        return agendaRepository.save(agenda);
    }

    public void deletar(Long idAgenda) {
        agendaRepository.delete(buscarPorId(idAgenda));
    }

    /* =============================
       Métodos auxiliares internos
       ============================= */
    private AgendamentoResponseDTO toResponseComNomes(Agenda agenda) {
        MedicoResponseDTO medico = medicoClient.buscarPorId(agenda.getMedicoId());
        PacienteResponseDTO paciente = pacienteClient.getPacienteById(agenda.getPacienteId());

        return agendamentoMapper.toResponse(
                agenda,
                paciente != null ? paciente.getNome() : "Paciente",
                medico != null ? medico.getNome() : "Médico"
        );
    }
}