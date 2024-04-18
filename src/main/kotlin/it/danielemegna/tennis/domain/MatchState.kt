package it.danielemegna.tennis.domain

data class MatchState(
    val firstPlayerName: String,
    val secondPlayerName: String,
    val serving: Serving,
    val currentGame: Game,
    // list of past sets
) {
    constructor(firstPlayerName: String, secondPlayerName: String) : this(
        firstPlayerName = firstPlayerName,
        secondPlayerName = secondPlayerName,
        serving = Serving.FIRST_PLAYER,
        currentGame = Game(
            firstPlayerScore = Game.GameScore.ZERO,
            secondPlayerScore = Game.GameScore.ZERO
        )
    )

    enum class Serving { FIRST_PLAYER, SECOND_PLAYER }

    data class Game(
        val firstPlayerScore: GameScore,
        val secondPlayerScore: GameScore,
    ) {
        enum class GameScore { ZERO, FIFTEEN, THIRTY, FORTY, ADVANTAGE }
    }
}