package com.app.turnosapp.Model;

import java.io.Serializable;
import java.util.List;

public class AgendaMedico implements Serializable {
    private long id;
    private Medico medico;
    private int mes;
    private int anio;
    private String fechaCreacion;
    private List<AgendaMedicoFecha> fechas;

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

    public List<AgendaMedicoFecha> getFechas() {
        return fechas;
    }

    public void setFechas(List<AgendaMedicoFecha> fechas) {
        this.fechas = fechas;
    }

    public AgendaMedico(int mes,int anio, Medico medico, String fechaCreacion){
        this.mes = mes;
        this.anio = anio;
        this.medico = medico;
        this.fechaCreacion = fechaCreacion;
    }


}
