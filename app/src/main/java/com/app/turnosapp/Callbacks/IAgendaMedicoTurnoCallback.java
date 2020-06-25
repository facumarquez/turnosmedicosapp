package com.app.turnosapp.Callbacks;

import com.app.turnosapp.Model.AgendaMedicoTurno;

import java.util.List;

public interface IAgendaMedicoTurnoCallback {
    void getTurnosAgendaMedico(List<AgendaMedicoTurno> turnosAgenda);
}
