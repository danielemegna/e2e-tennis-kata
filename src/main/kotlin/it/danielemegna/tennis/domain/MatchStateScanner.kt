package it.danielemegna.tennis.domain

import it.danielemegna.tennis.domain.MatchState.Game.GameScore.ADVANTAGE
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.FORTY
import it.danielemegna.tennis.domain.usecase.PlayerPoint.Player

class MatchStateScanner {

    fun wouldBeGamePoint(matchState: MatchState, pointAuthor: Player): Boolean {
        val currentGame = matchState.currentGame

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

    fun wouldBeSetPoint(matchState: MatchState, pointAuthor: Player): Boolean {
        if (wouldWinTieBreak(matchState, pointAuthor)) return true
        if (!wouldBeGamePoint(matchState, pointAuthor)) return false

        val currentSet = matchState.currentSet
        return when (pointAuthor) {
            Player.FIRST ->
                currentSet.firstPlayerScore >= 5 && currentSet.secondPlayerScore < currentSet.firstPlayerScore

            Player.SECOND ->
                currentSet.secondPlayerScore >= 5 && currentSet.firstPlayerScore < currentSet.secondPlayerScore
        }

    }

    fun wouldStartTieBreak(matchState: MatchState, pointAuthor: Player): Boolean {
        if (!wouldBeGamePoint(matchState, pointAuthor)) return false

        val currentSet = matchState.currentSet
        return when (pointAuthor) {
            Player.FIRST -> (currentSet.firstPlayerScore == 5 && currentSet.secondPlayerScore == 6)
            Player.SECOND -> (currentSet.secondPlayerScore == 5 && currentSet.firstPlayerScore == 6)
        }
    }

    fun wouldWinTieBreak(matchState: MatchState, pointAuthor: Player): Boolean {
        val currentTieBreak = matchState.currentSet.tieBreak ?: return false

        return when (pointAuthor) {
            Player.FIRST ->
                currentTieBreak.firstPlayerScore >= 6 && currentTieBreak.secondPlayerScore < currentTieBreak.firstPlayerScore

            Player.SECOND ->
                currentTieBreak.secondPlayerScore >= 6 && currentTieBreak.firstPlayerScore < currentTieBreak.secondPlayerScore
        }
    }

    fun wouldCancelTheAdvantagePoint(matchState: MatchState, pointAuthor: Player): Boolean {
        return when (pointAuthor) {
            Player.FIRST -> matchState.currentGame.secondPlayerScore == ADVANTAGE
            Player.SECOND -> matchState.currentGame.firstPlayerScore == ADVANTAGE
        }
    }
}
