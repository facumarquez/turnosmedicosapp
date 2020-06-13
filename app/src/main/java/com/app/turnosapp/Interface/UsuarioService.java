package com.app.turnosapp.Interface;

import com.app.turnosapp.Model.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioService {

    //TurnosAPI
    @GET("Usuarios/{username}/{password}/{tipo}")
    Call<Usuario> loginUser(@Path("username") String username, @Path("password") String password,@Path("tipo") String tipo);

    @GET("Usuarios/{idUsuario}")
    Call<Usuario> getUsuarioPorID(@Path("idUsuario") long idUsuario);

    @PUT("Usuarios/{idUsuario}")
    Call<Usuario> actualizarUsuario(@Path("idUsuario") long idUsuario, @Body Usuario usuario);
}
