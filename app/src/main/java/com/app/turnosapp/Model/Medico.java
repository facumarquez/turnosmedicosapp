package com.app.turnosapp.Model;

public class Medico {
    private int id_usuario;
    private String legajo;

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    @Override
    public String toString() {
        return "Medico{" +
                "id_usuario=" + id_usuario +
                ", legajo='" + legajo + '\'' +
                '}';
    }

}
