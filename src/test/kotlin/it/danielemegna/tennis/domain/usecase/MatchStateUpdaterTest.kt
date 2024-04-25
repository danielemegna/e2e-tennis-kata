package it.danielemegna.tennis.domain.usecase

import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.*
import it.danielemegna.tennis.domain.MatchState.Serving
import it.danielemegna.tennis.domain.MatchStateUpdater
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MatchStateUpdaterTest {

    private val updater = MatchStateUpdater()

    @Nested
    inner class GameNormalPoint {

        @Test
        fun `first player point on new game`() {
            val newMatchState = MatchState("p1", "p2")

            val updatedMatchState = updater.updatedMatch(newMatchState, PlayerPoint.Player.FIRST)

            assertEquals(FIFTEEN, updatedMatchState.currentGame.firstPlayerScore)
            assertEquals(ZERO, updatedMatchState.currentGame.secondPlayerScore)
        }

        @Test
        fun `second player point on new game`() {
            val newMatchState = MatchState("p1", "p2")

            val updatedMatchState = updater.updatedMatch(newMatchState, PlayerPoint.Player.SECOND)

            assertEquals(FIFTEEN, updatedMatchState.currentGame.secondPlayerScore)
            assertEquals(ZERO, updatedMatchState.currentGame.firstPlayerScore)
        }

        @Test
        fun `first player point on progress game`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(THIRTY, FIFTEEN)
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(FORTY, updatedMatchState.currentGame.firstPlayerScore)
            assertEquals(FIFTEEN, updatedMatchState.currentGame.secondPlayerScore)
        }

        @Test
        fun `second player point on progress game`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(THIRTY, FIFTEEN)
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(THIRTY, updatedMatchState.currentGame.secondPlayerScore)
            assertEquals(THIRTY, updatedMatchState.currentGame.firstPlayerScore)
        }

    }

    @Nested
    inner class GameWinning {

        @Test
        fun `game won by first player`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(FORTY, FIFTEEN)
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(1, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
        }

        @Test
        fun `game won by second player`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(THIRTY, FORTY)
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(1, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
        }

    }

    @Nested
    inner class AdvantagePoints {

        @Test
        fun `first player goes to advantage on point when forty-forty`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(FORTY, FORTY)
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(MatchState.Game(ADVANTAGE, FORTY), updatedMatchState.currentGame)
            assertEquals(0, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.secondPlayerScore)
        }

        @Test
        fun `second player goes to advantage on point when forty-forty`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(FORTY, FORTY)
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(MatchState.Game(FORTY, ADVANTAGE), updatedMatchState.currentGame)
            assertEquals(0, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.secondPlayerScore)
        }

        @Test
        fun `players come back forty-forty on first player untapped advantage`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(ADVANTAGE, FORTY)
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(MatchState.Game(FORTY, FORTY), updatedMatchState.currentGame)
            assertEquals(0, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.secondPlayerScore)
        }

        @Test
        fun `players come back forty-forty on second player untapped advantage`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(FORTY, ADVANTAGE)
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(MatchState.Game(FORTY, FORTY), updatedMatchState.currentGame)
            assertEquals(0, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.secondPlayerScore)
        }

        @Test
        fun `first player win game after advantage point`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(ADVANTAGE, FORTY)
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(1, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
        }

        @Test
        fun `second player win game after advantage point`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(FORTY, ADVANTAGE)
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(1, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
        }

    }

    @Nested
    inner class ServingPlayerUpdate {

        @Test
        fun `first player still serving in ongoing first game`() {
            val matchState = MatchState("p1", "p2").copy(
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `second player serve when a game served by first player is completed`() {
            val matchState = MatchState("p1", "p2").copy(
                serving = Serving.FIRST_PLAYER,
                currentGame = MatchState.Game(FORTY, ZERO)
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `first player serve when a game served by second player is completed`() {
            val matchState = MatchState("p1", "p2").copy(
                serving = Serving.SECOND_PLAYER,
                currentGame = MatchState.Game(FIFTEEN, FORTY)
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `first player serve also when a game served by second player is won by first player`() {
            val matchState = MatchState("p1", "p2").copy(
                serving = Serving.SECOND_PLAYER,
                currentGame = MatchState.Game(FORTY, ZERO)
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
        }

    }

}