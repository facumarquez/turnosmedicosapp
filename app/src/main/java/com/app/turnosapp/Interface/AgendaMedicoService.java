package com.app.turnosapp.Interface;

import com.app.turnosapp.Model.AgendaMedico;
import com.app.turnosapp.Model.AgendaMedicoFecha;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AgendaMedicoService {

    @GET("AgendaMedicos/{idMedico}/{mes}/{anio}")
    Call<AgendaMedico> getAgendaMedico(@Path("idMedico") long idMedico, @Path("mes") int mes,
                                       @Path("anio") int anio);

    @POST("AgendaMedicos")
    Call<AgendaMedico> crearAgendaMedico(@Body AgendaMedico agenda);

    @POST("AgendaMedicos/{idAgendaMedico}/GenerarTurnos")
    Call<Boolean>generarTurnos(@Path("idAgendaMedico") long idAgendaMedico);

    @GET("AgendaMedicos/{idAgendaMedico}/{fecha}")
    Call<AgendaMedicoFecha> obtenerFechaEspecificaDeAgenda(@Path("idAgendaMedico") long idAgendaMedico,
                                                           @Path("fecha") String fecha);

    @DELETE("AgendaMedicos/{idAgendaMedico}/EliminarFechasHuerfanas")
    Call<Void> eliminarFechasHuerfanas(@Path("idAgendaMedico") Long idAgendaMedico);

}
