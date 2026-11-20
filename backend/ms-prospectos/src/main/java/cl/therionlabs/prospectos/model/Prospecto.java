package cl.therionlabs.prospectos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "prospecto",
        uniqueConstraints = @UniqueConstraint(name = "uk_prospecto_evento_rut", columnNames = {"evento_id", "rut"}))
public class Prospecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "evento_id")
    private String eventoId;

    private String captadorId;
    private String rut;
    private String nombres;
    private String apellidos;

    private String sexo;
    private String fechaNacimiento;
    private String comunaResidencia;
    private String celular;
    private String email;

    private String actividad;
    private String actividadOtra;
    private String establecimiento;
    private String comunaEstablecimiento;
    private String jornada;
    private String sedes;
    private String nivelInteres;
    private String afirmacion;
    private String carrera1;

    private String carrera2;
    private String carrera3;

    private Boolean autorizaContacto = false;
    private Long fechaCaptura;

    public Prospecto() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }
    public String getCaptadorId() { return captadorId; }
    public void setCaptadorId(String captadorId) { this.captadorId = captadorId; }
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getComunaResidencia() { return comunaResidencia; }
    public void setComunaResidencia(String comunaResidencia) { this.comunaResidencia = comunaResidencia; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getActividad() { return actividad; }
    public void setActividad(String actividad) { this.actividad = actividad; }
    public String getActividadOtra() { return actividadOtra; }
    public void setActividadOtra(String actividadOtra) { this.actividadOtra = actividadOtra; }
    public String getEstablecimiento() { return establecimiento; }
    public void setEstablecimiento(String establecimiento) { this.establecimiento = establecimiento; }
    public String getComunaEstablecimiento() { return comunaEstablecimiento; }
    public void setComunaEstablecimiento(String comunaEstablecimiento) { this.comunaEstablecimiento = comunaEstablecimiento; }
    public String getJornada() { return jornada; }
    public void setJornada(String jornada) { this.jornada = jornada; }
    public String getSedes() { return sedes; }
    public void setSedes(String sedes) { this.sedes = sedes; }
    public String getNivelInteres() { return nivelInteres; }
    public void setNivelInteres(String nivelInteres) { this.nivelInteres = nivelInteres; }
    public String getAfirmacion() { return afirmacion; }
    public void setAfirmacion(String afirmacion) { this.afirmacion = afirmacion; }
    public String getCarrera1() { return carrera1; }
    public void setCarrera1(String carrera1) { this.carrera1 = carrera1; }
    public String getCarrera2() { return carrera2; }
    public void setCarrera2(String carrera2) { this.carrera2 = carrera2; }
    public String getCarrera3() { return carrera3; }
    public void setCarrera3(String carrera3) { this.carrera3 = carrera3; }
    public Boolean getAutorizaContacto() { return autorizaContacto; }
    public void setAutorizaContacto(Boolean autorizaContacto) { this.autorizaContacto = autorizaContacto; }
    public Long getFechaCaptura() { return fechaCaptura; }
    public void setFechaCaptura(Long fechaCaptura) { this.fechaCaptura = fechaCaptura; }
}
