package it.danielemegna.tennis.web.view

import it.danielemegna.tennis.domain.MatchRelevantMoment
import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.usecase.LoadOrInitMatch
import it.danielemegna.tennis.domain.usecase.PlayerPoint

data class ScoreBoardView(
    val matchId: String,
    val isFirstPlayerServing: Boolean,
    val firstPlayerName: String,
    val secondPlayerName: String,
    val finishedSets: List<FinishedSet>,
    val firstPlayerCurrentSetScore: Int,
    val secondPlayerCurrentSetScore: Int,
    val firstPlayerCurrentGameScore: String,
    val secondPlayerCurrentGameScore: String,
    val infoTooltipText: String?
) {
    data class FinishedSet(
        val firstPlayerScore: Int,
        val secondPlayerScore: Int,
        val firstPlayerTieBreakScore: Int?,
        val secondPlayerTieBreakScore: Int?
    ) {
        @Suppress("unused", "used by freemarker view template")
        val firstPlayerCssClass = if (firstPlayerScore > secondPlayerScore) "won" else "lost"

        @Suppress("unused", "used by freemarker view template")
        val secondPlayerCssClass = if (secondPlayerScore > firstPlayerScore) "won" else "lost"
    }

    companion object {

        fun from(matchId: String, usecaseResult: LoadOrInitMatch.Result): ScoreBoardView {
            return from(matchId, usecaseResult.matchState, null)
        }

        fun from(matchId: String, usecaseResult: PlayerPoint.Result): ScoreBoardView {
            return from(matchId, usecaseResult.newMatchState, usecaseResult.matchRelevantMoment)
        }

        private fun from(
            matchId: String,
            matchState: MatchState,
            matchRelevantMoment: MatchRelevantMoment?
        ): ScoreBoardView = ScoreBoardView(
            matchId = matchId,
            isFirstPlayerServing = matchState.serving == MatchState.Serving.FIRST_PLAYER,
            firstPlayerName = matchState.firstPlayerName,
            secondPlayerName = matchState.secondPlayerName,
            finishedSets = matchState.wonSets.map {
                FinishedSet(
                    firstPlayerScore = it.firstPlayerScore,
                    secondPlayerScore = it.secondPlayerScore,
                    firstPlayerTieBreakScore = it.tieBreak.firstPlayerScoreToShow(),
                    secondPlayerTieBreakScore = it.tieBreak.secondPlayerScoreToShow()
                )
            },
            firstPlayerCurrentSetScore = matchState.currentSet.firstPlayerScore,
            secondPlayerCurrentSetScore = matchState.currentSet.secondPlayerScore,
            firstPlayerCurrentGameScore = matchState.firstPlayerCurrentGameScore(),
            secondPlayerCurrentGameScore = matchState.secondPlayerCurrentGameScore(),
            infoTooltipText = matchRelevantMoment.toInfoTooltipText()
        )

        private fun MatchState.firstPlayerCurrentGameScore(): String {
            if (this.currentSet.tieBreak != null)
                return this.currentSet.tieBreak.firstPlayerScore.toString()

            return this.currentGame.firstPlayerScore.toString()
        }

        private fun MatchState.secondPlayerCurrentGameScore(): String {
            if (this.currentSet.tieBreak != null)
                return this.currentSet.tieBreak.secondPlayerScore.toString()

            return this.currentGame.secondPlayerScore.toString()
        }

        private fun MatchState.TieBreak?.firstPlayerScoreToShow(): Int? {
            if (this == null) return null
            return onlyIfSmaller(firstPlayerScore, secondPlayerScore)
        }

        private fun MatchState.TieBreak?.secondPlayerScoreToShow(): Int? {
            if (this == null) return null
            return onlyIfSmaller(secondPlayerScore, firstPlayerScore)
        }

        private fun onlyIfSmaller(aTieBreakScore: Int?, otherTieBreakScore: Int?): Int? {
            if (aTieBreakScore == null || otherTieBreakScore == null) return null
            if (aTieBreakScore > otherTieBreakScore) return null
            return aTieBreakScore
        }

        private fun MatchRelevantMoment?.toInfoTooltipText(): String? {
            if(this == null) return null
            return when(this.kind) {
                MatchRelevantMoment.Kind.SET_POINT -> "${this.counter} SET POINTS"
            }
        }

    }
}

