package com.example.mspaciente.services;

import com.example.mspaciente.dtos.PacienteRequestDTO;
import com.example.mspaciente.exceptions.BusinessException;
import com.example.mspaciente.exceptions.ResourceNotFoundException;
import com.example.mspaciente.mappers.PacienteMapper;
import com.example.mspaciente.models.Convenio;
import com.example.mspaciente.models.Paciente;
import com.example.mspaciente.repositories.ConvenioRepository;
import com.example.mspaciente.repositories.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final ConvenioRepository convenioRepository;
    private final PacienteMapper pacienteMapper;

    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> findById(Long id) {
        return pacienteRepository.findById(id);
    }

    public Paciente findByEmail(String email) {
        return pacienteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com email: " + email));
    }

    @Transactional
    public Paciente saveFromRequest(PacienteRequestDTO request) {
        if (pacienteRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Já existe um paciente cadastrado com este email");
        }

        Paciente paciente = pacienteMapper.toEntity(request);

        // Associa o convênio, se existir
        if (request.getConvenioId() != null) {
            Convenio convenio = convenioRepository.findById(request.getConvenioId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Convênio não encontrado com ID: " + request.getConvenioId()));
            paciente.setConvenio(convenio);
        }

        return pacienteRepository.save(paciente);
    }

    @Transactional
    public Paciente update(Long id, PacienteRequestDTO pacienteAtualizado) {
        Paciente pacienteExistente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + id));

        // Verifica se o email foi alterado e se já existe
        if (!pacienteExistente.getEmail().equals(pacienteAtualizado.getEmail()) &&
                pacienteRepository.existsByEmail(pacienteAtualizado.getEmail())) {
            throw new BusinessException("Já existe um paciente cadastrado com este email");
        }

        pacienteExistente.setNome(pacienteAtualizado.getNome());
        pacienteExistente.setEmail(pacienteAtualizado.getEmail());
        pacienteExistente.setTelefone(pacienteAtualizado.getTelefone());
        pacienteExistente.setDataNascimento(pacienteAtualizado.getDataNascimento());

        if (pacienteAtualizado.getConvenioId() != null) {
            Convenio convenio = convenioRepository.findById(pacienteAtualizado.getConvenioId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Convênio não encontrado com ID: " + pacienteAtualizado.getConvenioId()));
            pacienteExistente.setConvenio(convenio);
        } else {
            pacienteExistente.setConvenio(null);
        }

        return pacienteRepository.save(pacienteExistente);
    }

    @Transactional
    public Paciente updateFromRequest(Long id, PacienteRequestDTO request) {
        Paciente pacienteExistente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + id));

        pacienteExistente.setNome(request.getNome() != null ? request.getNome():pacienteExistente.getNome());
        pacienteExistente.setTelefone(request.getTelefone() != null ? request.getTelefone():pacienteExistente.getTelefone());
        pacienteExistente.setDataNascimento(request.getDataNascimento() != null ? request.getDataNascimento():pacienteExistente.getDataNascimento());

        if (request.getEmail() != null &&
                !pacienteExistente.getEmail().equals(request.getEmail()) &&
                pacienteRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Já existe um paciente cadastrado com este email");
        }

        pacienteExistente.setEmail(request.getEmail() != null ? request.getEmail() : pacienteExistente.getEmail());

        if (request.getConvenioId() != null) {
            Convenio convenio = convenioRepository.findById(request.getConvenioId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Convênio não encontrado com ID: " + request.getConvenioId()));
            pacienteExistente.setConvenio(convenio);
        } else {
            pacienteExistente.setConvenio(null);
        }

        return pacienteRepository.save(pacienteExistente);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paciente não encontrado com ID: " + id);
        }
        pacienteRepository.deleteById(id);
    }

    public List<Paciente> findByNomeContaining(String nome) {
        return pacienteRepository.findByNomeContaining(nome);
    }
}