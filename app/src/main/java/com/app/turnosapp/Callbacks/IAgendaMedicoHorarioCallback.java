package com.app.turnosapp.Callbacks;

import com.app.turnosapp.Model.AgendaMedicoHorario;

import java.util.List;

public interface IAgendaMedicoHorarioCallback {
    void getHorariosAgendaMedico(List<AgendaMedicoHorario> horariosAgenda);
}
