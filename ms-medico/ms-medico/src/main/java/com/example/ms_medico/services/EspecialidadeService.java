package com.example.ms_medico.services;

import com.example.ms_medico.dtos.EspecialidadeDTO;
import com.example.ms_medico.exceptions.ResourceNotFoundException;
import com.example.ms_medico.mappers.EspecialidadeMapper;
import com.example.ms_medico.models.Especialidade;
import com.example.ms_medico.repositories.EspecialidadeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EspecialidadeService {

    private final EspecialidadeRepository especialidadeRepository;

    public EspecialidadeService(EspecialidadeRepository especialidadeRepository) {
        this.especialidadeRepository = especialidadeRepository;
    }

    public List<EspecialidadeDTO> listarTodas() {
        return especialidadeRepository.findAll()
                .stream()
                .map(EspecialidadeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<EspecialidadeDTO> buscarPorId(Long id) {
        return especialidadeRepository.findById(id).map(EspecialidadeMapper::toDTO);
    }

    public List<Especialidade> buscarPorIds(List<Long> ids) {
        List<Especialidade> especialidades = especialidadeRepository.findAllById(ids);

        if (especialidades.size() != ids.size()) {
            throw new ResourceNotFoundException("Algumas especialidades não foram encontradas.");
        }

        return especialidades;
    }

    public EspecialidadeDTO salvar(EspecialidadeDTO dto) {
        if (especialidadeRepository.existsByNome(dto.getNome())) {
            throw new RuntimeException("Especialidade já cadastrada: " + dto.getNome());
        }
        Especialidade entity = EspecialidadeMapper.toEntity(dto);
        return EspecialidadeMapper.toDTO(especialidadeRepository.save(entity));
    }

    public EspecialidadeDTO atualizar(Long id, EspecialidadeDTO dto) {
        return especialidadeRepository.findById(id)
                .map(entity -> {
                    entity.setNome(dto.getNome());
                    return EspecialidadeMapper.toDTO(especialidadeRepository.save(entity));
                })
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada com id " + id));
    }

    public void deletar(Long id) {
        if (!especialidadeRepository.existsById(id)) {
            throw new RuntimeException("Especialidade não encontrada com id " + id);
        }
        especialidadeRepository.deleteById(id);
    }
}
