package com.app.turnosapp.Helpers;

import org.apache.commons.lang3.StringUtils;

public class StringHelper {

    public static String rellenarConCeros(String cadena, int digitos) {

        return StringUtils.leftPad(cadena, digitos, "0");
    }

    public static String convertirFechaAFormato_dd_mm_aaaa(String fecha) {

        return fecha.substring(6,8) + "/" + fecha.substring(4,6) + "/" + fecha.substring(0,2);
    }
}
