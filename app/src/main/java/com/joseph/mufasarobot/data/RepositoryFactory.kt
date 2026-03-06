package com.joseph.mufasarobot.data

import android.content.Context
import com.joseph.mufasarobot.data.local.TokenManagerImpl
import com.joseph.mufasarobot.data.remote.RetrofitClient
import com.joseph.mufasarobot.data.repository.TradingRepositoryImpl
import com.joseph.mufasarobot.interfaces.TradingRepository

object RepositoryFactory {

    private var tradingRepository: TradingRepository? = null

    fun getTradingRepository(context: Context): TradingRepository {
        return tradingRepository ?: synchronized(this) {
            val tokenManager = TokenManagerImpl(context)
            val apiService = RetrofitClient.apiService
            TradingRepositoryImpl(apiService, tokenManager).also {
                tradingRepository = it
            }
        }
    }
}