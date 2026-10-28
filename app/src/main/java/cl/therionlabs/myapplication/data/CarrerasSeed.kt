package cl.therionlabs.myapplication.data

// Catálogo de carreras según el cupón de admisión 2027 de Duoc UC.
// AppDatabase lo usa para dejar la tabla "carreras" completa (solo agrega las que faltan).
object CarrerasSeed {

    const val ESCUELA_ONLINE = "Duoc Online"

    val todas: List<CarreraEntity> = listOf(
        // Escuela de Administración y Negocios
        CarreraEntity(nombre = "Auditoría", escuela = "Escuela de Administración y Negocios"),
        CarreraEntity(nombre = "Ingeniería en Administración Mención Finanzas", escuela = "Escuela de Administración y Negocios"),
        CarreraEntity(nombre = "Ingeniería en Administración Mención Gestión de Personas", escuela = "Escuela de Administración y Negocios"),
        CarreraEntity(nombre = "Ingeniería en Administración Mención Innovación y Emprendimiento", escuela = "Escuela de Administración y Negocios"),
        CarreraEntity(nombre = "Ingeniería en Comercio Exterior", escuela = "Escuela de Administración y Negocios"),
        CarreraEntity(nombre = "Ingeniería en Gestión Logística", escuela = "Escuela de Administración y Negocios"),
        CarreraEntity(nombre = "Ingeniería en Marketing Digital", escuela = "Escuela de Administración y Negocios"),
        CarreraEntity(nombre = "Comercio Exterior", escuela = "Escuela de Administración y Negocios"),
        CarreraEntity(nombre = "Contabilidad General Mención Legislación Tributaria", escuela = "Escuela de Administración y Negocios"),
        CarreraEntity(nombre = "Técnico en Administración", escuela = "Escuela de Administración y Negocios"),
        CarreraEntity(nombre = "Técnico en Gestión Logística", escuela = "Escuela de Administración y Negocios"),
        // Escuela de Comunicación
        CarreraEntity(nombre = "Animación Digital", escuela = "Escuela de Comunicación"),
        CarreraEntity(nombre = "Comunicación Audiovisual", escuela = "Escuela de Comunicación"),
        CarreraEntity(nombre = "Ingeniería en Sonido", escuela = "Escuela de Comunicación"),
        CarreraEntity(nombre = "Publicidad", escuela = "Escuela de Comunicación"),
        CarreraEntity(nombre = "Relaciones Públicas y Comunicación Organizacional", escuela = "Escuela de Comunicación"),
        CarreraEntity(nombre = "Técnico Audiovisual", escuela = "Escuela de Comunicación"),
        CarreraEntity(nombre = "Tecnología en Sonido e Iluminación", escuela = "Escuela de Comunicación"),
        // Escuela de Construcción
        CarreraEntity(nombre = "Ingeniería en Construcción", escuela = "Escuela de Construcción"),
        CarreraEntity(nombre = "Ingeniería en Prevención de Riesgos", escuela = "Escuela de Construcción"),
        CarreraEntity(nombre = "Técnico Modelador Proyectista", escuela = "Escuela de Construcción"),
        CarreraEntity(nombre = "Restauración de Bienes Patrimoniales", escuela = "Escuela de Construcción"),
        CarreraEntity(nombre = "Técnico en Construcción", escuela = "Escuela de Construcción"),
        CarreraEntity(nombre = "Técnico en Prevención de Riesgos", escuela = "Escuela de Construcción"),
        CarreraEntity(nombre = "Técnico Topógrafo Geomático", escuela = "Escuela de Construcción"),
        // Escuela de Diseño
        CarreraEntity(nombre = "Diseño de Ambientes", escuela = "Escuela de Diseño"),
        CarreraEntity(nombre = "Diseño de Vestuario", escuela = "Escuela de Diseño"),
        CarreraEntity(nombre = "Diseño Gráfico", escuela = "Escuela de Diseño"),
        CarreraEntity(nombre = "Diseño Industrial e Innovación en Productos", escuela = "Escuela de Diseño"),
        CarreraEntity(nombre = "Ilustración para Contextos Globales", escuela = "Escuela de Diseño"),
        // Escuela de Gastronomía
        CarreraEntity(nombre = "Gastronomía Internacional", escuela = "Escuela de Gastronomía"),
        CarreraEntity(nombre = "Gastronomía", escuela = "Escuela de Gastronomía"),
        // Escuela de Informática y Telecomunicaciones
        CarreraEntity(nombre = "Ingeniería en Informática", escuela = "Escuela de Informática y Telecomunicaciones"),
        CarreraEntity(nombre = "Ingeniería en Infraestructura Tecnológica", escuela = "Escuela de Informática y Telecomunicaciones"),
        CarreraEntity(nombre = "Ingeniería en Redes y Telecomunicaciones", escuela = "Escuela de Informática y Telecomunicaciones"),
        CarreraEntity(nombre = "Analista Programador", escuela = "Escuela de Informática y Telecomunicaciones"),
        CarreraEntity(nombre = "Técnico en Redes y Telecomunicaciones", escuela = "Escuela de Informática y Telecomunicaciones"),
        // Escuela de Salud y Bienestar
        CarreraEntity(nombre = "Informática Biomédica", escuela = "Escuela de Salud y Bienestar"),
        CarreraEntity(nombre = "Preparador Físico", escuela = "Escuela de Salud y Bienestar"),
        CarreraEntity(nombre = "Técnico de Enfermería", escuela = "Escuela de Salud y Bienestar"),
        CarreraEntity(nombre = "Técnico en Laboratorio Clínico, Banco de Sangre e Imagenología", escuela = "Escuela de Salud y Bienestar"),
        CarreraEntity(nombre = "Técnico en Odontología", escuela = "Escuela de Salud y Bienestar"),
        CarreraEntity(nombre = "Técnico en Química y Farmacia", escuela = "Escuela de Salud y Bienestar"),
        // Escuela de Ingeniería y Recursos Naturales
        CarreraEntity(nombre = "Ingeniería Agrícola", escuela = "Escuela de Ingeniería y Recursos Naturales"),
        CarreraEntity(nombre = "Ingeniería en Electricidad y Automatización Industrial", escuela = "Escuela de Ingeniería y Recursos Naturales"),
        CarreraEntity(nombre = "Ingeniería en Mantenimiento Industrial", escuela = "Escuela de Ingeniería y Recursos Naturales"),
        CarreraEntity(nombre = "Ingeniería en Maquinaria y Vehículos Pesados", escuela = "Escuela de Ingeniería y Recursos Naturales"),
        CarreraEntity(nombre = "Ingeniería en Mecánica Automotriz y Autotrónica", escuela = "Escuela de Ingeniería y Recursos Naturales"),
        CarreraEntity(nombre = "Ingeniería en Recursos Naturales y Sostenibilidad", escuela = "Escuela de Ingeniería y Recursos Naturales"),
        CarreraEntity(nombre = "Técnico en Calidad de Alimentos", escuela = "Escuela de Ingeniería y Recursos Naturales"),
        CarreraEntity(nombre = "Técnico en Desabolladura y Pintura Automotriz", escuela = "Escuela de Ingeniería y Recursos Naturales"),
        CarreraEntity(nombre = "Técnico en Electricidad y Automatización Industrial", escuela = "Escuela de Ingeniería y Recursos Naturales"),
        CarreraEntity(nombre = "Técnico en Electricidad y Energías Renovables", escuela = "Escuela de Ingeniería y Recursos Naturales"),
        CarreraEntity(nombre = "Técnico en Geología", escuela = "Escuela de Ingeniería y Recursos Naturales"),
        CarreraEntity(nombre = "Técnico en Mantenimiento Industrial", escuela = "Escuela de Ingeniería y Recursos Naturales"),
        CarreraEntity(nombre = "Técnico en Maquinaria y Vehículos Pesados", escuela = "Escuela de Ingeniería y Recursos Naturales"),
        CarreraEntity(nombre = "Técnico en Mecánica Automotriz y Autotrónica", escuela = "Escuela de Ingeniería y Recursos Naturales"),
        CarreraEntity(nombre = "Técnico en Operación y Supervisión de Procesos Mineros", escuela = "Escuela de Ingeniería y Recursos Naturales"),
        CarreraEntity(nombre = "Técnico Veterinario y Pecuario", escuela = "Escuela de Ingeniería y Recursos Naturales"),
        // Escuela de Turismo y Hospitalidad
        CarreraEntity(nombre = "Administración en Turismo y Hospitalidad", escuela = "Escuela de Turismo y Hospitalidad"),
        CarreraEntity(nombre = "Técnico en Turismo y Hospitalidad", escuela = "Escuela de Turismo y Hospitalidad"),
        // Duoc Online
        CarreraEntity(nombre = "Administración Pública", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Auditoría y Análisis de Negocios", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Ingeniería en Administración de Seguridad Privada", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Ingeniería en Control de Gestión", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Ingeniería en Gestión de Personas", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Ingeniería en Marketing Digital", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Relaciones Públicas y Comunicación Organizacional", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Ingeniería en Prevención de Riesgos", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Ingeniería en Ciberseguridad", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Ingeniería en Ciencia de Datos", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Ingeniería en Desarrollo de Software", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Ingeniería en Automatización y Control Industrial", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Ingeniería en Minas", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Ingeniería Industrial", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Administración de Empresas", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Contabilidad Tributaria", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Técnico en Operaciones Logísticas", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Técnico en Trabajo Social", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Técnico en Prevención de Riesgos Laborales", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Desarrollo y Diseño Web", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Analista Programador Computacional", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Desarrollo de Aplicaciones", escuela = ESCUELA_ONLINE),
        CarreraEntity(nombre = "Técnico en Control y Monitoreo Remoto de Procesos Mineros", escuela = ESCUELA_ONLINE)
    )
}