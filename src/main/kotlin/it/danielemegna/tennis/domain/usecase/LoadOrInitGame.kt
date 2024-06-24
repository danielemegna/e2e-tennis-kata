package it.danielemegna.tennis.domain.usecase

import it.danielemegna.tennis.domain.repository.MatchRepository
import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.MatchState.Game
import it.danielemegna.tennis.domain.MatchState.Game.GameScore
import it.danielemegna.tennis.domain.MatchState.Serving

class LoadOrInitGame(private val matchRepository: MatchRepository) {

    fun run(matchId: String): MatchState {
        val newMatchState = MatchState(
            firstPlayerName = "Sinner",
            secondPlayerName = "Djokovic",
        )

        matchRepository.storeNewMatch(newMatchState)

        return newMatchState
    }

}