package com.app.turnosapp.Interface;

import com.app.turnosapp.Model.Especialidad;
import com.app.turnosapp.Model.Medico;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EspecialidadService {

    @GET("Especialidades")
    Call<List<Especialidad>> getEspecialidades();

    @GET("Especialidades/{especialidad}/Medicos")
    Call<List<Medico>> getMedicosPorEspecialidad(@Path("especialidad") Long especialidad);
}
