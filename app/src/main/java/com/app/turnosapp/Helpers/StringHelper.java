package com.app.turnosapp.Helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class StringHelper {

    public static String convertirFechaAFormatoJapones(Date fecha){
        return new SimpleDateFormat("yyMMdd").format(fecha);
    }

    public static String rellenarConCeros(String cadena, int digitos) {

        return StringUtils.leftPad(cadena, digitos, "0");
    }
}
