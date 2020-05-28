package com.app.turnosapp.Helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FechaHelper {

    public static String convertirFechaAFormatoJapones(Date fecha){
        return new SimpleDateFormat("yyMMdd").format(fecha);
    }
}
