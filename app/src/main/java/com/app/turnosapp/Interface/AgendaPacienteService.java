package com.app.turnosapp.Interface;

import com.app.turnosapp.Model.Turno;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AgendaPacienteService {

    @GET("AgendaPacientes/Pacientes/{idPaciente}/TurnosPendientes")
    Call<List<Turno>> getTurnosPaciente(@Path("idPaciente") Long idPaciente);

    @PUT("AgendaPacientes/{idPaciente}/AnularTurno")
    Call<Turno> anularTurno(@Path("idPaciente") Long idPaciente);

    @PUT("AgendaPacientes/{idPaciente}/ConfirmarTurno")
    Call<Turno> confirmarTurno(@Path("idPaciente") Long idPaciente);
}
