package com.app.turnosapp.Model;

import java.io.Serializable;
import java.util.List;

public class AgendaMedicoHorario implements Serializable {

    private Long id;
    private String horaDesde;
    private String horaHasta;
    private AgendaMedicoFecha agendaMedicoFecha;
    private List<AgendaMedicoTurno> turnos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHoraDesde() {
        return horaDesde;
    }

    public void setHoraDesde(String horaDesde) {
        this.horaDesde = horaDesde;
    }

    public String getHoraHasta() {
        return horaHasta;
    }

    public void setHoraHasta(String horaHasta) {
        this.horaHasta = horaHasta;
    }

    public AgendaMedicoFecha getAgendaMedicoFecha() {
        return agendaMedicoFecha;
    }

    public void setAgendaMedicoFecha(AgendaMedicoFecha agendaMedicoFecha) {
        this.agendaMedicoFecha = agendaMedicoFecha;
    }

    public List<AgendaMedicoTurno> getTurnos() {
        return turnos;
    }

    public void setTurnos(List<AgendaMedicoTurno> turnos) {
        this.turnos = turnos;
    }

    public AgendaMedicoHorario(String horaDesde, String horaHasta) {
        this.horaDesde = horaDesde;
        this.horaHasta = horaHasta;
    }

}
