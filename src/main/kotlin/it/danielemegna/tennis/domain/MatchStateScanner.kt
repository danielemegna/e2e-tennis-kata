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
        if(!wouldBeGamePoint(matchState, pointAuthor)) return false

        val currentSet = matchState.currentSet
        return when (pointAuthor) {
            Player.FIRST ->
                currentSet.firstPlayerScore >= 5 && currentSet.secondPlayerScore < currentSet.firstPlayerScore

            Player.SECOND ->
                currentSet.secondPlayerScore >= 5 && currentSet.firstPlayerScore < currentSet.secondPlayerScore
        }

    }
}