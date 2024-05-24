package it.danielemegna.tennis.domain

import it.danielemegna.tennis.domain.usecase.PlayerPoint
import kotlin.math.ceil

data class MatchState(
    val firstPlayerName: String,
    val secondPlayerName: String,
    val serving: Serving,
    val currentGame: Game,
    val currentSet: Set,
    val currentTieBreak: TieBreak?,
    val wonSets: List<Set>,
) {

    constructor(firstPlayerName: String, secondPlayerName: String) : this(
        firstPlayerName = firstPlayerName,
        secondPlayerName = secondPlayerName,
        serving = Serving.FIRST_PLAYER,
        currentGame = Game(),
        currentSet = Set(),
        currentTieBreak = null,
        wonSets = emptyList()
    )

    fun tieBreakInProgress() = currentTieBreak != null

    enum class Serving {
        FIRST_PLAYER, SECOND_PLAYER;

        fun next() = entries[(this.ordinal + 1) % entries.size]

        fun nextFor(tieBreak: TieBreak): Serving {
            val totalTieBreakPoints = tieBreak.let { it.firstPlayerScore + it.secondPlayerScore }
            if (totalTieBreakPoints % 2 == 0) return this
            return next()
        }

        fun playerStartedTheTieBreak(tieBreak: TieBreak): Serving {
            val totalTieBreakPoints = tieBreak.let { it.firstPlayerScore + it.secondPlayerScore }
            val totalPointsParity = (ceil(totalTieBreakPoints / 2.0).toInt()) % 2
            return if (totalPointsParity == 0) this else next()
        }
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

    data class TieBreak(
        val firstPlayerScore: Int = 0,
        val secondPlayerScore: Int = 0
    ) {
        fun increaseScore(pointAuthor: PlayerPoint.Player): TieBreak {
            return when (pointAuthor) {
                PlayerPoint.Player.FIRST -> this.copy(firstPlayerScore = firstPlayerScore + 1)
                PlayerPoint.Player.SECOND -> this.copy(secondPlayerScore = secondPlayerScore + 1)
            }
        }

        fun playerStartedTheTieBreak(currentServing: Serving): Serving {
            val totalPoints = firstPlayerScore + secondPlayerScore
            val totalPointsParity = (ceil(totalPoints / 2.0).toInt()) % 2
            if (totalPointsParity == 0)
                return currentServing

            return currentServing.next()
        }
    }

}
