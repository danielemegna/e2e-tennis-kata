package it.danielemegna.tennis.domain

data class MatchState(
    val firstPlayerName: String,
    val secondPlayerName: String,
    val serving: Serving,
    val currentGame: Game,
    // list of past sets
) {
    enum class Serving { FIRST_PLAYER, SECOND_PLAYER }

    data class Game(
        val firstPlayerScore: GameScore,
        val secondPlayerScore: GameScore,
    ) {
        enum class GameScore { ZERO, FIFTEEN, THIRTY, FORTY, ADVANTAGE }
    }
}