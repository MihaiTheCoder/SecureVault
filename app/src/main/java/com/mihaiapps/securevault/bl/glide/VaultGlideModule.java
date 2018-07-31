package com.mihaiapps.securevault.bl.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDecoder;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import androidx.annotation.NonNull;

@GlideModule
public class VaultGlideModule extends AppGlideModule {

    static String TAG = "VaultGlideModule";

    @Override
    public void registerComponents(@NonNull Context context, final @NonNull Glide glide, @NonNull Registry registry) {



        try {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    glide.clearDiskCache();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error clearing Glide cache", e);

        }
        //new Thread(runnable).start();

        Downsampler downsampler = new Downsampler(registry.getImageHeaderParsers(),
                context.getResources().getDisplayMetrics(), glide.getBitmapPool(), glide.getArrayPool());

        registry.prepend(String.class, InputStream.class,
                new LocalEncryptedImageModelLoaderFactory());

        registry.prepend(InputStream.class, new EncryptedStreamEncoder());

        registry.prepend(InputStream.class, Bitmap.class,
                new EncryptedStreamBitmapDecoder(
                        new StreamBitmapDecoder(downsampler, glide.getArrayPool())));

        registry.prepend(Bitmap.class, new EncryptedBitmapResourceEncoder());



        super.registerComponents(context, glide, registry);
    }
}
