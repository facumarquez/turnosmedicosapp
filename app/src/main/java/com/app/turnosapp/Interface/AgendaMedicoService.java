package com.app.turnosapp.Interface;

import com.app.turnosapp.Model.AgendaMedico;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AgendaMedicoService {

    @GET("AgendaMedicos/{idMedico}/{mes}/{anio}")
    Call<AgendaMedico> getAgendaMedico(@Path("idMedico") long idMedico,
                                       @Path("mes") int mes, @Path("anio") int anio);

    @POST("AgendaMedicos")
    Call<AgendaMedico> crearAgendaMedico(@Body AgendaMedico agenda);
}
