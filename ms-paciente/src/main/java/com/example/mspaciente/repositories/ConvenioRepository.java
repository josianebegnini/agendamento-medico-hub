package com.example.mspaciente.repositories;

import com.example.mspaciente.models.Convenio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConvenioRepository extends JpaRepository<Convenio, Long> {
    Optional<Convenio> findByNome(String nome);
    boolean existsByNome(String nome);
}