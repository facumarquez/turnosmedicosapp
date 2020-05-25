package com.app.turnosapp.Interface;
import com.app.turnosapp.Model.Especialidad;
import com.app.turnosapp.Model.Medico;
import com.app.turnosapp.Model.Turno;
import com.app.turnosapp.Model.Usuario;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TurnosAPI {
    //Para pruebas
    @GET("posts")
    Call<List<Turno>> getTurnos();

    //TurnosAPI
    @GET("Usuarios/{username}/{password}/{tipo}")
    Call<Usuario> loginUser(@Path("username") String username, @Path("password") String password,@Path("tipo") String tipo);

    @GET("Especialidades")
    Call<List<Especialidad>> getEspecialidades();

    @GET("Especialidades/{especialidad}/Medicos")
    Call<List<Medico>> getMedicosPorEspecialidad(@Path("especialidad") String especialidad);

    @GET("AgendaPacientes/Pacientes/{idPaciente}/TurnosPendientes")
    Call<List<Turno>> getTurnosPaciente(@Path("idPaciente") String idPaciente);
}
