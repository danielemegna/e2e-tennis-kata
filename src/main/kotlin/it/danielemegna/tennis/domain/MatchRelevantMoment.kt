package it.danielemegna.tennis.domain

import it.danielemegna.tennis.domain.MatchState.Game.GameScore
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.FORTY
import kotlin.math.abs

data class MatchRelevantMoment(
    val kind: Kind,
    val counter: Int? = null
) {
    companion object {
        fun from(matchState: MatchState): MatchRelevantMoment? {
            val isAnyoneForty =
                matchState.currentGame.firstPlayerScore == FORTY || matchState.currentGame.secondPlayerScore == FORTY
            val currentGameDifference =
                abs(matchState.currentGame.firstPlayerScore - matchState.currentGame.secondPlayerScore)
            val isAnyoneOverFour =
                matchState.currentSet.firstPlayerScore > 4 || matchState.currentSet.secondPlayerScore > 4
            if (isAnyoneForty && isAnyoneOverFour && currentGameDifference > 1) {
                return MatchRelevantMoment(Kind.SET_POINT, currentGameDifference)
            }

            return null
        }

    }

    enum class Kind { SET_POINT }
}

private operator fun GameScore.minus(other: GameScore): Int {
    return this.ordinal - other.ordinal
}

