package com.app.turnosapp.Helpers;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;

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
}
