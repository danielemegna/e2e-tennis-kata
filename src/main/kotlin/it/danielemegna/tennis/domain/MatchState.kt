package it.danielemegna.tennis.domain

data class MatchState(
    val firstPlayerName: String,
    val secondPlayerName: String,
    val serving: Serving,
    val currentGame: Game,
    val currentSet: Set
    // list of past sets
) {
    constructor(firstPlayerName: String, secondPlayerName: String) : this(
        firstPlayerName = firstPlayerName,
        secondPlayerName = secondPlayerName,
        serving = Serving.FIRST_PLAYER,
        currentGame = Game(),
        currentSet = Set(),
    )

    enum class Serving { FIRST_PLAYER, SECOND_PLAYER;
        fun next() = entries[(this.ordinal + 1) % entries.size]
    }

    data class Game(
        val firstPlayerScore: GameScore = GameScore.ZERO,
        val secondPlayerScore: GameScore = GameScore.ZERO,
    ) {
        enum class GameScore {
            ZERO, FIFTEEN, THIRTY, FORTY, ADVANTAGE;

            fun next() = GameScore.entries[this.ordinal + 1]
        }
    }

    data class Set(
        val firstPlayerScore: Int = 0,
        val secondPlayerScore: Int = 0
    )
}