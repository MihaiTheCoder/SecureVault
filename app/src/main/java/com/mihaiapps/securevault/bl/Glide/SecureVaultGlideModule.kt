package com.mihaiapps.securevault.bl.Glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.mihaiapps.securevault.bl.enc.EncryptUtils
import com.mihaiapps.securevault.bl.enc.cipher.AESCipher
import com.mihaiapps.securevault.bl.utils.MemoryPool
import java.io.InputStream

@GlideModule
class SecureVaultGlideModule: AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {



        registry.prepend(String::class.java, InputStream::class.java,
                LocalEncryptedImageModelLoaderFactory(context, AESCipher.getInstance(MemoryPool())))
    }
}