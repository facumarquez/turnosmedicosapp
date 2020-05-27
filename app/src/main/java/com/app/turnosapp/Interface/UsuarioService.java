package com.app.turnosapp.Interface;

import com.app.turnosapp.Model.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioService {

    @GET("Usuarios/{idUsuario}")
    Call<Usuario> getUsuarioPorID(@Path("idUsuario") long idUsuario);

    @GET("Usuarios/NombreUsuario/{nombreUsuario}")
    Call<Usuario> getUsuarioPorNombre(@Path("nombreUsuario") String nombreUsuario);

    @PUT("Usuarios/{idUsuario}")
    Call<Usuario> actualizarUsuario(@Path("idUsuario") long idUsuario, @Body Usuario usuario);
}
