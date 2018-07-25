package com.mihaiapps.securevault.bl.glide

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream

//@GlideModule
class KotlinVaultGLideModule: AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        Log.v("CIupa", "CIUPA")
        Log.v("CIupa", "CIUPA")
        Log.v("CIupa", "CIUPA")
        Log.v("CIupa", "CIUPA")
        Log.v("CIupa", "CIUPA")
        Log.v("CIupa", "CIUPA")
        Log.v("CIupa", "CIUPA")
        Log.v("CIupa", "CIUPA")
        registry.prepend(String::class.java, InputStream::class.java,
                LocalEncryptedImageModelLoaderFactory())
        super.registerComponents(context, glide, registry)
    }
}