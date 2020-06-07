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
    public String toString()  {
        return this.getApellido() + ", " +this.getNombre();
    }


    public String getFullname() {
        return this.getApellido() + ", " +this.getNombre();
    }
}
