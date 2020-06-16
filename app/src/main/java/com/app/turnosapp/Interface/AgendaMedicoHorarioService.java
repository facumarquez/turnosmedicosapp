package com.app.turnosapp.Interface;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface AgendaMedicoHorarioService {

    @DELETE("AgendaMedicoHorarios/{idAgendaMedicoHorario}")
    Call<Void> deleteAgendaMedicoHorario(@Path("idAgendaMedicoHorario") Long idAgendaMedicoHorario);
}
