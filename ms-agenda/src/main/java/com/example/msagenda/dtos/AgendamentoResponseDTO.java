package com.example.msagenda.dtos;

import com.example.msagenda.enums.StatusAgenda;
import com.example.msagenda.enums.TipoConsulta;

import java.time.LocalDateTime;

public class AgendamentoResponseDTO {
    private Long id;
    private Long pacienteId;
    private String pacienteNome;
    private Long medicoId;
    private String medicoNome;
    private LocalDateTime dataHora;
    private TipoConsulta tipoConsulta;
    private StatusAgenda status;

    public AgendamentoResponseDTO() {
    }

    public AgendamentoResponseDTO(Long id, Long pacienteId, String pacienteNome, Long medicoId, String medicoNome, LocalDateTime dataHora, TipoConsulta tipoConsulta, StatusAgenda status) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.pacienteNome = pacienteNome;
        this.medicoId = medicoId;
        this.medicoNome = medicoNome;
        this.dataHora = dataHora;
        this.tipoConsulta = tipoConsulta;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public String getPacienteNome() { return pacienteNome; }
    public void setPacienteNome(String pacienteNome) { this.pacienteNome = pacienteNome; }

    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }

    public String getMedicoNome() { return medicoNome; }
    public void setMedicoNome(String medicoNome) { this.medicoNome = medicoNome; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public TipoConsulta getTipoConsulta() { return tipoConsulta; }
    public void setTipoConsulta(TipoConsulta tipoConsulta) { this.tipoConsulta = tipoConsulta; }

    public StatusAgenda getStatus() { return status; }
    public void setStatus(StatusAgenda status) { this.status = status; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long pacienteId;
        private String pacienteNome;
        private Long medicoId;
        private String medicoNome;
        private LocalDateTime dataHora;
        private TipoConsulta tipoConsulta;
        private StatusAgenda status;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder pacienteId(Long pacienteId) {
            this.pacienteId = pacienteId;
            return this;
        }

        public Builder pacienteNome(String pacienteNome) {
            this.pacienteNome = pacienteNome;
            return this;
        }

        public Builder medicoId(Long medicoId) {
            this.medicoId = medicoId;
            return this;
        }

        public Builder medicoNome(String medicoNome) {
            this.medicoNome = medicoNome;
            return this;
        }

        public Builder dataHora(LocalDateTime dataHora) {
            this.dataHora = dataHora;
            return this;
        }

        public Builder tipoConsulta(TipoConsulta tipoConsulta) {
            this.tipoConsulta = tipoConsulta;
            return this;
        }

        public Builder status(StatusAgenda status) {
            this.status = status;
            return this;
        }

        public AgendamentoResponseDTO build() {
            return new AgendamentoResponseDTO(
                    id, pacienteId, pacienteNome, medicoId, medicoNome, dataHora, tipoConsulta, status
            );
        }
    }
}
