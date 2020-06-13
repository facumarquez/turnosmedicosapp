package com.app.turnosapp.Interface;

import com.app.turnosapp.Model.Turno;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AgendaMedicoTurnoService {

    //Para pruebas
    @GET("posts")
    Call<List<Turno>> getTurnos();
}
