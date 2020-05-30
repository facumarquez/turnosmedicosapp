package com.app.turnosapp.Callbacks;

import com.app.turnosapp.Model.AgendaMedicoFecha;

import java.util.List;

public interface IAgendaMedicoFechaCallback {
    void getFechasAgendaMedico(List<AgendaMedicoFecha> fechasAgenda);
}
