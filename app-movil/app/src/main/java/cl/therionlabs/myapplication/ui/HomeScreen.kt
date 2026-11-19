package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.therionlabs.myapplication.R
import cl.therionlabs.myapplication.navigation.Screen
import cl.therionlabs.myapplication.ui.theme.MyApplicationTheme
import cl.therionlabs.myapplication.viewmodels.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DuocNavy)
    ) {
        CirculosDecorativos()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(96.dp))

            LogoApp()

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Captación de prospectos",
                color = Color.White,
                fontSize = 36.sp,
                lineHeight = 42.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Registra a los estudiantes interesados en estudiar en Duoc UC, incluso sin conexión a internet.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DuocBotonAmarillo(
                    texto = "Iniciar sesión",
                    onClick = { viewModel.navigateTo(Screen.Login) }
                )
                DuocBotonContorno(
                    texto = "Registrarse como captador",
                    onClick = { viewModel.navigateTo(Screen.RegistroCaptador) },
                    color = Color.White
                )
            }

            Text(
                text = "Todos los derechos reservados",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 20.dp)
            )
        }
    }
}

@Composable
fun LogoApp() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo Duoc UC",
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun CirculosDecorativos() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawCircle(
            color = DuocYellow,
            radius = w * 0.43f,
            center = Offset(w * 0.88f, h * 0.05f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.18f),
            radius = w * 0.23f,
            center = Offset(w * 0.97f, h * 0.27f),
            style = Stroke(width = 1.5.dp.toPx())
        )
        drawCircle(
            color = DuocAzulAcento,
            radius = w * 0.13f,
            center = Offset(0f, h * 0.80f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MyApplicationTheme {
        HomeScreen(viewModel = viewModel())
    }
}
