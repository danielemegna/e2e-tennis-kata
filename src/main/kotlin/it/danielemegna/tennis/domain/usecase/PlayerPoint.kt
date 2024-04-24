package it.danielemegna.tennis.domain.usecase

import it.danielemegna.tennis.domain.repository.MatchRepository
import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.MatchState.Game

class PlayerPoint(private val matchRepository: MatchRepository) {

    fun run(pointAuthor: Player): MatchState {
        val currentMatchState = matchRepository.getOngoingMatch()

        val newMatchState = updatedMatch(currentMatchState, pointAuthor)

        matchRepository.updateOngoingMatch(newMatchState);
        return newMatchState
    }

    private fun updatedMatch(matchState: MatchState, pointAuthor: Player): MatchState {
        val currentGame = matchState.currentGame
        if (
            currentGame.firstPlayerScore == Game.GameScore.FORTY &&
            pointAuthor == Player.FIRST
        ) {
            val currentSet = matchState.currentSet
            return matchState.copy(
                currentGame = Game(),
                currentSet = currentSet.copy(firstPlayerScore = currentSet.firstPlayerScore + 1),
                serving = matchState.serving.next()
            )
        }

        if (
            currentGame.secondPlayerScore == Game.GameScore.FORTY &&
            pointAuthor == Player.SECOND
        ) {
            val currentSet = matchState.currentSet
            return matchState.copy(
                currentGame = Game(),
                currentSet = currentSet.copy(secondPlayerScore = currentSet.secondPlayerScore + 1)
            )
        }

        return matchState.copy(
            currentGame = currentGame.updateWith(pointAuthor)
        )
    }

    enum class Player { FIRST, SECOND }

    private fun Game.updateWith(pointAuthor: Player): Game {
        return when (pointAuthor) {
            Player.FIRST -> this.copy(firstPlayerScore = firstPlayerScore.next())
            Player.SECOND -> this.copy(secondPlayerScore = secondPlayerScore.next())
        }
    }
}
