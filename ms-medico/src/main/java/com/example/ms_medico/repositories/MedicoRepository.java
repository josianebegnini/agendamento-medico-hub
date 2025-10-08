package com.example.ms_medico.repositories;

import com.example.ms_medico.models.Medico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Optional<Medico> findByCrm(String crm);
    Page<Medico> findByEspecialidades_NomeIgnoreCase(String especialidade, Pageable pageable);
}
