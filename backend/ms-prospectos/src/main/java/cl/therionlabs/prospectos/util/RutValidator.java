package cl.therionlabs.prospectos.util;

public final class RutValidator {

    private RutValidator() {
    }

    public static String normalizar(String rut) {
        if (rut == null) {
            return null;
        }
        String limpio = rut.replace(".", "").replace(" ", "").toUpperCase();
        if (limpio.length() >= 2 && !limpio.contains("-")) {
            limpio = limpio.substring(0, limpio.length() - 1) + "-" + limpio.charAt(limpio.length() - 1);
        }
        return limpio;
    }

    public static boolean esValido(String rut) {
        String normalizado = normalizar(rut);
        if (normalizado == null || !normalizado.matches("\\d{7,8}-[0-9K]")) {
            return false;
        }
        String cuerpo = normalizado.substring(0, normalizado.length() - 2);
        char digito = normalizado.charAt(normalizado.length() - 1);
        return calcularDigito(cuerpo) == digito;
    }

    private static char calcularDigito(String cuerpo) {
        int suma = 0;
        int factor = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cuerpo.charAt(i)) * factor;
            factor = factor == 7 ? 2 : factor + 1;
        }
        int resto = 11 - (suma % 11);
        if (resto == 11) {
            return '0';
        }
        if (resto == 10) {
            return 'K';
        }
        return (char) ('0' + resto);
    }
}
