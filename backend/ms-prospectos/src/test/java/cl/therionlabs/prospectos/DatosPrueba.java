package cl.therionlabs.prospectos;

import cl.therionlabs.prospectos.model.Prospecto;

public final class DatosPrueba {

    private DatosPrueba() {
    }

    public static Prospecto prospecto(String eventoId, String rut) {
        Prospecto p = new Prospecto();
        p.setEventoId(eventoId);
        p.setCaptadorId("captador-1");
        p.setRut(rut);
        p.setNombres("Camila Andrea");
        p.setApellidos("Rojas Soto");
        p.setSexo("Femenino");
        p.setFechaNacimiento("30-05-2008");
        p.setComunaResidencia("Maipú");
        p.setCelular("912345678");
        p.setEmail("camila@correo.cl");
        p.setActividad("4° medio");
        p.setEstablecimiento("Liceo A-90");
        p.setComunaEstablecimiento("Maipú");
        p.setJornada("Diurna");
        p.setSedes("Maipú|Plaza Oeste");
        p.setNivelInteres("Alto");
        p.setAfirmacion("2");
        p.setCarrera1("Analista Programador");
        p.setCarrera2("Ingeniería en Informática");
        p.setCarrera3("Ingeniería en Ciberseguridad");
        p.setAutorizaContacto(true);
        p.setFechaCaptura(1763600000000L);
        return p;
    }
}
