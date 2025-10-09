package com.example.msagenda.services;

import com.example.msagenda.dtos.*;
import com.example.msagenda.enums.StatusAgenda;
import com.example.msagenda.enums.TipoConsulta;
import com.example.msagenda.exceptions.ResourceNotFoundException;
import com.example.msagenda.mappers.AgendamentoMapper;
import com.example.msagenda.models.Agenda;
import com.example.msagenda.producers.AgendaProducer;
import com.example.msagenda.repositories.AgendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final AgendamentoMapper agendamentoMapper;
    private final WebClient pacienteWebClient;
    private final WebClient medicoWebClient;
    private final AgendaProducer agendaProducer;

    public AgendaService(AgendaRepository agendaRepository,
                         AgendamentoMapper agendamentoMapper,
                         WebClient pacienteWebClient,
                         WebClient medicoWebClient,
                         AgendaProducer agendaProducer) {

        this.agendaRepository = agendaRepository;
        this.agendamentoMapper = agendamentoMapper;
        this.pacienteWebClient = pacienteWebClient;
        this.medicoWebClient = medicoWebClient;
        this.agendaProducer = agendaProducer;
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

        if (dto.getMedicoId() == null) {
            throw new ResourceNotFoundException("Médico inválido.");
        }
        if (dto.getPacienteId() == null) {
            throw new ResourceNotFoundException("Paciente inválido.");
        }
        MedicoResponseDTO medico = medicoWebClient.get()
                .uri("/api/medicos/{id}", dto.getMedicoId())
                .retrieve()
                .bodyToMono(MedicoResponseDTO.class).block();

        PacienteResponseDTO paciente = pacienteWebClient.get()
                .uri("/api/pacientes/{id}", dto.getPacienteId())
                .retrieve()
                .bodyToMono(PacienteResponseDTO.class).block();

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

        Agenda retorno = agendaRepository.save(novaAgenda);

        if (paciente.getEmail() != null && !paciente.getEmail().isBlank()) {

            EmailPacienteDTO emailDto = new EmailPacienteDTO();
            emailDto.setMedicoNome(medico.getNome());
            emailDto.setPacienteNome(paciente.getNome());
            emailDto.setPacienteEmail(paciente.getEmail());
            emailDto.setDataHora(dto.getDataHora());
            emailDto.setTipoConsulta(dto.getTipoConsulta());
            emailDto.setStatus(StatusAgenda.AGENDADA);
            emailDto.setAssunto("Consulta Agendada com Sucesso");
            emailDto.setMensagem("Sua consulta foi marcada para " + dto.getDataHora());

            // 3️⃣ Enviar mensagem via RabbitMQ
            agendaProducer.enviarEmailPaciente(emailDto);
        }

        return retorno;


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

        MedicoResponseDTO medico = medicoWebClient.get()
                .uri("/api/medicos/{id}", agenda.getMedicoId())
                .retrieve()
                .bodyToMono(MedicoResponseDTO.class).block();

        PacienteResponseDTO paciente = pacienteWebClient.get()
                .uri("/api/pacientes/{id}", agenda.getPacienteId())
                .retrieve()
                .bodyToMono(PacienteResponseDTO.class).block();

        return agendamentoMapper.toResponse(
                agenda,
                paciente != null ? paciente.getNome() : "Paciente",
                medico != null ? medico.getNome() : "Médico"
        );
    }
}