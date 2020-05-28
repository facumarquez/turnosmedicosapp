package com.app.turnosapp.Interface;

import com.app.turnosapp.Model.Especialidad;
import com.app.turnosapp.Model.Medico;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MedicoService {
    @GET("Medicos/{idMedico}/especialidades")
    Call<List<Especialidad>> getEspecialidadesPorMedico(@Path("idMedico") long idMedico);

    @GET("Medicos/NombreUsuario/{nombreUsuario}")
    Call<Medico> getMedicoPorNombre(@Path("nombreUsuario") String nombreUsuario);
}
