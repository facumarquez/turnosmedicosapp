package com.app.turnosapp.Model;

import java.io.Serializable;

public class Medico extends Usuario implements Serializable {

    private String legajo;

    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    @Override
    public String toString() {
        return "Medico{" +
                "idUsuario=" + this.getIdUsuario() +
                ", nombre='" + this.getNombre() + '\'' +
                ", apellido='" + this.getApellido() + '\'' +
                ", usuario='" + this.getUsuario() + '\'' +
                ", password='" + this.getPassword() + '\'' +
                ", mail='" + this.getMail() + '\'' +
                ", sexo='" + this.getSexo() + '\'' +
                ", fecha_nacimiento='" + this.getFecha_nacimiento() + '\'' +
                ", telefono='" + this.getTelefono() + '\'' +
                ", legajo='" + this.getLegajo() + '\'' +
                '}';
    }

    public String getFullname() {
        return this.getNombre() + ", " +this.getApellido();
    }
}
