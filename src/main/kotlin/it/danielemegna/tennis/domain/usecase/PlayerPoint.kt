package it.danielemegna.tennis.domain.usecase

import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.MatchState.Game
import it.danielemegna.tennis.domain.repository.MatchRepository

class PlayerPoint(private val matchRepository: MatchRepository) {

    fun run(pointAuthor: Player): MatchState {
        val currentMatchState = matchRepository.getOngoingMatch()

        val newMatchState = updatedMatch(currentMatchState, pointAuthor)

        matchRepository.updateOngoingMatch(newMatchState);
        return newMatchState
    }

    private fun updatedMatch(matchState: MatchState, pointAuthor: Player): MatchState {
        val currentGame = matchState.currentGame
        val currentSet = matchState.currentSet

        if (
            (pointAuthor == Player.FIRST && currentGame.secondPlayerScore == Game.GameScore.ADVANTAGE)
            ||
            (pointAuthor == Player.SECOND && currentGame.firstPlayerScore == Game.GameScore.ADVANTAGE)
        ) {
            return matchState.copy(
                currentGame = currentGame.copy(
                    firstPlayerScore = Game.GameScore.FORTY,
                    secondPlayerScore = Game.GameScore.FORTY
                )
            )
        }

        if (
            pointAuthor == Player.FIRST &&
            (currentGame.firstPlayerScore == Game.GameScore.ADVANTAGE ||
                (currentGame.firstPlayerScore == Game.GameScore.FORTY && currentGame.secondPlayerScore < Game.GameScore.FORTY))
        ) {
            return matchState.copy(
                currentGame = Game(),
                currentSet = currentSet.copy(firstPlayerScore = currentSet.firstPlayerScore + 1),
                serving = matchState.serving.next()
            )
        }

        if (
            pointAuthor == Player.SECOND &&
            (currentGame.secondPlayerScore == Game.GameScore.ADVANTAGE ||
                (currentGame.secondPlayerScore == Game.GameScore.FORTY && currentGame.firstPlayerScore < Game.GameScore.FORTY))
        ) {
            return matchState.copy(
                currentGame = Game(),
                currentSet = currentSet.copy(secondPlayerScore = currentSet.secondPlayerScore + 1),
                serving = matchState.serving.next()
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
