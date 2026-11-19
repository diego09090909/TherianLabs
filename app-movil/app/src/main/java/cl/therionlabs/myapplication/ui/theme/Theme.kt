package cl.therionlabs.myapplication.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AzulElectric,      // Color de los botones
    secondary = AzulGlaciar,    // Color secundario / destacados
    background = Negro,          // Fondo general en modo oscuro
    surface = Negro,
    onPrimary = Blanco,          // Texto dentro de botones
    onBackground = Blanco,       // Texto general sobre fondo oscuro
    onSurface = Blanco
)

private val LightColorScheme = lightColorScheme(
    primary = AzulElectric,
    secondary = AzulGlaciar,
    background = Blanco,
    surface = Blanco,
    onPrimary = Blanco,
    onBackground = Negro,
    onSurface = Negro
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}