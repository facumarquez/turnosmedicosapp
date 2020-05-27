package com.app.turnosapp.Model;

import java.io.Serializable;

public class AgendaMedico implements Serializable {
    private long id;
    private Medico medico;
    private int mes;
    private int anio;
    private String fechaCreacion;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
