package it.danielemegna.tennis.domain

import it.danielemegna.tennis.domain.MatchState.Game.GameScore.*
import it.danielemegna.tennis.domain.MatchState.Serving
import it.danielemegna.tennis.domain.usecase.PlayerPoint.Player
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MatchStateScannerTest {

    private val scanner = MatchStateScanner()

    @Nested
    inner class GameWinning {

        @Test
        fun `game won by first player`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(FORTY, FIFTEEN)
            )

            assertTrue(scanner.wouldBeGamePoint(matchState, Player.FIRST))
            assertFalse(scanner.wouldBeGamePoint(matchState, Player.SECOND))
            assertFalse(scanner.wouldBeSetPoint(matchState, Player.FIRST))
            assertFalse(scanner.wouldStartTieBreak(matchState, Player.FIRST))
        }

        @Test
        fun `game won by second player`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(THIRTY, FORTY)
            )

            assertTrue(scanner.wouldBeGamePoint(matchState, Player.SECOND))
            assertFalse(scanner.wouldBeGamePoint(matchState, Player.FIRST))
            assertFalse(scanner.wouldBeSetPoint(matchState, Player.SECOND))
            assertFalse(scanner.wouldStartTieBreak(matchState, Player.SECOND))
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

            assertTrue(scanner.wouldBeSetPoint(matchState, Player.FIRST))
            assertFalse(scanner.wouldBeSetPoint(matchState, Player.SECOND))
            assertTrue(scanner.wouldBeGamePoint(matchState, Player.FIRST))
            assertFalse(scanner.wouldStartTieBreak(matchState, Player.FIRST))
        }

        @Test
        fun `second player wins the set on six games`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 4, secondPlayerScore = 5),
                currentGame = MatchState.Game(FORTY, ADVANTAGE),
                serving = Serving.SECOND_PLAYER
            )

            assertTrue(scanner.wouldBeSetPoint(matchState, Player.SECOND))
            assertFalse(scanner.wouldBeSetPoint(matchState, Player.FIRST))
            assertTrue(scanner.wouldBeGamePoint(matchState, Player.SECOND))
            assertFalse(scanner.wouldStartTieBreak(matchState, Player.FIRST))
        }

        @Test
        fun `player do not wins the set on six games when five-all`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 5, secondPlayerScore = 5),
                currentGame = MatchState.Game(FORTY, FIFTEEN),
                serving = Serving.FIRST_PLAYER
            )

            assertFalse(scanner.wouldBeSetPoint(matchState, Player.SECOND))
            assertFalse(scanner.wouldBeSetPoint(matchState, Player.FIRST))
            assertTrue(scanner.wouldBeGamePoint(matchState, Player.FIRST))
            assertFalse(scanner.wouldStartTieBreak(matchState, Player.FIRST))
        }

        @Test
        fun `first player wins the set on seven games when second has five`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 6, secondPlayerScore = 5),
                currentGame = MatchState.Game(FORTY, THIRTY),
                serving = Serving.FIRST_PLAYER
            )

            assertTrue(scanner.wouldBeSetPoint(matchState, Player.FIRST))
            assertFalse(scanner.wouldBeSetPoint(matchState, Player.SECOND))
            assertTrue(scanner.wouldBeGamePoint(matchState, Player.FIRST))
            assertFalse(scanner.wouldStartTieBreak(matchState, Player.FIRST))
        }

        @Test
        fun `second player wins the set on seven games when first has five`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 5, secondPlayerScore = 6),
                currentGame = MatchState.Game(FORTY, ADVANTAGE),
                serving = Serving.FIRST_PLAYER
            )

            assertTrue(scanner.wouldBeSetPoint(matchState, Player.SECOND))
            assertFalse(scanner.wouldBeSetPoint(matchState, Player.FIRST))
            assertTrue(scanner.wouldBeGamePoint(matchState, Player.SECOND))
            assertFalse(scanner.wouldStartTieBreak(matchState, Player.FIRST))
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

            assertFalse(scanner.wouldStartTieBreak(matchState, Player.FIRST))
            assertTrue(scanner.wouldBeGamePoint(matchState, Player.FIRST))
            assertFalse(scanner.wouldBeSetPoint(matchState, Player.FIRST))
            assertFalse(scanner.wouldWinTieBreak(matchState, Player.FIRST))
        }

        @Test
        fun `it starts on six-all`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 6, secondPlayerScore = 5),
                currentGame = MatchState.Game(FIFTEEN, FORTY),
                serving = Serving.SECOND_PLAYER
            )
            assertTrue(scanner.wouldStartTieBreak(matchState, Player.SECOND))
            assertTrue(scanner.wouldBeGamePoint(matchState, Player.SECOND))
            assertFalse(scanner.wouldBeSetPoint(matchState, Player.SECOND))
            assertFalse(scanner.wouldWinTieBreak(matchState, Player.SECOND))
        }

        @Test
        fun `first player wins tie-break on seven points`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(
                    firstPlayerScore = 6,
                    secondPlayerScore = 6,
                    tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 6, 2)
                ),
                currentGame = MatchState.Game(ZERO, ZERO),
                serving = Serving.FIRST_PLAYER
            )

            assertTrue(scanner.wouldWinTieBreak(matchState, Player.FIRST))
            assertFalse(scanner.wouldWinTieBreak(matchState, Player.SECOND))
            assertTrue(scanner.wouldBeSetPoint(matchState, Player.FIRST))
            assertFalse(scanner.wouldStartTieBreak(matchState, Player.FIRST))
        }

        @Test
        fun `second player wins tie-break on seven points`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(
                    firstPlayerScore = 6,
                    secondPlayerScore = 6,
                    tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 5, 6)
                ),
                currentGame = MatchState.Game(ZERO, ZERO),
                serving = Serving.FIRST_PLAYER
            )

            assertTrue(scanner.wouldWinTieBreak(matchState, Player.SECOND))
            assertFalse(scanner.wouldWinTieBreak(matchState, Player.FIRST))
            assertTrue(scanner.wouldBeSetPoint(matchState, Player.SECOND))
            assertFalse(scanner.wouldStartTieBreak(matchState, Player.SECOND))
        }

        @Test
        fun `tie-break continues on seven-six`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(
                    firstPlayerScore = 6,
                    secondPlayerScore = 6,
                    tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 6, 6)
                ),
                currentGame = MatchState.Game(ZERO, ZERO),
                serving = Serving.FIRST_PLAYER
            )

            assertFalse(scanner.wouldWinTieBreak(matchState, Player.FIRST))
            assertFalse(scanner.wouldWinTieBreak(matchState, Player.SECOND))
            assertFalse(scanner.wouldBeSetPoint(matchState, Player.FIRST))
        }

        @Test
        fun `tie-break continues indefinitely on one-point score gap`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(
                    firstPlayerScore = 6,
                    secondPlayerScore = 6,
                    tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 13, 13)
                ),
                currentGame = MatchState.Game(ZERO, ZERO),
                serving = Serving.SECOND_PLAYER
            )

            assertFalse(scanner.wouldWinTieBreak(matchState, Player.SECOND))
            assertFalse(scanner.wouldWinTieBreak(matchState, Player.FIRST))
            assertFalse(scanner.wouldBeSetPoint(matchState, Player.SECOND))
        }

        @Test
        fun `first player wins tie-break on two-point score gap`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(
                    firstPlayerScore = 6,
                    secondPlayerScore = 6,
                    tieBreak = MatchState.TieBreak(Serving.SECOND_PLAYER, 9, 8)
                ),
                currentGame = MatchState.Game(ZERO, ZERO),
                serving = Serving.FIRST_PLAYER
            )

            assertTrue(scanner.wouldWinTieBreak(matchState, Player.FIRST))
            assertFalse(scanner.wouldWinTieBreak(matchState, Player.SECOND))
            assertTrue(scanner.wouldBeSetPoint(matchState, Player.FIRST))
        }

        @Test
        fun `second player wins tie-break on two-point score gap`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(
                    firstPlayerScore = 6,
                    secondPlayerScore = 6,
                    tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 14, 15)
                ),
                currentGame = MatchState.Game(ZERO, ZERO),
                serving = Serving.SECOND_PLAYER
            )

            assertTrue(scanner.wouldWinTieBreak(matchState, Player.SECOND))
            assertFalse(scanner.wouldWinTieBreak(matchState, Player.FIRST))
            assertTrue(scanner.wouldBeSetPoint(matchState, Player.SECOND))
        }

    }

    @Nested
    inner class AdvantagePoints {

        @Test
        fun `first player goes to advantage on point when forty-forty`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(FORTY, FORTY)
            )

            assertFalse(scanner.wouldCancelTheAdvantagePoint(matchState, Player.FIRST))
            assertFalse(scanner.wouldCancelTheAdvantagePoint(matchState, Player.SECOND))
        }

        @Test
        fun `players come back forty-forty on second player untapped advantage`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(FORTY, ADVANTAGE)
            )

            assertTrue(scanner.wouldCancelTheAdvantagePoint(matchState, Player.FIRST))
            assertFalse(scanner.wouldCancelTheAdvantagePoint(matchState, Player.SECOND))
        }

        @Test
        fun `players come back forty-forty on first player untapped advantage`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(ADVANTAGE, FORTY)
            )

            assertTrue(scanner.wouldCancelTheAdvantagePoint(matchState, Player.SECOND))
            assertFalse(scanner.wouldCancelTheAdvantagePoint(matchState, Player.FIRST))
        }

    }

    @Nested
    inner class NormalPoints {

        @Test
        fun `on new game`() {
            val matchState = MatchState("p1", "p2")
            nothingShouldBeRecognized(matchState)
        }

        @Test
        fun `on progress game`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(THIRTY, FIFTEEN)
            )

            nothingShouldBeRecognized(matchState)
        }

        @Test
        fun `first player close to set point`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(5, 4),
                currentGame = MatchState.Game(THIRTY, FIFTEEN)
            )

            nothingShouldBeRecognized(matchState)
        }

        @Test
        fun `set close to tiebreak`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 6, secondPlayerScore = 5),
                currentGame = MatchState.Game(ZERO, THIRTY),
                serving = Serving.SECOND_PLAYER
            )

            nothingShouldBeRecognized(matchState)
        }

        private fun nothingShouldBeRecognized(matchState: MatchState) {
            listOf(Player.FIRST, Player.SECOND).forEach { pointAuthor ->
                assertFalse(scanner.wouldBeGamePoint(matchState, pointAuthor))
                assertFalse(scanner.wouldBeSetPoint(matchState, pointAuthor))
                assertFalse(scanner.wouldStartTieBreak(matchState, pointAuthor))
                assertFalse(scanner.wouldCancelTheAdvantagePoint(matchState, pointAuthor))
                assertFalse(scanner.wouldWinTieBreak(matchState, pointAuthor))
            }
        }
    }

}
