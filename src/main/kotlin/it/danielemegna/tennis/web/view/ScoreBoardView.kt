package it.danielemegna.tennis.web.view

data class ScoreBoardView(
    val firstPlayerName: String,
    val secondPlayerName: String,
    val isFirstPlayerServing: Boolean,
    val firstPlayerCurrentGameScore: Int,
    val secondPlayerCurrentGameScore: Int,
    val firstPlayerCurrentSetScore: Int,
    val secondPlayerCurrentSetScore: Int,
)