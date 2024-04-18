package it.danielemegna.tennis.domain.usecase

import it.danielemegna.tennis.domain.repository.MatchRepository
import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.MatchState.Game
import it.danielemegna.tennis.domain.MatchState.Game.GameScore

class PlayerPoint(private val matchRepository: MatchRepository) {

    fun run(pointAuthor: Player): MatchState {
        val currentMatchState = matchRepository.getOngoingMatch()

        val newMatchState = currentMatchState.copy(
            currentGame = currentMatchState.currentGame.updateWith(pointAuthor)
        )

        matchRepository.updateOngoingMatch(newMatchState);
        return newMatchState
    }

    enum class Player { FIRST, SECOND }

    private fun Game.updateWith(pointAuthor: Player): Game {
        return when(pointAuthor) {
            Player.FIRST -> this.copy(firstPlayerScore = firstPlayerScore.next())
            Player.SECOND -> this.copy(secondPlayerScore = secondPlayerScore.next())
        }
    }
}
