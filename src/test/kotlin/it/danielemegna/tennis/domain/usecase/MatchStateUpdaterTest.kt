package it.danielemegna.tennis.domain.usecase

import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.*
import it.danielemegna.tennis.domain.MatchState.Serving
import it.danielemegna.tennis.domain.MatchStateUpdater
import org.junit.jupiter.api.Assertions.*
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

        @Test
        fun `first player close to set point`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(5, 4),
                currentGame = MatchState.Game(THIRTY, FIFTEEN)
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(FORTY, updatedMatchState.currentGame.firstPlayerScore)
            assertEquals(FIFTEEN, updatedMatchState.currentGame.secondPlayerScore)
            assertEquals(MatchState.Set(5, 4), updatedMatchState.currentSet)
            assertEquals(emptyList(), updatedMatchState.wonSets)
        }

        @Test
        fun `point to cancel a set point`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(5, 4),
                currentGame = MatchState.Game(FORTY, FIFTEEN)
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(FORTY, updatedMatchState.currentGame.firstPlayerScore)
            assertEquals(THIRTY, updatedMatchState.currentGame.secondPlayerScore)
            assertEquals(MatchState.Set(5, 4), updatedMatchState.currentSet)
            assertEquals(emptyList(), updatedMatchState.wonSets)
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
            assertEquals(emptyList(), updatedMatchState.wonSets)
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

    @Nested
    inner class SetWinning {

        @Test
        fun `first player wins the set on six games`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 5, secondPlayerScore = 1),
                currentGame = MatchState.Game(FORTY, ZERO),
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            val expectedWonSet = MatchState.Set(firstPlayerScore = 6, secondPlayerScore = 1)
            assertEquals(listOf(expectedWonSet), updatedMatchState.wonSets)
            assertEquals(0, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `second player wins the set on six games`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 4, secondPlayerScore = 5),
                currentGame = MatchState.Game(FORTY, ADVANTAGE),
                serving = Serving.SECOND_PLAYER
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            val expectedWonSet = MatchState.Set(firstPlayerScore = 4, secondPlayerScore = 6)
            assertEquals(listOf(expectedWonSet), updatedMatchState.wonSets)
            assertEquals(0, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
            assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `player do not wins the set on six games when five-all`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 5, secondPlayerScore = 5),
                currentGame = MatchState.Game(FORTY, FIFTEEN),
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(6, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(5, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(emptyList(), updatedMatchState.wonSets)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `first player wins the set on seven games when second has five`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 6, secondPlayerScore = 5),
                currentGame = MatchState.Game(FORTY, THIRTY),
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            val expectedWonSet = MatchState.Set(firstPlayerScore = 7, secondPlayerScore = 5)
            assertEquals(listOf(expectedWonSet), updatedMatchState.wonSets)
            assertEquals(MatchState.Set(0, 0), updatedMatchState.currentSet)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `second player wins the set on seven games when first has five`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 5, secondPlayerScore = 6),
                currentGame = MatchState.Game(FORTY, ADVANTAGE),
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            val expectedWonSet = MatchState.Set(firstPlayerScore = 5, secondPlayerScore = 7)
            assertEquals(listOf(expectedWonSet), updatedMatchState.wonSets)
            assertEquals(MatchState.Set(0, 0), updatedMatchState.currentSet)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }
    }

    @Nested
    inner class TieBreak {

        @Test
        fun `still not tiebreak on six-five`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 5, secondPlayerScore = 5),
                currentGame = MatchState.Game(FORTY, THIRTY),
            )
            assertNull(matchState.currentTieBreak)

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertNull(matchState.currentTieBreak)
            assertEquals(6, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(5, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(emptyList(), updatedMatchState.wonSets)
        }

        @Test
        fun `it starts on six-all`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 6, secondPlayerScore = 5),
                currentGame = MatchState.Game(FIFTEEN, FORTY),
                serving = Serving.SECOND_PLAYER
            )
            assertNull(matchState.currentTieBreak)

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(MatchState.TieBreak(0, 0), updatedMatchState.currentTieBreak)
            assertEquals(6, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(6, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(emptyList(), updatedMatchState.wonSets)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
            assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `during tie break points increase tie-break score instead of game score`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(6, 6),
                currentTieBreak = MatchState.TieBreak(firstPlayerScore = 0, secondPlayerScore = 0),
                currentGame = MatchState.Game(ZERO, ZERO),
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(MatchState.TieBreak(1, 0), updatedMatchState.currentTieBreak)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
            assertEquals(MatchState.Set(6, 6), updatedMatchState.currentSet)
            assertEquals(emptyList(), updatedMatchState.wonSets)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `after first point the serving is changed every two point`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(6, 6),
                currentTieBreak = MatchState.TieBreak(firstPlayerScore = 1, secondPlayerScore = 0),
                currentGame = MatchState.Game(ZERO, ZERO),
                serving = Serving.SECOND_PLAYER
            )

            val updatedMatchState = updater.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(MatchState.TieBreak(1, 1), updatedMatchState.currentTieBreak)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }

    }

}
