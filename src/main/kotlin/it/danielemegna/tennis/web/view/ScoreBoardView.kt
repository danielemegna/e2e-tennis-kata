package it.danielemegna.tennis.web.view

data class ScoreBoardView(
    val isFirstPlayerServing: Boolean,
    val firstPlayerName: String,
    val secondPlayerName: String,
    val firstPlayerCurrentSetScore: Int,
    val secondPlayerCurrentSetScore: Int,
    val firstPlayerCurrentGameScore: String,
    val secondPlayerCurrentGameScore: String,
)