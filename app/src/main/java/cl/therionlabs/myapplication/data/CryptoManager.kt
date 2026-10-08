package cl.therionlabs.myapplication.data

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

// Cifra/descifra texto con AES-GCM. La llave vive en el Android Keystore (no sale del dispositivo).
object CryptoManager {

    private const val ALIAS = "captura_prospectos_key"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    private fun obtenerLlave(): SecretKey {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (ks.getKey(ALIAS, null) as? SecretKey)?.let { return it }

        val generador = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generador.init(
            KeyGenParameterSpec.Builder(
                ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generador.generateKey()
    }

    // Resultado = Base64( iv(12 bytes) + texto cifrado )
    fun cifrar(texto: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, obtenerLlave())
        val cifrado = cipher.doFinal(texto.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(cipher.iv + cifrado, Base64.NO_WRAP)
    }

    fun descifrar(base64: String): String {
        val datos = Base64.decode(base64, Base64.NO_WRAP)
        val iv = datos.copyOfRange(0, 12)
        val cifrado = datos.copyOfRange(12, datos.size)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, obtenerLlave(), GCMParameterSpec(128, iv))
        return String(cipher.doFinal(cifrado), Charsets.UTF_8)
    }
}
