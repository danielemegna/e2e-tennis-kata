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
        currentGame = Game()
    )

    enum class Serving { FIRST_PLAYER, SECOND_PLAYER }

    data class Game(
        val firstPlayerScore: GameScore = GameScore.ZERO,
        val secondPlayerScore: GameScore = GameScore.ZERO,
    ) {
        enum class GameScore {
            ZERO, FIFTEEN, THIRTY, FORTY, ADVANTAGE;

            fun next() = GameScore.entries[this.ordinal + 1]
        }
    }
}