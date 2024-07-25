package it.danielemegna.tennis.domain

import it.danielemegna.tennis.domain.MatchState.Game.GameScore.FORTY
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.THIRTY

data class MatchRelevantMoment(
    val kind: Kind,
    val counter: Int? = null
) {

    companion object {
        fun from(matchState: MatchState): MatchRelevantMoment? {
            if (closeToSetPoint(matchState)) {
                return MatchRelevantMoment(Kind.SET_POINT, 2)
            }

            return null
        }

        private fun closeToSetPoint(matchState: MatchState): Boolean {
            return matchState.currentGame.firstPlayerScore == FORTY &&
                matchState.currentGame.secondPlayerScore < THIRTY
        }
    }

    enum class Kind { SET_POINT }
}
