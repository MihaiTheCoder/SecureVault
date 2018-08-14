package com.mihaiapps.securevault.bl.enc

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.mihaiapps.securevault.data.AppDatabaseFactory
import com.mihaiapps.securevault.data.KeyValuePairEntity
import java.security.SecureRandom
import android.util.Base64
import com.mihaiapps.securevault.bl.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class PasswordManager(private val asymmetricKeyStoreManager: AsymmetricKeyStoreManager,
                      context: Context,
                      private val databaseFactory: AppDatabaseFactory,
                      private val hmacSha1Signature: HmacSha1Signature) {
    companion object {
        const val IS_PIN_REGISTERED_PREF= "IsPinRegistered"
        const val ALLOW_PASSWORD_FORGET = "IsPasswordForgettable"
        const val PASSWORD_HASH = "PASSWORD_HASH"
        const val PASSWORD_HMAC_KEY = "PASSWORD_HMAC_KEY"
        const val LOGIN_ATTEMPTS = "LOGIN_ATTEMPTS"

    }

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val keyValuePairDAO by lazy { databaseFactory.get().keyValuePairDao() }

    private var hmac: ByteArray? = null

    fun getIsPinRegistered(): Boolean {
        return preferences.getBoolean(IS_PIN_REGISTERED_PREF, false)
    }

    fun setIsPinRegistered(value: Boolean) {
        val preferencesEdit = preferences.edit()
        preferencesEdit.putBoolean(IS_PIN_REGISTERED_PREF, value)
        preferencesEdit.apply()
    }

    fun setIsPasswordForgettable(isPasswordForgettable: Boolean) {
        val preferencesEdit = preferences.edit()
        preferencesEdit.putBoolean(ALLOW_PASSWORD_FORGET, isPasswordForgettable)
        preferencesEdit.apply()
    }


    fun hasDecidedIfPasswordIsForgettable(): Boolean {
        return preferences.contains(ALLOW_PASSWORD_FORGET)
    }

    fun getIsPasswordForgettable(): Boolean {
        return preferences.getBoolean(ALLOW_PASSWORD_FORGET, false)
    }

    fun rememberPassword(pass: CharSequence) {
        hmac = hmac(pass)
    }

    fun verifyPinIsTheSame(pass: CharSequence): Boolean {
        return if(hmac == null)
            false
        else
            hmac(pass).contentEquals(hmac!!)
    }

    fun hmac(passSequence: CharSequence): ByteArray {
        val passByteArray = toByteArray(passSequence)
        hmacSha1Signature.init(getHmacKey())
        return hmacSha1Signature.hmac(passByteArray)
    }

    private fun getHmacKey(): ByteArray {
        val encryptedHmac =  preferences.getString(PASSWORD_HMAC_KEY,null)

        return if(encryptedHmac == null) {
            val random = SecureRandom()
            val key = ByteArray(16)
            random.nextBytes(key)
            val encryptedKey = asymmetricKeyStoreManager.encryptKey(key)
            val edit = preferences.edit()
            edit.putString(PASSWORD_HMAC_KEY, base64Encode(encryptedKey))
            edit.apply()
            key
        }
        else {
            asymmetricKeyStoreManager.decryptKey(base64Decode(encryptedHmac))
        }
    }

    private fun base64Encode(message: ByteArray): String {
        return String(Base64.encode(message,Base64.DEFAULT))
    }

    private fun base64Decode(message: String): ByteArray {
        return Base64.decode(message, Base64.DEFAULT)
    }

    private fun deleteSharedPreference(key: String) {
        val edit = preferences.edit()
        edit.remove(key)
        edit.apply()
    }

    private fun toByteArray(charSequence: CharSequence): ByteArray {
        val barr = ByteArray(charSequence.length)
        for (i in barr.indices) {
            barr[i] = charSequence[i].toByte()
        }
        return barr
    }

    fun registerPasswordInDatabase(pass:CharSequence) {
        val mac: ByteArray = hmac(pass)
        keyValuePairDAO.insertAll(KeyValuePairEntity(PASSWORD_HASH,mac))
        val loginAttempts = LoginAttempts(0, Calendar.getInstance().time)
        upsertLoginAttempts(loginAttempts)
    }

    fun initDbWithPassword(pass:CharArray) {
        databaseFactory.setEncryption(pass)
    }

    fun isDatabaseCorrectlyDecrypted(): Boolean {
        return databaseFactory.isDatabaseCorrectlyDecrypted()
    }

    fun login(pass:CharSequence): Boolean {
        val keyValuePairEntity: KeyValuePairEntity? = keyValuePairDAO.findById(PASSWORD_HASH)
        return if(keyValuePairEntity == null)

            false
        else {
            val isPasswordCorrect = hmac(pass).contentEquals(keyValuePairEntity.value)

            if(isPasswordCorrect)
                upsertLoginAttempts(LoginAttempts(0, getCurrentDate()))
            else {
                increaseNumberOfAttempts()
            }

            isPasswordCorrect
        }
    }

    fun getPeriodToWaitForWrongPin(): Long {
        return if(!getIsPinRegistered()) {
            0
        }
        else {
            val loginAttempts = getLoginAttempts()
            val nextLoginAttempts = LoginAttempts(loginAttempts.numberOfRetries + 1, getCurrentDate())
            val delaysInMillis = getDelayInMilliSeconds(nextLoginAttempts.numberOfRetries)

            delaysInMillis
        }
    }

    private fun getDelayInMilliSeconds(numberOfRetries: Int): Long {
        if(numberOfRetries <= -1)
            return TimeUnit.DAYS.toMillis(1)
        if(numberOfRetries <= 6)
            return 0
        if(numberOfRetries<=10)
            return TimeUnit.MINUTES.toMillis(1)
        if(numberOfRetries<=20)
            return  TimeUnit.MINUTES.toMillis(30)
        return TimeUnit.DAYS.toMillis(1)
    }

    private fun canRetryLogin(loginAttempts: LoginAttempts): Boolean {
        if(loginAttempts.numberOfRetries == 0)
            return true

        val actualDiffInMillis = getCurrentDate().time - loginAttempts.dateOfLastRetry.time
        val expectedDelayInMillis = getDelayInMilliSeconds(loginAttempts.numberOfRetries)

        return actualDiffInMillis > expectedDelayInMillis
    }

    private fun getCurrentDate() = Calendar.getInstance().time

    private fun upsertLoginAttempts(loginAttempts: LoginAttempts) {
        val loginAttemptsByteArray = loginAttempts.toByteArray()
        val encryptedAttempts = asymmetricKeyStoreManager.encryptKey(loginAttemptsByteArray)
        val edit = preferences.edit()
        edit.putString(LOGIN_ATTEMPTS, encryptedAttempts.encodeToBase64())
        edit.apply()
    }

    private fun getLoginAttempts(): LoginAttempts {
        val loginEncrypted = preferences.getString(LOGIN_ATTEMPTS, null)!!
                .decodeBase64()

        val decryptedLoginAttempts = asymmetricKeyStoreManager.decryptKey(loginEncrypted)
        return LoginAttempts.fromByteArray(decryptedLoginAttempts)
    }

    private fun increaseNumberOfAttempts() {
        val loginAttempts = getLoginAttempts()
        val newLoginAttempts = LoginAttempts(loginAttempts.numberOfRetries+1, getCurrentDate())
        upsertLoginAttempts(newLoginAttempts)
    }

    data class LoginAttempts(val numberOfRetries: Int, val dateOfLastRetry: Date) {
        override fun toString(): String {
            return "$numberOfRetries;${dateFormat.format(dateOfLastRetry)}"
        }

        fun toByteArray(): ByteArray {
            return toString().toByteArrayUtf8()
        }

        companion object {
            fun fromByteArray(serialized: ByteArray): LoginAttempts {
                val str = serialized.toStringUtf8()
                val splited = str.split(";")
                val numberOfRetries = splited[0].toInt()

                return LoginAttempts(numberOfRetries, dateFormat.parse(splited[1]))
            }

            var dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        }
    }
}