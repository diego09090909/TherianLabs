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
    version = 4,
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

        // Formulario del cupón de admisión: más datos del alumno y 3 carreras.
        // Los registros que ya estaban guardados quedan con esos campos vacíos.
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                listOf(
                    "carrera2", "carrera3", "sexo", "fechaNacimiento", "comunaResidencia",
                    "comunaColegio", "jornada", "sedes", "nivelInteres", "afirmacion"
                ).forEach { columna ->
                    db.execSQL("ALTER TABLE `prospectos` ADD COLUMN `$columna` TEXT NOT NULL DEFAULT ''")
                }
                db.execSQL("ALTER TABLE `prospectos` ADD COLUMN `autorizaDatos` INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Deja la tabla "carreras" completa. Solo agrega las que faltan, así funciona en una
        // instalación nueva y también en un teléfono que viene de una versión anterior.
        private fun sembrarCarrerasFaltantes(db: SupportSQLiteDatabase) {
            if (db.isReadOnly) return
            val cursor = db.query("SELECT COUNT(*) FROM carreras")
            val existentes = if (cursor.moveToFirst()) cursor.getInt(0) else 0
            cursor.close()
            if (existentes >= CarrerasSeed.todas.size) return

            db.beginTransaction()
            try {
                CarrerasSeed.todas.forEach { c ->
                    db.execSQL(
                        "INSERT INTO carreras (nombre, escuela) SELECT ?, ? " +
                                "WHERE NOT EXISTS (SELECT 1 FROM carreras WHERE nombre = ? AND escuela = ?)",
                        arrayOf<Any>(c.nombre, c.escuela, c.nombre, c.escuela)
                    )
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }

        fun obtenerInstancia(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "captadores_db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            sembrarCarrerasFaltantes(db)
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}