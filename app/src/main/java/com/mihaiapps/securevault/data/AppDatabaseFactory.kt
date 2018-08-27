package com.mihaiapps.securevault.data


import android.util.Log
import androidx.room.Room
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.mihaiapps.securevault.MainApplication

class AppDatabaseFactory {
    companion object {
        const val TAG = "AppDatabaseFactory"
    }

    private var encryptFactory: SafeHelperFactory? = null
    var database: AppDatabase? = null

    fun get(): AppDatabase {

        if(database != null)
            return database!!

        var db =  Room.databaseBuilder(MainApplication.getContext(), AppDatabase::class.java, AppDatabase.NAME)
                .allowMainThreadQueries()

        if(encryptFactory != null)
            db = db.openHelperFactory(encryptFactory)

        database = db.build()

        return database!!
    }

    fun setEncryption(pass: CharArray) {
        encryptFactory = SafeHelperFactory(pass)
    }

    fun isDatabaseCorrectlyDecrypted(): Boolean {
        val db = get()
        return try {
            db.beginTransaction()
            true
        } catch (e: Exception) {
            false
        } finally {
            try {
                db.endTransaction()
            } catch (e: Exception) {
                Log.e(TAG,e.toString())
            }
        }
    }

    fun changePin(newPin: CharArray) {
        if(isDatabaseCorrectlyDecrypted()) {
            val writableDatabase = get().openHelper.writableDatabase
            SafeHelperFactory.rekey(writableDatabase,newPin)
        }
    }
}