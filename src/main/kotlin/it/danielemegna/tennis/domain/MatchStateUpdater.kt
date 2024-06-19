package it.danielemegna.tennis.domain

import it.danielemegna.tennis.domain.MatchState.Game
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.ADVANTAGE
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.FORTY
import it.danielemegna.tennis.domain.usecase.PlayerPoint.Player

class MatchStateUpdater {

    fun updatedMatch(matchState: MatchState, pointAuthor: Player): MatchState {
        if (matchState.isCanceledAdvantagePoint(pointAuthor))
            return matchState.setCurrentGameFortyForty()

        if (matchState.tieBreakInProgress()) {
            if (matchState.isTieBreakWinningPoint(pointAuthor))
                return matchState.tieBreakWonByPlayer(pointAuthor)
            return matchState.increaseTieBreakPlayerScore(pointAuthor)
        }

        if (matchState.isGamePoint(pointAuthor)) {
            if (matchState.isSetPoint(pointAuthor))
                return matchState.setWonByPlayer(pointAuthor)
            if (matchState.needTieBreak(pointAuthor))
                return matchState.startTieBreak(pointAuthor)

            return matchState.gameWonByPlayer(pointAuthor)
        }

        return matchState.increaseCurrentGamePlayerScore(pointAuthor)
    }

    private fun MatchState.isCanceledAdvantagePoint(pointAuthor: Player): Boolean {
        return when (pointAuthor) {
            Player.FIRST -> currentGame.secondPlayerScore == ADVANTAGE
            Player.SECOND -> currentGame.firstPlayerScore == ADVANTAGE
        }
    }

    private fun MatchState.isTieBreakWinningPoint(pointAuthor: Player): Boolean {
        val currentTieBreak = currentSet.tieBreak ?: return false

        return when (pointAuthor) {
            Player.FIRST ->
                currentTieBreak.firstPlayerScore >= 6 && currentTieBreak.secondPlayerScore < currentTieBreak.firstPlayerScore

            Player.SECOND ->
                currentTieBreak.secondPlayerScore >= 6 && currentTieBreak.firstPlayerScore < currentTieBreak.secondPlayerScore
        }
    }

    private fun MatchState.isGamePoint(pointAuthor: Player): Boolean {
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

    private fun MatchState.isSetPoint(pointAuthor: Player): Boolean {
        return when (pointAuthor) {
            Player.FIRST ->
                currentSet.firstPlayerScore >= 5 && currentSet.secondPlayerScore < currentSet.firstPlayerScore

            Player.SECOND ->
                currentSet.secondPlayerScore >= 5 && currentSet.firstPlayerScore < currentSet.secondPlayerScore
        }
    }

    private fun MatchState.needTieBreak(pointAuthor: Player): Boolean {
        return when (pointAuthor) {
            Player.FIRST -> (currentSet.firstPlayerScore == 5 && currentSet.secondPlayerScore == 6)
            Player.SECOND -> (currentSet.secondPlayerScore == 5 && currentSet.firstPlayerScore == 6)
        }
    }

    private fun MatchState.setCurrentGameFortyForty(): MatchState {
        return this.copy(
            currentGame = currentGame.copy(
                firstPlayerScore = FORTY,
                secondPlayerScore = FORTY
            )
        )
    }

    private fun MatchState.tieBreakWonByPlayer(pointAuthor: Player): MatchState {
        val currentTieBreak = currentSet.tieBreak
            ?: throw RuntimeException("Tie break won by player never started!")

        val wonTieBreak = currentTieBreak.increaseScore(pointAuthor)
        val wonSet = currentSet.increaseScore(pointAuthor).copy(tieBreak = wonTieBreak)
        return this.copy(
            wonSets = wonSets.plus(wonSet),
            currentSet = MatchState.Set(),
            currentGame = Game(),
            serving = wonTieBreak.playerStartedTheTieBreak.next()
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

    private fun MatchState.startTieBreak(pointAuthor: Player): MatchState {
        val newState = this.gameWonByPlayer(pointAuthor)
        val setWithTieBreak = newState.currentSet.copy(
            tieBreak = MatchState.TieBreak(newState.serving)
        )
        return newState.copy(currentSet = setWithTieBreak)
    }

    private fun MatchState.gameWonByPlayer(pointAuthor: Player): MatchState {
        return copy(
            currentSet = currentSet.increaseScore(pointAuthor),
            currentGame = Game(),
            serving = serving.next()
        )
    }

    private fun MatchState.increaseTieBreakPlayerScore(pointAuthor: Player): MatchState {
        val currentTieBreak = currentSet.tieBreak
            ?: throw RuntimeException("Cannot increase tie break score: tie break never started!")

        val updatedTieBreak = currentTieBreak.increaseScore(pointAuthor)
        val updatedSet = this.currentSet.copy(tieBreak = updatedTieBreak)
        val newServing = if (updatedTieBreak.shouldChangeServing()) this.serving.next() else this.serving

        return this.copy(
            currentSet = updatedSet,
            serving = newServing
        )
    }

    private fun MatchState.increaseCurrentGamePlayerScore(pointAuthor: Player): MatchState {
        return this.copy(currentGame = currentGame.increaseScore(pointAuthor))
    }

}
