package com.app.turnosapp.Model;

import java.io.Serializable;

public class AgendaMedicoFecha implements Serializable {
    private long id;
    private String fecha;
    private AgendaMedico agendaMedico;
    private Especialidad especialidad;

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

    //TODO: completar campos
    //private List<AgendaMedicoHorario> horarios;

    public AgendaMedicoFecha(String fecha, AgendaMedico agendaMedico, Especialidad especialidad) {
        this.fecha = fecha;
        this.agendaMedico = agendaMedico;
        this.especialidad = especialidad;
    }
}
