package it.danielemegna.tennis.domain

import it.danielemegna.tennis.domain.MatchState.Game.GameScore.*
import it.danielemegna.tennis.domain.usecase.PlayerPoint
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
}