package com.example.lexiapp.data.network

import com.example.lexiapp.domain.model.gameResult.LetsReadGameResult
import com.example.lexiapp.domain.service.FireStoreService
import com.example.lexiapp.domain.service.ResultGamesService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResultGamesServiceImpl @Inject constructor(
    private val db: FireStoreService
) : ResultGamesService {
    override suspend fun getWhereIsTheLetterResults(email: String) =
        db.getLastResultsWhereIsTheLetterGame(email)

    override suspend fun getCorrectWordResults(email: String) =
        db.getLastResultsCorrectWordGame(email)

    override fun getLRResults(email: String) =
        db.getLastResultsLetsReadGame(email)

}
