package com.app.turnosapp.Model;

import com.google.gson.JsonObject;

import java.util.Map;

public class Turno {
    private Long id;
    private String fechaTurno;
    private String turnoHasta;
    private String estadoTurno;

    private Especialidad especialidad;
    private String turnoDesde;
    private Medico medico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFechaTurno() {
        return fechaTurno;
    }

    public void setFechaTurno(String fechaTurno) {
        this.fechaTurno = fechaTurno;
    }

    public String getTurnoHasta() {
        return turnoHasta;
    }

    public void setTurnoHasta(String turnoHasta) {
        this.turnoHasta = turnoHasta;
    }

    public String getEstadoTurno() {
        return estadoTurno;
    }

    public void setEstadoTurno(String estadoTurno) {
        this.estadoTurno = estadoTurno;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public String getTurnoDesde() {
        return turnoDesde;
    }

    public void setTurnoDesde(String turnoDesde) {
        this.turnoDesde = turnoDesde;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    @Override
    public String toString() {
        return "Turno{" +
                "id=" + id +
                ", fechaTurno='" + fechaTurno + '\'' +
                ", turnoHasta='" + turnoHasta + '\'' +
                ", especialidad=" + especialidad +
                ", turnoDesde='" + turnoDesde + '\'' +
                ", medico=" + medico +
                '}';
    }
}
