package com.sgn.util;

import java.math.BigDecimal;


public final class NotaReglas {

    private NotaReglas() {}


    public static void validarVal(BigDecimal val) {
        if (val == null || val.compareTo(BigDecimal.ONE) < 0 || val.compareTo(BigDecimal.TEN) > 0) {
            throw new IllegalArgumentException("La nota debe estar entre 1 y 10");
        }
        if (val.scale() > 2) {
            throw new IllegalArgumentException("Máximo 2 decimales");
        }
    }


    public static boolean enProceso(BigDecimal val) {
        return val != null && val.compareTo(BigDecimal.valueOf(6)) < 0;
    }


    public static String normalizarIdent(String ident) {
        if (ident == null) return "";
        String t = ident.trim();
        String digits = t.replaceAll("[^0-9]", "");
        return digits.length() >= 10 ? digits : t.toLowerCase();
    }


    public static void validarCuil(String cuil) {
        if (cuil == null || cuil.isBlank()) {
            throw new IllegalArgumentException("CUIL/CUIT obligatorio");
        }
        String d = cuil.replaceAll("[^0-9]", "");
        if (d.length() != 11) {
            throw new IllegalArgumentException("CUIL/CUIT inválido: debe tener 11 dígitos");
        }
    }
}
