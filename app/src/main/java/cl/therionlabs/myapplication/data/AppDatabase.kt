package cl.therionlabs.myapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        CaptadorEntity::class,
        Prospecto::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun captadorDao(): CaptadorDao

    abstract fun prospectoDao(): ProspectoDao

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

        fun obtenerInstancia(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "captadores_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
