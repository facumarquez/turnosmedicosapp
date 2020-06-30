package com.app.turnosapp.Interface;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface AgendaMedicoTurnoService {

    @DELETE("AgendaMedicoTurnos/{idAgendaMedicoTurno}")
    Call<Void> deleteTurno(@Path("idAgendaMedicoTurno") Long idAgendaMedicoTurno);
}
