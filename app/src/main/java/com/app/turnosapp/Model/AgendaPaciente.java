package com.app.turnosapp.Model;

public class AgendaPaciente {

    private Long id;
    private Paciente paciente;
    private AgendaMedicoTurno turno;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public AgendaMedicoTurno getTurno() {
        return turno;
    }

    public void setTurno(AgendaMedicoTurno turno) {
        this.turno = turno;
    }

    public Especialidad getEspecialidad() {
        return this.getTurno().getAgendaMedicoHorario().getAgendaMedicoFecha().getEspecialidad();
    }

    public Medico getMedico() {
        return this.getTurno().getAgendaMedicoHorario().getAgendaMedicoFecha().getAgendaMedico().getMedico();
    }

    public String getFechaTurno() {
        return this.getTurno().getAgendaMedicoHorario().getAgendaMedicoFecha().getFecha();
    }

    public String getTurnoDesde() {
        return this.getTurno().getTurnoDesde();
    }

    public String getTurnoHasta() {
        return this.getTurno().getTurnoHasta();
    }

    public String getEstadoTurno() {
        return this.getTurno().getEstado().toString();
    }
}
