package it.danielemegna.tennis.domain

import it.danielemegna.tennis.domain.MatchState.*
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.*
import it.danielemegna.tennis.domain.MatchState.Set
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MatchRelevantMomentTest {

    @Test
    fun `from a new match state`() {
        val matchState = MatchState("p1", "p2")
        val actual = MatchRelevantMoment.from(matchState)
        assertThat(actual).isNull()
    }

    @Test
    fun `a normal game point`() {
        val matchState = MatchState("p1", "p2").copy(
            currentGame = Game(FORTY, FIFTEEN),
            currentSet = Set(firstPlayerScore = 2, secondPlayerScore = 2)
        )
        val actual = MatchRelevantMoment.from(matchState)
        assertThat(actual).isNull()
    }

    @Test
    fun `one set point is not a relevant moment`() {
        val matchState = MatchState("p1", "p2").copy(
            currentGame = Game(FORTY, THIRTY),
            currentSet = Set(firstPlayerScore = 5, secondPlayerScore = 2)
        )
        val actual = MatchRelevantMoment.from(matchState)
        assertThat(actual).isNull()
    }

    @Test
    fun `two set point`() {
        val matchState = MatchState("p1", "p2").copy(
            currentGame = Game(FORTY, FIFTEEN),
            currentSet = Set(firstPlayerScore = 5, secondPlayerScore = 2)
        )

        val actual = MatchRelevantMoment.from(matchState)

        val expected = MatchRelevantMoment(kind = MatchRelevantMoment.Kind.SET_POINT, counter = 2)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `three set point`() {
        val matchState = MatchState("p1", "p2").copy(
            currentGame = Game(ZERO, FORTY),
            currentSet = Set(firstPlayerScore = 4, secondPlayerScore = 5)
        )

        val actual = MatchRelevantMoment.from(matchState)

        val expected = MatchRelevantMoment(kind = MatchRelevantMoment.Kind.SET_POINT, counter = 3)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `one set point at tiebreak is a relevant moment`() {
        val matchState = MatchState("p1", "p2").copy(
            currentSet = Set(
                firstPlayerScore = 6,
                secondPlayerScore = 6,
                tieBreak = TieBreak(Serving.FIRST_PLAYER, 8, 9)
            ),
        )

        val actual = MatchRelevantMoment.from(matchState)

        val expected = MatchRelevantMoment(kind = MatchRelevantMoment.Kind.SET_POINT, counter = 1)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `four set point at tiebreak`() {
        val matchState = MatchState("p1", "p2").copy(
            currentSet = Set(
                firstPlayerScore = 6,
                secondPlayerScore = 6,
                tieBreak = TieBreak(Serving.FIRST_PLAYER, 6, 2)
            ),
        )

        val actual = MatchRelevantMoment.from(matchState)

        val expected = MatchRelevantMoment(kind = MatchRelevantMoment.Kind.SET_POINT, counter = 4)
        assertThat(actual).isEqualTo(expected)
    }

}
