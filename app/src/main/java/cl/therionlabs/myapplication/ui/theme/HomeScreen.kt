package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.therionlabs.myapplication.R
import cl.therionlabs.myapplication.navigation.Screen
import cl.therionlabs.myapplication.ui.theme.Blanco
import cl.therionlabs.myapplication.ui.theme.MyApplicationTheme
import cl.therionlabs.myapplication.viewmodels.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel) { // <- Aquí se agrega el parámetro viewModel
    Box {
        Image(
            painter = painterResource(R.drawable.images),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))

            LogoApp()

            Button(
                onClick = {}
            ) {
                Text("Iniciar Sesión")
            }

            // Al hacer clic, le avisa al ViewModel que navegue a Register
            Button(
                onClick = { viewModel.navigateTo(Screen.Register) }
            ) {
                Text("Registrarse")
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Todos los derechos reservados",
                color = Blanco,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Composable
fun LogoApp() {
    Image(
        painter = painterResource(R.drawable.logo),
        contentDescription = "Logo App",
        modifier = Modifier.padding(40.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MyApplicationTheme {
        HomeScreen(viewModel = viewModel())
    }
}