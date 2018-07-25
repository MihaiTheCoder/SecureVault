package com.mihaiapps.securevault.bl.glide;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.mihaiapps.securevault.bl.enc.cipher.AESCipher;
import com.mihaiapps.securevault.bl.utils.MemoryPool;

import java.io.InputStream;

import androidx.annotation.NonNull;

@GlideModule
public class VaultGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {

        registry.prepend(String.class, InputStream.class,
                new LocalEncryptedImageModelLoaderFactory());
        super.registerComponents(context, glide, registry);

        //registry.prepend(String::class.java, InputStream::class.java,
//                LocalEncryptedImageModelLoaderFactoryX(context, AESCipher.getInstance(MemoryPool())))
    }
}
