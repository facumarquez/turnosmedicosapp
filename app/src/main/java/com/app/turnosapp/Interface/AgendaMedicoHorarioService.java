package com.app.turnosapp.Interface;

import com.app.turnosapp.Model.AgendaMedicoFecha;
import com.app.turnosapp.Model.AgendaMedicoHorario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AgendaMedicoHorarioService {

    @DELETE("AgendaMedicoHorarios/{idAgendaMedicoHorario}")
    Call<Void> deleteAgendaMedicoHorario(@Path("idAgendaMedicoHorario") Long idAgendaMedicoHorario);

    @POST("AgendaMedicoHorarios/")
    Call<List<AgendaMedicoHorario>> crearHorarios(@Body List<AgendaMedicoHorario> horarios);

    @POST("AgendaMedicoHorarios/EliminarHorarios")
    Call<Void> deleteHorarios(@Body List<AgendaMedicoFecha> fechas);

}
