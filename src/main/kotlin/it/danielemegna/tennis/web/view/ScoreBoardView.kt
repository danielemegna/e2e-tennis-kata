package it.danielemegna.tennis.web.view

data class ScoreBoardView(
    val firstPlayerName: String,
    val secondPlayerName: String,
    val isFirstPlayerServing: Boolean,
    val firstPlayerCurrentGameScore: String,
    val secondPlayerCurrentGameScore: String,
    val firstPlayerCurrentSetScore: Int,
    val secondPlayerCurrentSetScore: Int,
)