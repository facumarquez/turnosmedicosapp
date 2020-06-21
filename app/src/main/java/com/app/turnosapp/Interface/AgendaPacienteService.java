package com.app.turnosapp.Interface;

import com.app.turnosapp.Model.AgendaPaciente;
import com.app.turnosapp.Model.ColaEsperaPaciente;
import com.app.turnosapp.Model.Turno;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AgendaPacienteService {

    @GET("AgendaPacientes/Pacientes/{idPaciente}/TurnosPendientes")
    Call<List<Turno>> getTurnosPaciente(@Path("idPaciente") Long idPaciente);

    @PUT("AgendaPacientes/{idAgendaPaciente}/AnularTurno")
    Call<Turno> anularTurno(@Path("idAgendaPaciente") Long idAgendaPaciente);

    @PUT("AgendaPacientes/{idAgendaPaciente}/ConfirmarTurno")
    Call<Turno> confirmarTurno(@Path("idAgendaPaciente") Long idAgendaPaciente);

    @POST("AgendaPacientes")
    Call<AgendaPaciente> crearAgendaPaciente(@Body AgendaPaciente agendaPaciente);

    @POST("AgendaPacientes/ColaEspera")
    Call<AgendaPaciente> agregarAColaDeEspera(@Body ColaEsperaPaciente colaEspera);
}
