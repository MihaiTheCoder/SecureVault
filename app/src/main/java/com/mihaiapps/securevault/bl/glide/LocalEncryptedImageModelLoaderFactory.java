package com.mihaiapps.securevault.bl.glide;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

import androidx.annotation.NonNull;

public class LocalEncryptedImageModelLoaderFactory implements ModelLoaderFactory<String, InputStream> {
    @NonNull
    @Override
    public ModelLoader<String, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return new LocalEncryptedImageModelLoader(new LocalEncryptedDataFetcherFactory());
    }

    @Override
    public void teardown() {

    }
}
