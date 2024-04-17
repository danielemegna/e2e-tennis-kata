package it.danielemegna.tennis.domain.usecase

import it.danielemegna.tennis.domain.repository.MatchRepository
import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.MatchState.Game
import it.danielemegna.tennis.domain.MatchState.Game.GameScore
import it.danielemegna.tennis.domain.MatchState.Serving

class InitNewGame(private val matchRepository: MatchRepository) {

    fun run(): MatchState {
        val newMatchState = MatchState(
            firstPlayerName = "Sinner",
            secondPlayerName = "Djokovic",
            serving = Serving.FIRST_PLAYER,
            currentGame = Game(
                firstPlayerScore = GameScore.ZERO,
                secondPlayerScore = GameScore.ZERO
            )
        )

        matchRepository.storeNewMatch(newMatchState)

        return newMatchState
    }

}