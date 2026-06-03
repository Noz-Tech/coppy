package org.noztech.coppy.core.database

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import java.security.GeneralSecurityException

internal object DatabasePassphraseManager {
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val KEY_ALIAS = "coppy_db_master_key"
    private const val PREFS_NAME = "coppy_secure_prefs"
    private const val PREF_ENCRYPTED_PASSPHRASE = "encrypted_db_passphrase"
    private const val PREF_IV = "encrypted_db_passphrase_iv"

    fun getOrCreate(
        context: Context,
        onPassphraseReset: () -> Unit = {}
    ): ByteArray {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val encrypted = prefs.getString(PREF_ENCRYPTED_PASSPHRASE, null)
        val iv = prefs.getString(PREF_IV, null)

        if (encrypted != null && iv != null) {
            try {
                return decrypt(
                    encrypted = Base64.decode(encrypted, Base64.DEFAULT),
                    iv = Base64.decode(iv, Base64.DEFAULT),
                    key = getOrCreateSecretKey()
                )
            } catch (_: GeneralSecurityException) {
                resetStoredPassphrase(context)
                onPassphraseReset()
            } catch (_: IllegalArgumentException) {
                resetStoredPassphrase(context)
                onPassphraseReset()
            }
        }

        val passphrase = ByteArray(32).also { java.security.SecureRandom().nextBytes(it) }
        val encryption = encrypt(passphrase, getOrCreateSecretKey())
        prefs.edit()
            .putString(PREF_ENCRYPTED_PASSPHRASE, Base64.encodeToString(encryption.first, Base64.NO_WRAP))
            .putString(PREF_IV, Base64.encodeToString(encryption.second, Base64.NO_WRAP))
            .apply()
        return passphrase
    }

    private fun resetStoredPassphrase(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(PREF_ENCRYPTED_PASSPHRASE)
            .remove(PREF_IV)
            .apply()

        KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
            load(null)
            if (containsAlias(KEY_ALIAS)) {
                deleteEntry(KEY_ALIAS)
            }
        }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        val existingKey = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (existingKey != null) return existingKey

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    private fun encrypt(plainBytes: ByteArray, key: SecretKey): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encrypted = cipher.doFinal(plainBytes)
        return encrypted to cipher.iv
    }

    private fun decrypt(encrypted: ByteArray, iv: ByteArray, key: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        return cipher.doFinal(encrypted)
    }
}
