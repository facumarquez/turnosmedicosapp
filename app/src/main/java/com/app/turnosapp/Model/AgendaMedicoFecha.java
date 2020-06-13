package com.app.turnosapp.Model;

import java.io.Serializable;
import java.util.List;

public class AgendaMedicoFecha implements Serializable {
    private long id;
    private String fecha;
    private AgendaMedico agendaMedico;
    private Especialidad especialidad;

    private List<AgendaMedicoHorario> horarios;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public AgendaMedico getAgendaMedico() {
        return agendaMedico;
    }

    public void setAgendaMedico(AgendaMedico agendaMedico) {
        this.agendaMedico = agendaMedico;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public List<AgendaMedicoHorario> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<AgendaMedicoHorario> horarios) {
        this.horarios = horarios;
    }

    public AgendaMedicoFecha(String fecha, AgendaMedico agendaMedico, Especialidad especialidad) {
        this.fecha = fecha;
        this.agendaMedico = agendaMedico;
        this.especialidad = especialidad;
    }
}
