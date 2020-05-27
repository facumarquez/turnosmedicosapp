package com.app.turnosapp.Model;

import java.util.ArrayList;

public class Paciente extends Usuario {

    private String documento;
    private Boolean cuotaAlDia;

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Boolean getCuotaAlDia() {
        return cuotaAlDia;
    }

    public void setCuotaAlDia(Boolean cuotaAlDia) {
        this.cuotaAlDia = cuotaAlDia;
    }

    @Override
    public String toString() {
        return "Paciente{" +
                "id=" + this.getIdUsuario() +
                ", nombre='" + this.getNombre() + '\'' +
                ", apellido='" + this.getApellido() + '\'' +
                ", usuario='" + this.getUsuario() + '\'' +
                ", password='" + this.getPassword() + '\'' +
                ", mail='" + this.getMail() + '\'' +
                ", sexo='" + this.getSexo() + '\'' +
                ", fecha_nacimiento='" + this.getFecha_nacimiento() + '\'' +
                ", telefono='" + this.getTelefono() + '\'' +
                ", documento='" + this.getDocumento() + '\'' +
                ", cuotaAlDia=" + this.getCuotaAlDia() +
                '}';
    }
}
