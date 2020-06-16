package com.app.turnosapp.Model;

import java.io.Serializable;

public class AgendaMedicoTurno implements Serializable {

    private Long id;
    private String turnoDesde;
    private String turnoHasta;
    private EstadoTurno estado;
    private AgendaMedicoHorario agendaMedicoHorario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTurnoDesde() {
        return turnoDesde;
    }

    public void setTurnoDesde(String turnoDesde) {
        this.turnoDesde = turnoDesde;
    }

    public String getTurnoHasta() {
        return turnoHasta;
    }

    public void setTurnoHasta(String turnoHasta) {
        this.turnoHasta = turnoHasta;
    }

    public EstadoTurno getEstado() {
        return estado;
    }

    public void setEstado(EstadoTurno estado) {
        this.estado = estado;
    }

    public AgendaMedicoHorario getAgendaMedicoHorario() {
        return agendaMedicoHorario;
    }

    public void setAgendaMedicoHorario(AgendaMedicoHorario agendaMedicoHorario) {
        this.agendaMedicoHorario = agendaMedicoHorario;
    }
}
