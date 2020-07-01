package com.app.turnosapp.Helpers;

import com.app.turnosapp.Model.AgendaMedico;
import com.app.turnosapp.Model.AgendaMedicoHorario;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class StringHelper {

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

        int mesActual = calendario.get(Calendar.MONTH);

        if(agendaMedico.getMes() > mesActual) {
            return true;
        }

        calendario.add((GregorianCalendar.DAY_OF_MONTH), 7);

        Date fechaConSieteDiasAgregados = calendario.getTime();
        String fechaConSieteDiasAgregadosFormateada = convertirFechaAFormatoJapones(fechaConSieteDiasAgregados);

        if(fecha.compareTo(fechaConSieteDiasAgregadosFormateada) >= 0) {
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
        if (horarios == null || horarios.size() == 0){
            return false;
        }
        for (AgendaMedicoHorario horario:horarios) {
            String horaDesde = horario.getHoraDesde();
            String horaHasta = horario.getHoraHasta();

            if ((hasta.compareTo(horaDesde) > 0) && !desde.equals(horaHasta)){
                rangoSuperpuesto = true;
                break;
            }
        }

        return rangoSuperpuesto;
    }

}
