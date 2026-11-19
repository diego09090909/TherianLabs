package cl.therionlabs.prospectos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "prospecto",
        uniqueConstraints = @UniqueConstraint(name = "uk_prospecto_evento_rut", columnNames = {"evento_id", "rut"}))
public class Prospecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // >> QUE HACE: Marca la clase como tabla "prospecto". El id lo genera MySQL solo (autoincrement).
    // >> QUE HACE: La restriccion unica evita el mismo RUT dos veces en el mismo evento.

    @NotNull
    @Column(name = "evento_id")
    private Long eventoId;

    private Long captadorId;
    // >> QUE HACE: eventoId = a que evento pertenece. captadorId = quien lo registro (puede ir vacio por ahora).

    @NotBlank
    private String rut;

    @NotBlank
    private String nombres;

    @NotBlank
    private String apellidos;
    // >> QUE HACE: Datos personales obligatorios. @NotBlank rechaza vacios (da 400 si faltan).

    private String sexo;
    private String fechaNacimiento;
    private String comunaResidencia;
    // >> QUE HACE: Datos opcionales de la ficha. fechaNacimiento va como texto "2008-05-21" para partir simple.

    @NotBlank
    private String celular;

    @NotBlank
    @Email
    private String email;
    // >> QUE HACE: Contacto. @Email revisa que el correo tenga formato valido.

    private Boolean autorizaContacto = false;
    private Long fechaCaptura;
    // >> QUE HACE: autorizaContacto = el alumno acepto que lo llamen. fechaCaptura = momento en que se tomo en la app (milisegundos).

    public Prospecto() {}
    // >> QUE HACE: Constructor vacio, JPA lo necesita para armar objetos desde la base.

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public Long getCaptadorId() { return captadorId; }
    public void setCaptadorId(Long captadorId) { this.captadorId = captadorId; }
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
    public Boolean getAutorizaContacto() { return autorizaContacto; }
    public void setAutorizaContacto(Boolean autorizaContacto) { this.autorizaContacto = autorizaContacto; }
    public Long getFechaCaptura() { return fechaCaptura; }
    public void setFechaCaptura(Long fechaCaptura) { this.fechaCaptura = fechaCaptura; }
    // >> QUE HACE: Getters y setters: la forma en que Spring lee y escribe cada campo (JSON <-> objeto).
}
