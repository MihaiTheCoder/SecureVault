package com.mihaiapps.securevault.di

import com.mihaiapps.securevault.MainApplication
import com.mihaiapps.securevault.bl.enc.*
import com.mihaiapps.securevault.bl.utils.MemoryPool
import org.koin.dsl.module.applicationContext

val encryptionModule = applicationContext {
    bean { HmacSha1Signature() }
    bean {AsymmetricKeyStoreManagerImpl(EncryptUtils.MASTER_KEY_ALIAS, MainApplication.getContext()) as AsymmetricKeyStoreManager }
    bean { PasswordManager(get(), MainApplication.getContext(), get(), get()) }

    bean{MemoryPool()}
    bean { KeyInitializer(get(),get(), get())}

    bean { EncryptFileManagerImpl(get<KeyInitializer>().cipher,get(), get()) as EncryptedFileManager}
}