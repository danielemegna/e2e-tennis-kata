package it.danielemegna.tennis.domain

import it.danielemegna.tennis.domain.MatchState.Game
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.ADVANTAGE
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.FORTY
import it.danielemegna.tennis.domain.usecase.PlayerPoint.Player

class MatchStateUpdater {

    fun updatedMatch(matchState: MatchState, pointAuthor: Player): MatchState {
        val currentGame = matchState.currentGame

        if (untappedAdvantagePoint(pointAuthor, currentGame))
            return matchState.setCurrentGameFortyForty()

        if (gameWon(pointAuthor, currentGame))
            return matchState.gameWonByPlayer(pointAuthor)

        return matchState.increaseCurrentGamePlayerScore(pointAuthor)
    }

    private fun untappedAdvantagePoint(pointAuthor: Player, currentGame: Game): Boolean {
        if (currentGame.firstPlayerScore == ADVANTAGE)
            return pointAuthor == Player.SECOND
        if (currentGame.secondPlayerScore == ADVANTAGE)
            return pointAuthor == Player.FIRST

        return false
    }

    private fun gameWon(pointAuthor: Player, currentGame: Game): Boolean {
        if (pointAuthor == Player.FIRST) {
            if (currentGame.firstPlayerScore == ADVANTAGE) return true
            if (currentGame.firstPlayerScore == FORTY && currentGame.secondPlayerScore < FORTY) return true
        }
        if (pointAuthor == Player.SECOND) {
            if (currentGame.secondPlayerScore == ADVANTAGE) return true
            if (currentGame.secondPlayerScore == FORTY && currentGame.firstPlayerScore < FORTY) return true
        }

        return false
    }

    private fun MatchState.setCurrentGameFortyForty() =
        this.copy(
            currentGame = currentGame.copy(
                firstPlayerScore = FORTY,
                secondPlayerScore = FORTY
            )
        )

    private fun MatchState.gameWonByPlayer(pointAuthor: Player): MatchState {
        return this.copy(
            currentGame = Game(),
            currentSet = currentSet.increaseScore(pointAuthor),
            serving = serving.next()
        )
    }

    private fun MatchState.increaseCurrentGamePlayerScore(pointAuthor: Player) =
        this.copy(currentGame = currentGame.increaseScore(pointAuthor))

}
