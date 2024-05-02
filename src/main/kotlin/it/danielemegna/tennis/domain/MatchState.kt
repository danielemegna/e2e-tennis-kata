package it.danielemegna.tennis.domain

import it.danielemegna.tennis.domain.usecase.PlayerPoint

data class MatchState(
    val firstPlayerName: String,
    val secondPlayerName: String,
    val serving: Serving,
    val currentGame: Game,
    val currentSet: Set,
    val wonSets: List<Set>
) {
    constructor(firstPlayerName: String, secondPlayerName: String) : this(
        firstPlayerName = firstPlayerName,
        secondPlayerName = secondPlayerName,
        serving = Serving.FIRST_PLAYER,
        currentGame = Game(),
        currentSet = Set(),
        wonSets = emptyList()
    )

    enum class Serving {
        FIRST_PLAYER, SECOND_PLAYER;
        fun next() = entries[(this.ordinal + 1) % entries.size]
    }

    data class Game(
        val firstPlayerScore: GameScore = GameScore.ZERO,
        val secondPlayerScore: GameScore = GameScore.ZERO,
    ) {
        fun increaseScore(pointAuthor: PlayerPoint.Player): Game {
            return when (pointAuthor) {
                PlayerPoint.Player.FIRST -> this.copy(firstPlayerScore = firstPlayerScore.increase())
                PlayerPoint.Player.SECOND -> this.copy(secondPlayerScore = secondPlayerScore.increase())
            }
        }

        enum class GameScore {
            ZERO, FIFTEEN, THIRTY, FORTY, ADVANTAGE;

            fun increase() = entries[this.ordinal + 1]

            override fun toString() = when (this) {
                ZERO -> "0"
                FIFTEEN -> "15"
                THIRTY -> "30"
                FORTY -> "40"
                ADVANTAGE -> "A"
            }
        }
    }

    data class Set(
        val firstPlayerScore: Int = 0,
        val secondPlayerScore: Int = 0
    ) {
        fun increaseScore(pointAuthor: PlayerPoint.Player): Set {
            return when (pointAuthor) {
                PlayerPoint.Player.FIRST -> this.copy(firstPlayerScore = firstPlayerScore + 1)
                PlayerPoint.Player.SECOND -> this.copy(secondPlayerScore = secondPlayerScore + 1)
            }
        }
    }
}