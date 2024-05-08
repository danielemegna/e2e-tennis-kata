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
        val secondPlayerScore: Int
    ) {
        val wonByFirstPlayer = firstPlayerScore > secondPlayerScore
    }

    companion object {
        fun from(matchState: MatchState): ScoreBoardView = ScoreBoardView(
            isFirstPlayerServing = matchState.serving == MatchState.Serving.FIRST_PLAYER,
            firstPlayerName = matchState.firstPlayerName,
            secondPlayerName = matchState.secondPlayerName,
            finishedSets = matchState.wonSets.map { FinishedSet(it.firstPlayerScore, it.secondPlayerScore) },
            firstPlayerCurrentSetScore = matchState.currentSet.firstPlayerScore,
            secondPlayerCurrentSetScore = matchState.currentSet.secondPlayerScore,
            firstPlayerCurrentGameScore = matchState.currentGame.firstPlayerScore.toString(),
            secondPlayerCurrentGameScore = matchState.currentGame.secondPlayerScore.toString(),
        )
    }
}
