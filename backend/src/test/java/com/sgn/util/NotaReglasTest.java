package com.sgn.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class NotaReglasTest {

    @Test
    void notaMenorASeisEsEnProceso() {
        assertTrue(NotaReglas.enProceso(new BigDecimal("5.99")));
        assertFalse(NotaReglas.enProceso(new BigDecimal("6.00")));
        assertFalse(NotaReglas.enProceso(new BigDecimal("10")));
    }

    @Test
    void validaRangoYDecimales() {
        assertDoesNotThrow(() -> NotaReglas.validarVal(new BigDecimal("7.25")));
        assertThrows(IllegalArgumentException.class, () -> NotaReglas.validarVal(new BigDecimal("0.5")));
        assertThrows(IllegalArgumentException.class, () -> NotaReglas.validarVal(new BigDecimal("10.01")));
        assertThrows(IllegalArgumentException.class, () -> NotaReglas.validarVal(new BigDecimal("7.123")));
    }

    @Test
    void cuilDebeTenerOnceDigitos() {
        assertDoesNotThrow(() -> NotaReglas.validarCuil("20-11111111-2"));
        assertThrows(IllegalArgumentException.class, () -> NotaReglas.validarCuil("123"));
        assertThrows(IllegalArgumentException.class, () -> NotaReglas.validarCuil(""));
    }

    @Test
    void normalizaIdentificadorNumerico() {
        assertEquals("20111111112", NotaReglas.normalizarIdent("20-11111111-2"));
        assertEquals("admin@et35.local", NotaReglas.normalizarIdent("Admin@et35.local"));
    }
}
