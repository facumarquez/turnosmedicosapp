package com.app.turnosapp.Interface;

import com.app.turnosapp.Model.AgendaMedicoFecha;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AgendaMedicoFechaService {

    @POST("AgendaMedicoFechas")
    Call<List<AgendaMedicoFecha>> crearFechasAgendaMedico(@Body List<AgendaMedicoFecha> fechasAgenda);
}
