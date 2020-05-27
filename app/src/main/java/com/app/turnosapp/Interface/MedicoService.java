package com.app.turnosapp.Interface;

import com.app.turnosapp.Model.Especialidad;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MedicoService {
    @GET("Medicos/{idMedico}/especialidades")
    Call<List<Especialidad>> getEspecialidadesPorMedico(@Path("idMedico") long idMedico);
}
