package com.app.turnosapp.Helpers;

import com.app.turnosapp.Model.AgendaMedico;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

        int mes = calendario.get(Calendar.MONTH) + 1;

        if(agendaMedico.getMes() > mes) {
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

}
