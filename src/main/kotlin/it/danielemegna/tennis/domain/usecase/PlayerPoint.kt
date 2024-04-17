package it.danielemegna.tennis.domain.usecase

import it.danielemegna.tennis.domain.repository.MatchRepository
import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.MatchState.Game
import it.danielemegna.tennis.domain.MatchState.Game.GameScore
import it.danielemegna.tennis.domain.MatchState.Serving

class PlayerPoint(private val matchRepository: MatchRepository) {

    fun run(player: Player): MatchState {
        val currentMatchState = matchRepository.getOngoingMatch()

        val newMatchState = currentMatchState.copy(
            currentGame = currentMatchState.currentGame.copy(
                firstPlayerScore = GameScore.FIFTEEN
            )
        )

        matchRepository.updateOngoingMatch(newMatchState);
        return newMatchState
    }

    enum class Player { FIRST, SECOND }

}