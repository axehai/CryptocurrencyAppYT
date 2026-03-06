package com.plcoding.cryptocurrencyappyt.domain.use_case.get_coins

import com.plcoding.cryptocurrencyappyt.common.Resource
import com.plcoding.cryptocurrencyappyt.data.remote.dto.CoinDetailDto
import com.plcoding.cryptocurrencyappyt.data.remote.dto.CoinDto
import com.plcoding.cryptocurrencyappyt.domain.repository.CoinRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class GetCoinsUseCaseTest {
    private lateinit var fakeRepository: FakeCoinRepository
    private lateinit var useCase: GetCoinsUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeCoinRepository()
        useCase = GetCoinsUseCase(fakeRepository)
    }

    @Test
    fun `invoke emits Loading then Success when repository returns coins`() = runTest {
        //Arrange
        fakeRepository.coinsToReturn = listOf(
            CoinDto(
                id = "bitcoin",
                isActive = true,
                isNew = false,
                name = "Bitcoin",
                rank = 1,
                symbol = "BTC",
                type = "coin"
            ),
            CoinDto(
                id = "ethereum",
                isActive = true,
                isNew = false,
                name = "Ethereum",
                rank = 2,
                symbol = "ETH",
                type = "coin"
            )
        )

        //Act
        val result = useCase().toList()

        //Assert
        assertTrue(result[0] is Resource.Loading)

        assertTrue(result[1] is Resource.Success)
        val success = result[1] as Resource.Success
        assertEquals("Bitcoin", success.data?.get(0)?.name)
        assertEquals(2, success.data?.size)
    }

    @Test
    fun `invoke emits Loading then Error when repository throws IOException`() = runTest {
        //Arrange
        fakeRepository.shouldThrowIOException = true

        //Act
        val result = useCase().toList()

        //Assert
        assertTrue(result[0] is Resource.Loading)

        assertTrue(result[1] is Resource.Error)
        val error = result[1] as Resource.Error
        assertEquals("Couldn't reach servers, check your internet connection",error.message)
    }

    @Test
    fun `invoke emits Loading then Error when repository throws HttpException`() = runTest {
        //Arrange
        fakeRepository.shouldThrowHttpException = true
        //Act
        val result = useCase().toList()

        //Assert
        assertTrue(result[0] is Resource.Loading)

        assertTrue(result[1] is Resource.Error)
        val error = result[1] as Resource.Error
        assertTrue(error.message?.isNotBlank() == true)
    }

}

private class FakeCoinRepository : CoinRepository {

    var coinsToReturn: List<CoinDto> = emptyList()
    var shouldThrowIOException: Boolean = false
    var shouldThrowHttpException = false


    override suspend fun getCoins(): List<CoinDto> {
        if(shouldThrowIOException) throw IOException()
        if(shouldThrowHttpException) {
            val errorResponse = Response.error<Any>(
                404,
                "{}".toResponseBody("application/json".toMediaType())
            )
            throw HttpException(errorResponse)
        }
        return coinsToReturn
    }

    override suspend fun getCoinById(coinId: String): CoinDetailDto {
        TODO("Not needed for GetCoinsUseCase tests")
    }

}