package com.example.msagenda.repositories;

import com.example.msagenda.models.Agenda;
import com.example.msagenda.models.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
    boolean existsByMedicoAndDataHora(Medico medico, LocalDateTime dataHora);
}
