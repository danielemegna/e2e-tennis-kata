package it.danielemegna.tennis.domain

import it.danielemegna.tennis.domain.MatchState.Game
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.ADVANTAGE
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.FORTY
import it.danielemegna.tennis.domain.usecase.PlayerPoint.Player

class MatchStateUpdater {

    fun updatedMatch(matchState: MatchState, pointAuthor: Player): MatchState {
        val currentGame = matchState.currentGame
        val currentSet = matchState.currentSet

        if (untappedAdvantagePoint(pointAuthor, currentGame))
            return matchState.setCurrentGameFortyForty()

        if (
            pointAuthor == Player.FIRST &&
            (currentGame.firstPlayerScore == ADVANTAGE ||
                (currentGame.firstPlayerScore == FORTY && currentGame.secondPlayerScore < FORTY))
        ) {
            return matchState.copy(
                currentGame = Game(),
                currentSet = currentSet.copy(firstPlayerScore = currentSet.firstPlayerScore + 1),
                serving = matchState.serving.next()
            )
        }

        if (
            pointAuthor == Player.SECOND &&
            (currentGame.secondPlayerScore == ADVANTAGE ||
                (currentGame.secondPlayerScore == FORTY && currentGame.firstPlayerScore < FORTY))
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


    private fun untappedAdvantagePoint(pointAuthor: Player, currentGame: Game): Boolean {
        if (currentGame.firstPlayerScore == ADVANTAGE)
            return pointAuthor == Player.SECOND
        if (currentGame.secondPlayerScore == ADVANTAGE)
            return pointAuthor == Player.FIRST

        return false
    }

    private fun Game.updateWith(pointAuthor: Player): Game {
        return when (pointAuthor) {
            Player.FIRST -> this.copy(firstPlayerScore = firstPlayerScore.next())
            Player.SECOND -> this.copy(secondPlayerScore = secondPlayerScore.next())
        }
    }

    private fun MatchState.setCurrentGameFortyForty() =
        this.copy(
            currentGame = currentGame.copy(
                firstPlayerScore = FORTY,
                secondPlayerScore = FORTY
            )
        )
}
