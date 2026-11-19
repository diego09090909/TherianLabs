package cl.therionlabs.myapplication.ui

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors


@Composable
fun CamaraQr(
    activo: Boolean,
    onQrLeido: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val alLeer by rememberUpdatedState(onQrLeido)
    val estaActivo by rememberUpdatedState(activo)

    val vistaPrevia = remember {
        PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
    }

    DisposableEffect(lifecycleOwner) {
        var liberado = false
        val ejecutor = Executors.newSingleThreadExecutor()
        val escaner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )
        val futuro = ProcessCameraProvider.getInstance(context)

        futuro.addListener({
            if (liberado) return@addListener
            val proveedor = futuro.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = vistaPrevia.surfaceProvider
            }

            val analisis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analisis.setAnalyzer(ejecutor) { proxy ->
                if (!estaActivo) {
                    proxy.close()
                    return@setAnalyzer
                }
                val bitmap = try {
                    proxy.toBitmap()
                } catch (e: Exception) {
                    null
                }
                if (bitmap == null) {
                    proxy.close()
                    return@setAnalyzer
                }
                val imagen = InputImage.fromBitmap(bitmap, proxy.imageInfo.rotationDegrees)
                escaner.process(imagen)
                    .addOnSuccessListener { codigos ->
                        val texto = codigos.firstOrNull { !it.rawValue.isNullOrBlank() }?.rawValue
                        if (texto != null && estaActivo) alLeer(texto)
                    }
                    .addOnCompleteListener { proxy.close() }
            }

            try {
                proveedor.unbindAll()
                proveedor.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analisis
                )
            } catch (e: Exception) {
                // Sin cámara disponible: el usuario aún puede escribir el código a mano.
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            liberado = true
            if (futuro.isDone) runCatching { futuro.get().unbindAll() }
            escaner.close()
            ejecutor.shutdown()
        }
    }

    AndroidView(factory = { vistaPrevia }, modifier = modifier)
}
