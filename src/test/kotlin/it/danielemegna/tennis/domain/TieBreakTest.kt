package it.danielemegna.tennis.domain

import it.danielemegna.tennis.domain.MatchState.Serving.FIRST_PLAYER
import it.danielemegna.tennis.domain.MatchState.TieBreak
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TieBreakTest {

    @Test
    fun `on one total points serving changes (the serving after the first point changes)`() {
        val newTieBreak = aTieBreak(firstPlayerScore = 1, secondPlayerScore = 0)
        assertTrue(newTieBreak.shouldChangeServing())
    }

    @Test
    fun `on two total points serving does not change (the serving after the second point does not change)`() {
        var tieBreak = aTieBreak(firstPlayerScore = 2, secondPlayerScore = 0)
        assertFalse(tieBreak.shouldChangeServing())
        tieBreak = aTieBreak(firstPlayerScore = 1, secondPlayerScore = 1)
        assertFalse(tieBreak.shouldChangeServing())
    }

    @Test
    fun `on three total points serving changes (the serving after the third point changes)`() {
        var tieBreak = aTieBreak(firstPlayerScore = 2, secondPlayerScore = 1)
        assertTrue(tieBreak.shouldChangeServing())
        tieBreak = aTieBreak(firstPlayerScore = 0, secondPlayerScore = 3)
        assertTrue(tieBreak.shouldChangeServing())
    }

    @Test
    fun `on eleven total points serving changes (the serving after odd total points changes)`() {
        var tieBreak = aTieBreak(firstPlayerScore = 6, secondPlayerScore = 5)
        assertTrue(tieBreak.shouldChangeServing())
        tieBreak = aTieBreak(firstPlayerScore = 5, secondPlayerScore = 6)
        assertTrue(tieBreak.shouldChangeServing())
    }

    @Test
    fun `on eight total points serving does not change (the serving after even total points does not change)`() {
        var tieBreak = aTieBreak(firstPlayerScore = 6, secondPlayerScore = 2)
        assertFalse(tieBreak.shouldChangeServing())
        tieBreak = aTieBreak(firstPlayerScore = 3, secondPlayerScore = 5)
        assertFalse(tieBreak.shouldChangeServing())
    }

    @Test
    fun `on seventeen total points serving changes (the serving after odd total points changes)`() {
        var tieBreak = aTieBreak(firstPlayerScore = 9, secondPlayerScore = 8)
        assertTrue(tieBreak.shouldChangeServing())
        tieBreak = aTieBreak(firstPlayerScore = 8, secondPlayerScore = 9)
        assertTrue(tieBreak.shouldChangeServing())
    }

    @Test
    fun `on twenty total points serving does not change (the serving after even total points does not change)`() {
        val tieBreak = aTieBreak(firstPlayerScore = 10, secondPlayerScore = 10)
        assertFalse(tieBreak.shouldChangeServing())
    }

    private fun aTieBreak(firstPlayerScore: Int, secondPlayerScore: Int): TieBreak {
        return TieBreak(
            playerStartedTheTieBreak = FIRST_PLAYER,
            firstPlayerScore = firstPlayerScore,
            secondPlayerScore = secondPlayerScore
        )
    }

}
