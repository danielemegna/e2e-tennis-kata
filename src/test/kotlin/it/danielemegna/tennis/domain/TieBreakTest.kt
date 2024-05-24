package it.danielemegna.tennis.domain

import it.danielemegna.tennis.domain.MatchState.Serving.FIRST_PLAYER
import it.danielemegna.tennis.domain.MatchState.Serving.SECOND_PLAYER
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TieBreakTest {

    @Nested
    inner class InferThePlayerWhoStartedTheTieBreak {

        @Test
        fun `on just started tie-break`() {
            val newTieBreak = MatchState.TieBreak(firstPlayerScore = 0, secondPlayerScore = 0)
            assertEquals(FIRST_PLAYER, newTieBreak.playerStartedTheTieBreak(currentServing = FIRST_PLAYER))
            assertEquals(SECOND_PLAYER, newTieBreak.playerStartedTheTieBreak(currentServing = SECOND_PLAYER))
        }

        @Test
        fun `on first point`() {
            var tieBreak = MatchState.TieBreak(firstPlayerScore = 1, secondPlayerScore = 0)
            assertEquals(FIRST_PLAYER, tieBreak.playerStartedTheTieBreak(currentServing = SECOND_PLAYER))

            tieBreak = MatchState.TieBreak(firstPlayerScore = 0, secondPlayerScore = 1)
            assertEquals(SECOND_PLAYER, tieBreak.playerStartedTheTieBreak(currentServing = FIRST_PLAYER))
        }

        @Test
        fun `after eight points is currently serving (1 + 2 + 2 + 2 + 1)`() {
            var tieBreak = MatchState.TieBreak(firstPlayerScore = 5, secondPlayerScore = 3)
            assertEquals(FIRST_PLAYER, tieBreak.playerStartedTheTieBreak(currentServing = FIRST_PLAYER))

            tieBreak = MatchState.TieBreak(firstPlayerScore = 4, secondPlayerScore = 4)
            assertEquals(SECOND_PLAYER, tieBreak.playerStartedTheTieBreak(currentServing = SECOND_PLAYER))
        }

        @Test
        fun `after eleven points is currently serving (1 + 2 + 2 + 2 + 2 + 2)`() {
            var tieBreak = MatchState.TieBreak(firstPlayerScore = 6, secondPlayerScore = 5)
            assertEquals(FIRST_PLAYER, tieBreak.playerStartedTheTieBreak(currentServing = FIRST_PLAYER))

            tieBreak = MatchState.TieBreak(firstPlayerScore = 5, secondPlayerScore = 6)
            assertEquals(SECOND_PLAYER, tieBreak.playerStartedTheTieBreak(currentServing = SECOND_PLAYER))
        }

        @Test
        fun `after six points is NOT currently serving (1 + 2 + 2 + 1)`() {
            var tieBreak = MatchState.TieBreak(firstPlayerScore = 5, secondPlayerScore = 1)
            assertEquals(FIRST_PLAYER, tieBreak.playerStartedTheTieBreak(currentServing = SECOND_PLAYER))

            tieBreak = MatchState.TieBreak(firstPlayerScore = 3, secondPlayerScore = 3)
            assertEquals(SECOND_PLAYER, tieBreak.playerStartedTheTieBreak(currentServing = FIRST_PLAYER))
        }

        @Test
        fun `after nine points is NOT currently serving (1 + 2 + 2 + 2 + 2)`() {
            var tieBreak = MatchState.TieBreak(firstPlayerScore = 5, secondPlayerScore = 4)
            assertEquals(FIRST_PLAYER, tieBreak.playerStartedTheTieBreak(currentServing = SECOND_PLAYER))

            tieBreak = MatchState.TieBreak(firstPlayerScore = 3, secondPlayerScore = 6)
            assertEquals(SECOND_PLAYER, tieBreak.playerStartedTheTieBreak(currentServing = FIRST_PLAYER))
        }

    }

    @Nested
    inner class InferPlayerWillServeInTheNextGame {

        @Test
        fun `on zero total points serving changes (the serving after the first point changes)`() {
            val newTieBreak = MatchState.TieBreak(firstPlayerScore = 0, secondPlayerScore = 0)
            assertEquals(FIRST_PLAYER, newTieBreak.nextServing(currentServing = SECOND_PLAYER))
            assertEquals(SECOND_PLAYER, newTieBreak.nextServing(currentServing = FIRST_PLAYER))
        }

        @Test
        fun `on one total points serving does not change (the serving after the second point does not change)`() {
            var tieBreak = MatchState.TieBreak(firstPlayerScore = 1, secondPlayerScore = 0)
            assertEquals(FIRST_PLAYER, tieBreak.nextServing(currentServing = FIRST_PLAYER))
            tieBreak = MatchState.TieBreak(firstPlayerScore = 0, secondPlayerScore = 1)
            assertEquals(SECOND_PLAYER, tieBreak.nextServing(currentServing = SECOND_PLAYER))
        }

        @Test
        fun `on two total points serving changes (the serving after the third point changes)`() {
            var tieBreak = MatchState.TieBreak(firstPlayerScore = 1, secondPlayerScore = 1)
            assertEquals(FIRST_PLAYER, tieBreak.nextServing(currentServing = SECOND_PLAYER))
            tieBreak = MatchState.TieBreak(firstPlayerScore = 0, secondPlayerScore = 2)
            assertEquals(SECOND_PLAYER, tieBreak.nextServing(currentServing = FIRST_PLAYER))
        }

        @Test
        fun `on ten total points serving changes (the serving after odd total points changes)`() {
            var tieBreak = MatchState.TieBreak(firstPlayerScore = 6, secondPlayerScore = 4)
            assertEquals(FIRST_PLAYER, tieBreak.nextServing(currentServing = SECOND_PLAYER))
            tieBreak = MatchState.TieBreak(firstPlayerScore = 5, secondPlayerScore = 5)
            assertEquals(SECOND_PLAYER, tieBreak.nextServing(currentServing = FIRST_PLAYER))
        }

        @Test
        fun `on seven total points serving does not change (the serving after even total points does not change)`() {
            var tieBreak = MatchState.TieBreak(firstPlayerScore = 5, secondPlayerScore = 2)
            assertEquals(FIRST_PLAYER, tieBreak.nextServing(currentServing = FIRST_PLAYER))
            tieBreak = MatchState.TieBreak(firstPlayerScore = 1, secondPlayerScore = 6)
            assertEquals(SECOND_PLAYER, tieBreak.nextServing(currentServing = SECOND_PLAYER))
        }

        @Test
        fun `on sixteen total points serving changes (the serving after odd total points changes)`() {
            val tieBreak = MatchState.TieBreak(firstPlayerScore = 8, secondPlayerScore = 8)
            assertEquals(FIRST_PLAYER, tieBreak.nextServing(currentServing = SECOND_PLAYER))
        }

        @Test
        fun `on nineteen total points serving does not change (the serving after even total points does not change)`() {
            val tieBreak = MatchState.TieBreak(firstPlayerScore = 10, secondPlayerScore = 9)
            assertEquals(FIRST_PLAYER, tieBreak.nextServing(currentServing = FIRST_PLAYER))
        }
    }

}
