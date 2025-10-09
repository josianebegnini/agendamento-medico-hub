package com.example.msagenda.models;

import com.example.msagenda.enums.StatusAgenda;
import com.example.msagenda.enums.TipoConsulta;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    private TipoConsulta tipoConsulta;

    @Enumerated(EnumType.STRING)
    private StatusAgenda status;

    private Long medicoId;
    private Long pacienteId;
    public Agenda() {}

    public Agenda(Long id, Long pacienteId, Long medicoId,
                  LocalDateTime dataHora, TipoConsulta tipoConsulta, StatusAgenda status) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.dataHora = dataHora;
        this.tipoConsulta = tipoConsulta;
        this.status = status;
    }

    // ✅ Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public TipoConsulta getTipoConsulta() { return tipoConsulta; }
    public void setTipoConsulta(TipoConsulta tipoConsulta) { this.tipoConsulta = tipoConsulta; }

    public StatusAgenda getStatus() { return status; }
    public void setStatus(StatusAgenda status) { this.status = status; }

    // ✅ Builder manual
    public static AgendaBuilder builder() {
        return new AgendaBuilder();
    }

    public static class AgendaBuilder {
        private Long id;
        private Long pacienteId;
        private Long medicoId;
        private LocalDateTime dataHora;
        private TipoConsulta tipoConsulta;
        private StatusAgenda status;

        public AgendaBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AgendaBuilder pacienteId(Long pacienteId) {
            this.pacienteId = pacienteId;
            return this;
        }

        public AgendaBuilder medicoId(Long medicoId) {
            this.medicoId = medicoId;
            return this;
        }

        public AgendaBuilder dataHora(LocalDateTime dataHora) {
            this.dataHora = dataHora;
            return this;
        }

        public AgendaBuilder tipoConsulta(TipoConsulta tipoConsulta) {
            this.tipoConsulta = tipoConsulta;
            return this;
        }

        public AgendaBuilder status(StatusAgenda status) {
            this.status = status;
            return this;
        }

        public Agenda build() {
            return new Agenda(id, pacienteId, medicoId, dataHora, tipoConsulta, status);
        }
    }
}
