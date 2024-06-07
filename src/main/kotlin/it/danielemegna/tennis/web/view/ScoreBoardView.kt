package it.danielemegna.tennis.web.view

import it.danielemegna.tennis.domain.MatchState

data class ScoreBoardView(
    val isFirstPlayerServing: Boolean,
    val firstPlayerName: String,
    val secondPlayerName: String,
    val finishedSets: List<FinishedSet>,
    val firstPlayerCurrentSetScore: Int,
    val secondPlayerCurrentSetScore: Int,
    val firstPlayerCurrentGameScore: String,
    val secondPlayerCurrentGameScore: String,
) {
    data class FinishedSet(
        val firstPlayerScore: Int,
        val secondPlayerScore: Int,
        val firstPlayerTieBreakScore: Int?,
        val secondPlayerTieBreakScore: Int?
    ) {
        @Suppress("unused", "used by freemarker view template")
        val firstPlayerCssClass = if(firstPlayerScore > secondPlayerScore) "won" else "lost"
        @Suppress("unused", "used by freemarker view template")
        val secondPlayerCssClass = if(secondPlayerScore > firstPlayerScore) "won" else "lost"
    }

    companion object {
        fun from(matchState: MatchState): ScoreBoardView = ScoreBoardView(
            isFirstPlayerServing = matchState.serving == MatchState.Serving.FIRST_PLAYER,
            firstPlayerName = matchState.firstPlayerName,
            secondPlayerName = matchState.secondPlayerName,
            finishedSets = matchState.wonSets.map {
                FinishedSet(
                    firstPlayerScore = it.firstPlayerScore,
                    secondPlayerScore = it.secondPlayerScore,
                    firstPlayerTieBreakScore = it.firstPlayerTieBreakScore,
                    secondPlayerTieBreakScore = it.secondPlayerTieBreakScore
                )
            },
            firstPlayerCurrentSetScore = matchState.currentSet.firstPlayerScore,
            secondPlayerCurrentSetScore = matchState.currentSet.secondPlayerScore,
            firstPlayerCurrentGameScore = matchState.firstPlayerCurrentGameScore(),
            secondPlayerCurrentGameScore = matchState.secondPlayerCurrentGameScore(),
        )

        private fun MatchState.firstPlayerCurrentGameScore(): String {
            if (this.currentTieBreak != null)
                return this.currentTieBreak.firstPlayerScore.toString()

            return this.currentGame.firstPlayerScore.toString()
        }

        private fun MatchState.secondPlayerCurrentGameScore(): String {
            if (this.currentTieBreak != null)
                return this.currentTieBreak.secondPlayerScore.toString()

            return this.currentGame.secondPlayerScore.toString()
        }
    }
}
