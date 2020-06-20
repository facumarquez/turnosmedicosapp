package com.app.turnosapp.Interface;

import com.app.turnosapp.Model.AgendaMedicoFecha;
import com.app.turnosapp.Model.AgendaMedicoHorario;
import com.app.turnosapp.Model.AgendaMedicoTurno;
import com.app.turnosapp.Model.Medico;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AgendaMedicoFechaService {

    @POST("AgendaMedicoFechas")
    Call<List<AgendaMedicoFecha>> crearFechasAgendaMedico(@Body List<AgendaMedicoFecha> fechasAgenda);

    @GET("AgendaMedicoFechas/{idEspecialidad}/{idMedico}/{mes}/{anio}/{horario}")
    Call<List<AgendaMedicoFecha>> getAgendaMedicoFechasByEspecialidad_Medico_Periodo_Horario(@Path("idEspecialidad") Long idEspecialidad,
                                                                                                @Path("idMedico") Long idMedico,
                                                                                                @Path("mes") int mes,
                                                                                                @Path("anio") int anio,
                                                                                                @Path("horario") String horario);

    @GET("AgendaMedicoFechas/{idEspecialidad}/{mes}/{anio}/{horario}")
    Call<List<AgendaMedicoFecha>> getAgendaMedicoFechasByEspecialidad_Periodo_Horario(@Path("idEspecialidad") Long idEspecialidad,
                                                                                  @Path("mes") int mes,
                                                                                  @Path("anio") int anio,
                                                                                  @Path("horario") String horario);


    @GET("AgendaMedicoFechas/{fecha}/{horario}/Especialidad/{idEspecialidad}/MedicosDisponibles")
    Call<List<Medico>> getMedicosPorFechaDeAtencion_Especialidad_Horario(@Path("fecha") String fecha, @Path("horario") String horario,
                                                    @Path("idEspecialidad") long idEspecialidad);

    @GET("AgendaMedicoFechas/{idAgendaMedicoFecha}/{horario}/TurnosDisponibles")
    Call<List<AgendaMedicoTurno>> getTurnosDeUnaFechaYHorarioEspecifico(@Path("idAgendaMedicoFecha") Long idAgendaMedicoFecha,
                                                                                                    @Path("horario") String horario);

    @GET("AgendaMedicoFechas/{fecha}/Medicos/{idMedico}/TurnosDisponibles")
    Call<List<AgendaMedicoTurno>> getTurnosDeUnMedicoEspecifico(@Path("fecha") String fecha,
                                                     @Path("idMedico") long idMedico);


    @GET("AgendaMedicoFechas/AgendaMedicos/{idAgendaMedico}")
    Call<List<AgendaMedicoFecha>> getFechasPorAgendaMedico(@Path("idAgendaMedico") long idAgendaMedico);


    @POST("AgendaMedicoFechas/Horarios")
    Call<List<AgendaMedicoHorario>> obtenerHorariosDeFechas(@Body List<AgendaMedicoFecha> fechasAgenda);
}
