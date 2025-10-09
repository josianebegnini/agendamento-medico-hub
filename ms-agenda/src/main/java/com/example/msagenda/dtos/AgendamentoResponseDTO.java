package com.example.msagenda.dtos;

import com.example.msagenda.enums.StatusAgenda;
import com.example.msagenda.enums.TipoConsulta;

import java.time.LocalDateTime;

public class AgendamentoResponseDTO {
    private Long id;
    private String pacienteNome;
    private String medicoNome;
    private LocalDateTime dataHora;
    private TipoConsulta tipoConsulta;
    private StatusAgenda status;

    public AgendamentoResponseDTO() {
    }

    public AgendamentoResponseDTO(Long id, String pacienteNome, String medicoNome, LocalDateTime dataHora, TipoConsulta tipoConsulta, StatusAgenda status) {
        this.id = id;
        this.pacienteNome = pacienteNome;
        this.medicoNome = medicoNome;
        this.dataHora = dataHora;
        this.tipoConsulta = tipoConsulta;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPacienteNome() {
        return pacienteNome;
    }

    public void setPacienteNome(String pacienteNome) {
        this.pacienteNome = pacienteNome;
    }

    public String getMedicoNome() {
        return medicoNome;
    }

    public void setMedicoNome(String medicoNome) {
        this.medicoNome = medicoNome;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public TipoConsulta getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(TipoConsulta tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public StatusAgenda getStatus() {
        return status;
    }

    public void setStatus(StatusAgenda status) {
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String pacienteNome;
        private String medicoNome;
        private LocalDateTime dataHora;
        private TipoConsulta tipoConsulta;
        private StatusAgenda status;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder pacienteNome(String pacienteNome) {
            this.pacienteNome = pacienteNome;
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
            return new AgendamentoResponseDTO(id, pacienteNome, medicoNome, dataHora, tipoConsulta, status);
        }
    }
}
