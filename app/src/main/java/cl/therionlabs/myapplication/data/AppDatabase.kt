package cl.therionlabs.myapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CaptadorEntity::class,
        Prospecto::class,
        CarreraEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun captadorDao(): CaptadorDao
    abstract fun prospectoDao(): ProspectoDao
    abstract fun carreraDao(): CarreraDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `prospectos` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `eventoId` TEXT NOT NULL,
                        `nombreCompleto` TEXT NOT NULL,
                        `rut` TEXT NOT NULL,
                        `correoCifrado` TEXT NOT NULL,
                        `telefonoCifrado` TEXT NOT NULL,
                        `colegio` TEXT NOT NULL,
                        `nivelEducativo` TEXT NOT NULL,
                        `carrera` TEXT NOT NULL,
                        `modalidad` TEXT NOT NULL,
                        `observaciones` TEXT NOT NULL,
                        `fechaCaptura` INTEGER NOT NULL,
                        `estado` TEXT NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS
                    `index_prospectos_rut_eventoId`
                    ON `prospectos` (`rut`, `eventoId`)
                    """.trimIndent()
                )
            }
        }

        // Migración adaptada a los campos de CarreraEntity (id, nombre, escuela)
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `carreras` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `nombre` TEXT NOT NULL,
                        `escuela` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun obtenerInstancia(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "captadores_db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                scope.launch(Dispatchers.IO) {
                                    val carreras = listOf(
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
                                        CarreraEntity(nombre = "Técnico en Turismo y Hospitalidad", escuela = "Escuela de Turismo y Hospitalidad")
                                    )
                                    database.carreraDao().insertarCarreras(carreras)
                                }
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}