package com.app.turnosapp.Interface;

import com.app.turnosapp.Model.Paciente;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PacienteService {
    @GET("Pacientes/{idPaciente}")
    Call<Paciente> getPaciente(@Path("idPaciente") long idPaciente);

    @GET("Pacientes/{idPaciente}/pagoAlDia/")
    Call<Boolean> pacienteAlDia(@Path("idPaciente") long idPaciente);

}
