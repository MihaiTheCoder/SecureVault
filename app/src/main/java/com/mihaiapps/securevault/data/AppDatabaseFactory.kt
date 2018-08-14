package com.mihaiapps.securevault.data


import android.util.Log
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room
import com.mihaiapps.securevault.MainApplication
import com.commonsware.cwac.saferoom.*
import com.mihaiapps.securevault.Login
import com.mihaiapps.securevault.R
import kotlinx.android.synthetic.main.fragment_login.*

class AppDatabaseFactory {
    companion object {
        const val TAG = "AppDatabaseFactory"
    }

    private var encryptFactory: SafeHelperFactory? = null
    var database: AppDatabase? = null

    fun get(): AppDatabase {

        if(database != null)
            return database!!

        //MainApplication.getContext().deleteDatabase(AppDatabase.NAME)

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
}