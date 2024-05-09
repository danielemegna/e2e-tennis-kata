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

        if (!gameWon(pointAuthor, currentGame))
            return matchState.increaseCurrentGamePlayerScore(pointAuthor)

        if (setWon(pointAuthor, matchState.currentSet))
            return matchState.setWonByPlayer(pointAuthor)

        return matchState.gameWonByPlayer(pointAuthor)
    }

    private fun setWon(pointAuthor: Player, currentSet: MatchState.Set): Boolean {
        if (pointAuthor == Player.FIRST)
            if (currentSet.firstPlayerScore == 5 && currentSet.secondPlayerScore < 5) return true
        if (pointAuthor == Player.SECOND)
            if (currentSet.secondPlayerScore == 5 && currentSet.firstPlayerScore < 5) return true
        return false
    }

    private fun untappedAdvantagePoint(pointAuthor: Player, currentGame: Game): Boolean {
        if (currentGame.firstPlayerScore == ADVANTAGE && pointAuthor == Player.SECOND) return true
        if (currentGame.secondPlayerScore == ADVANTAGE && pointAuthor == Player.FIRST) return true
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

    private fun MatchState.setCurrentGameFortyForty(): MatchState {
        return this.copy(
            currentGame = currentGame.copy(
                firstPlayerScore = FORTY,
                secondPlayerScore = FORTY
            )
        )
    }

    private fun MatchState.gameWonByPlayer(pointAuthor: Player): MatchState {
        return copy(
            currentSet = currentSet.increaseScore(pointAuthor),
            currentGame = Game(),
            serving = serving.next()
        )
    }

    private fun MatchState.setWonByPlayer(pointAuthor: Player): MatchState {
        val wonSet = currentSet.increaseScore(pointAuthor)
        return this.copy(
            wonSets = wonSets.plus(wonSet),
            currentSet = MatchState.Set(),
            currentGame = Game(),
            serving = serving.next(),
        )
    }

    private fun MatchState.increaseCurrentGamePlayerScore(pointAuthor: Player): MatchState {
        return this.copy(currentGame = currentGame.increaseScore(pointAuthor))
    }

}

