package com.example.ms_medico.repositories;

import com.example.ms_medico.models.Especialidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {
    Optional<Especialidade> findByNome(String nome);
    boolean existsByNome(String nome);
}