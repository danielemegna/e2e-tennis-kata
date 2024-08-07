package it.danielemegna.tennis.domain

import it.danielemegna.tennis.domain.MatchState.Game.GameScore
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.FORTY
import it.danielemegna.tennis.domain.usecase.PlayerPoint
import kotlin.math.abs

data class MatchRelevantMoment(
    val kind: Kind,
    val counter: Int? = null
) {
    companion object {
        fun from(matchState: MatchState): MatchRelevantMoment? {
            val matchStateScanner = MatchStateScanner()
            val currentGameDifference = abs(
                matchState.currentGame.firstPlayerScore - matchState.currentGame.secondPlayerScore
            )

            if (matchStateScanner.wouldWinTieBreak(matchState)) {
                val tieBreak = matchState.currentSet.tieBreak!!
                val tieBreakDifference = abs(tieBreak.firstPlayerScore - tieBreak.secondPlayerScore)
                return MatchRelevantMoment(Kind.SET_POINT, tieBreakDifference)
            }

            if (matchStateScanner.wouldBeSetPoint(matchState) && currentGameDifference > 1)
                return MatchRelevantMoment(Kind.SET_POINT, currentGameDifference)

            return null
        }

    }

    enum class Kind { SET_POINT }
}

private fun MatchStateScanner.wouldWinTieBreak(matchState: MatchState): Boolean {
    return wouldWinTieBreak(matchState, PlayerPoint.Player.FIRST) ||
        wouldWinTieBreak(matchState, PlayerPoint.Player.SECOND)
}

private fun MatchStateScanner.wouldBeSetPoint(matchState: MatchState): Boolean {
    return wouldBeSetPoint(matchState, PlayerPoint.Player.FIRST) ||
        wouldBeSetPoint(matchState, PlayerPoint.Player.SECOND)
}

private operator fun GameScore.minus(other: GameScore): Int {
    return this.ordinal - other.ordinal
}

