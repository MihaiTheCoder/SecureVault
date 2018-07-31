package com.mihaiapps.securevault.bl.enc

import android.Manifest
import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.content.Context.FINGERPRINT_SERVICE
import android.content.Context.KEYGUARD_SERVICE
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.math.BigInteger
import java.security.*
import java.security.spec.RSAKeyGenParameterSpec
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

class AsymmetricKeyStoreManagerImpl(private val masterKeyAlias: String,
                                    private val context: Context) : AsymmetricKeyStoreManager {

    private val keyStore by lazy { initKeyStore() }

    private val cipher by lazy { Cipher.getInstance(RSA_ECB_PKCS1_PADDING) }

    private val cipherNoPadding by lazy { Cipher.getInstance(RSA_ECB_NO_PADDING) }

    init {
        createMasterKeyIfNotInStore()
    }

    override fun encryptKey(keyToEncrypt: ByteArray): ByteArray {
        return encryptWithCipher(keyToEncrypt, cipher)
    }

    private fun encryptWithCipher(keyToEncrypt: ByteArray, cipher: Cipher): ByteArray {
        val masterKey = getMasterKey()

        return when {
            masterKey != null -> cipherDoFinal(cipher, Cipher.ENCRYPT_MODE, keyToEncrypt, masterKey.public)
            else -> keyToEncrypt
        }
    }

    override fun decryptKey(keyToDecrypt: ByteArray): ByteArray {
        val masterKey = getMasterKey()
        return when {
            masterKey != null -> cipherDoFinal(cipher, Cipher.DECRYPT_MODE, keyToDecrypt, masterKey.private)
            else -> keyToDecrypt
        }
    }

    override fun masterKeyExists() = getMasterKey() != null

    override fun createMasterKeyIfNotInStore() {
        if(!masterKeyExists())
            createMasterKey()
    }

    override fun deleteMasterKey() {
        keyStore.deleteEntry(masterKeyAlias)
    }

    private fun createMasterKey() {
        val generator = KeyPairGenerator.getInstance(ALGORITHM, ANDROID_KEY_STORE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initNewPhones(generator, masterKeyAlias)
        } else {
            initOldPhones(generator, masterKeyAlias, context)
        }

        generator.genKeyPair()
    }

    private fun cipherDoFinal(cipher: Cipher,cipher_mode: Int, content: ByteArray, key: Key): ByteArray {
        cipher.init(cipher_mode, key)
        return cipher.doFinal(content)
    }

    private fun getMasterKey(): KeyPair? {
        val privateKey = keyStore.getKey(masterKeyAlias, null) as PrivateKey?
        val publicKey = keyStore.getCertificate(masterKeyAlias)?.publicKey
        return when {
            privateKey != null && publicKey != null -> KeyPair(publicKey, privateKey)
            else -> null
        }
    }

    private fun initKeyStore(): KeyStore {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
        return keyStore
    }

    @Suppress("DEPRECATION")
    private fun initOldPhones(generator: KeyPairGenerator, alias: String, context: Context) {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.YEAR, 20)

        val builder = KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSerialNumber(BigInteger.ONE)
                .setSubject(X500Principal("CN=$alias CA Certificate"))
                .setStartDate(startDate.time)
                .setEndDate(endDate.time)

        generator.initialize(builder.build())
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun initNewPhones(generator: KeyPairGenerator, alias: String) {

        var builder = KeyGenParameterSpec.Builder(
                alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setAlgorithmParameterSpec(RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4))
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .setDigests(KeyProperties.DIGEST_SHA256)


        if(isFingerprintEnabled())
            builder = builder.setUserAuthenticationRequired(true)

        if(Build.VERSION.SDK_INT >= 28) {
            builder = builder.setUserPresenceRequired(true)
        }
        generator.initialize(builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isFingerprintEnabled(): Boolean {

        val fingerprintManager = context.getSystemService(FINGERPRINT_SERVICE) as FingerprintManager
        val keyguardManager = context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager

        return if (!fingerprintManager.isHardwareDetected)
        {
            // Device doesn't support fingerprint authentication
            false
        }
        else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED)
        {
            false
        }
        else if (!keyguardManager.isKeyguardSecure)
        {
            false
        }
        else fingerprintManager.hasEnrolledFingerprints()
    }

    companion object {
        private const val  ANDROID_KEY_STORE= "AndroidKeyStore"
        private const val ALGORITHM = "RSA"
        private const val RSA_ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding"
        private const val RSA_ECB_NO_PADDING = "RSA/ECB/NoPadding"
    }
}