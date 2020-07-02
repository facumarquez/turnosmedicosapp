package com.app.turnosapp.Helpers;

import com.app.turnosapp.Model.AgendaMedico;
import com.app.turnosapp.Model.AgendaMedicoHorario;
import com.app.turnosapp.Model.AgendaMedicoTurno;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

public class StringHelper {

    public static final int INTERVALO_TURNO = 15;

    public static String rellenarConCeros(String cadena, int digitos) {

        return StringUtils.leftPad(cadena, digitos, "0");
    }

    public static String convertirFechaAFormato_dd_mm_aaaa(String fecha) {

        return fecha.substring(6,8) + "/" + fecha.substring(4,6) + "/" + fecha.substring(0,4);
    }

    public static ArrayList<String> obtenerAniosAgendaMedico() {

        ArrayList<String> anios = new ArrayList<String>();

        Calendar calendario= Calendar.getInstance();
        int year= calendario.get(Calendar.YEAR);

        anios.add(String.valueOf(year));
        anios.add(String.valueOf(year + 1));

        return anios;
    }

    public static String convertirFechaAFormatoJapones(Date fecha) {

        return new SimpleDateFormat("yyyyMMdd").format(fecha);
    }


    public static boolean puedeModificarFechaAgenda(AgendaMedico agendaMedico, String fecha) {

        Calendar calendario = Calendar.getInstance();


        int mesActual = calendario.get(Calendar.MONTH) + 1;

        if(agendaMedico.getMes() > mesActual) {
            return true;
        }

        calendario.add((GregorianCalendar.DAY_OF_MONTH), 7);

        Date fechaConSieteDiasAgregados = calendario.getTime();
        String fechaConSieteDiasAgregadosFormateada = convertirFechaAFormatoJapones(fechaConSieteDiasAgregados);

        //TODO: antes >=
        if(fecha.compareTo(fechaConSieteDiasAgregadosFormateada) > 0) {
            return true;
        }else {
            return false;
        }
    }

    public static boolean mesCorrecto(String fecha, int mesAgenda) {

        int mesDeFecha = Integer.valueOf(fecha.substring(4,6));

        return mesDeFecha == mesAgenda;
    }

    public static boolean rangoSuperpuesto(String desde, String hasta, List<AgendaMedicoHorario> horarios) {

        boolean rangoSuperpuesto = false;
        List <AgendaMedicoTurno> turnos = new ArrayList<AgendaMedicoTurno>();
        List <String> turnosString = new ArrayList<String>();
        HashSet<String> turnosSinRepetir = new HashSet<String>();

        if (horarios == null || horarios.size() == 0){
            return false;
        }
        for (AgendaMedicoHorario horario:horarios) {
            turnos = new ArrayList<AgendaMedicoTurno>();

            turnos.addAll(generarTurnos(horario.getHoraDesde(),horario.getHoraHasta()));

            AgendaMedicoTurno turno = new AgendaMedicoTurno();
            turnos.addAll(generarTurnos(desde,hasta));

            for (AgendaMedicoTurno item: turnos) {
                turnosString.add(item.getTurnoDesde() + "-" + item.getTurnoHasta());
            }
            turnosSinRepetir.addAll(turnosString);
            rangoSuperpuesto = rangoSuperpuesto || turnosSinRepetir.size() != turnos.size();
        }

        return rangoSuperpuesto;

    }

    public static List<AgendaMedicoTurno> generarTurnos(String horaDesde,String horaHasta){

        List <AgendaMedicoTurno> turnos = new ArrayList<AgendaMedicoTurno>();

        String turnoDesde = horaDesde;

        AgendaMedicoTurno turno = generarTurno(turnoDesde);

        String turnoHasta = turno.getTurnoHasta();

        turnos.add(turno);

        while (turnoHasta.compareTo(horaHasta) < 0) {
            turnoDesde = turnoHasta;
            turno = generarTurno(turnoDesde);
            turnoHasta = turno.getTurnoHasta();
            turnos.add(turno);
        }
        return turnos;
    }

    private static AgendaMedicoTurno generarTurno(String turnoDesde) {

        GregorianCalendar calendario = (GregorianCalendar) GregorianCalendar.getInstance();
        calendario.set(Calendar.HOUR_OF_DAY, Integer.valueOf(turnoDesde.substring(0,2)));
        calendario.set(Calendar.MINUTE,Integer.valueOf(turnoDesde.substring(3,5)));
        calendario.add(GregorianCalendar.MINUTE, INTERVALO_TURNO);

        String turnoHasta = new SimpleDateFormat("HH:mm").format(calendario.getTime()).toString();
        AgendaMedicoTurno turno = new AgendaMedicoTurno();
        turno.setTurnoDesde(turnoDesde);
        turno.setTurnoHasta(turnoHasta);
        return turno;
    }
}
