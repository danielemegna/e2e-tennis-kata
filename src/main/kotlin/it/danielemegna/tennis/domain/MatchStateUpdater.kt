package it.danielemegna.tennis.domain

import it.danielemegna.tennis.domain.MatchState.Game
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.FORTY
import it.danielemegna.tennis.domain.usecase.PlayerPoint.Player

class MatchStateUpdater(
    private val matchStateScanner: MatchStateScanner
) {

    fun updatedMatch(matchState: MatchState, pointAuthor: Player): MatchState {
        if (matchStateScanner.wouldCancelTheAdvantagePoint(matchState, pointAuthor))
            return matchState.setCurrentGameFortyForty()

        if (matchStateScanner.wouldWinTieBreak(matchState, pointAuthor))
            return matchState.tieBreakWonByPlayer(pointAuthor)

        if (matchStateScanner.wouldStartTieBreak(matchState, pointAuthor))
            return matchState.startTieBreak(pointAuthor)

        if (matchStateScanner.wouldBeSetPoint(matchState, pointAuthor))
            return matchState.setWonByPlayer(pointAuthor)

        if (matchStateScanner.wouldBeGamePoint(matchState, pointAuthor))
            return matchState.gameWonByPlayer(pointAuthor)

        if (matchState.tieBreakInProgress())
            return matchState.increaseTieBreakPlayerScore(pointAuthor)

        return matchState.increaseCurrentGamePlayerScore(pointAuthor)
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
