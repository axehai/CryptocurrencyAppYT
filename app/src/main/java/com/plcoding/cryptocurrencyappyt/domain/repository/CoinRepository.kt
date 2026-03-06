package com.plcoding.cryptocurrencyappyt.domain.repository

import com.plcoding.cryptocurrencyappyt.data.remote.dto.CoinDetailDto
import com.plcoding.cryptocurrencyappyt.data.remote.dto.CoinDto

/**
 * Only interfaces lie on the domain repositories
 * Concrete implementations happen on data-repository
 */
interface CoinRepository {
    suspend fun getCoins(): List<CoinDto>
    suspend fun getCoinById(coinId: String): CoinDetailDto
}