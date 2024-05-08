package it.danielemegna.tennis.web.view

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
}
