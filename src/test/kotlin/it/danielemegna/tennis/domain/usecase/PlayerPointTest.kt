package it.danielemegna.tennis.domain.usecase

import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.MatchState.Game.GameScore
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.*
import it.danielemegna.tennis.domain.MatchState.Serving
import it.danielemegna.tennis.domain.repository.StubMatchRepository
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PlayerPointTest {

    @Test
    fun `first player point on new game`() {
        val newMatchState = MatchState("p1", "p2")

        val updatedMatchState = updatedMatchStateFor(newMatchState, PlayerPoint.Player.FIRST)

        assertEquals(FIFTEEN, updatedMatchState.currentGame.firstPlayerScore)
        assertEquals(ZERO, updatedMatchState.currentGame.secondPlayerScore)
    }

    @Test
    fun `second player point on new game`() {
        val newMatchState = MatchState("p1", "p2")

        val updatedMatchState = updatedMatchStateFor(newMatchState, PlayerPoint.Player.SECOND)

        assertEquals(FIFTEEN, updatedMatchState.currentGame.secondPlayerScore)
        assertEquals(ZERO, updatedMatchState.currentGame.firstPlayerScore)
    }

    @Test
    fun `first player point on progress game`() {
        val matchState = MatchState("p1", "p2").copy(
            currentGame = MatchState.Game(THIRTY, FIFTEEN)
        )

        val updatedMatchState = updatedMatchStateFor(matchState, PlayerPoint.Player.FIRST)

        assertEquals(FORTY, updatedMatchState.currentGame.firstPlayerScore)
        assertEquals(FIFTEEN, updatedMatchState.currentGame.secondPlayerScore)
    }

    @Test
    fun `second player point on progress game`() {
        val matchState = MatchState("p1", "p2").copy(
            currentGame = MatchState.Game(THIRTY, FIFTEEN)
        )

        val updatedMatchState = updatedMatchStateFor(matchState, PlayerPoint.Player.SECOND)

        assertEquals(THIRTY, updatedMatchState.currentGame.secondPlayerScore)
        assertEquals(THIRTY, updatedMatchState.currentGame.firstPlayerScore)
    }

    @Test
    fun `game won by first player`() {
        val matchState = MatchState("p1", "p2").copy(
            currentGame = MatchState.Game(FORTY, FIFTEEN)
        )

        val updatedMatchState = updatedMatchStateFor(matchState, PlayerPoint.Player.FIRST)

        assertEquals(1, updatedMatchState.currentSet.firstPlayerScore)
        assertEquals(0, updatedMatchState.currentSet.secondPlayerScore)
        assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
    }

    @Test
    fun `game won by second player`() {
        val matchState = MatchState("p1", "p2").copy(
            currentGame = MatchState.Game(THIRTY, FORTY)
        )

        val updatedMatchState = updatedMatchStateFor(matchState, PlayerPoint.Player.SECOND)

        assertEquals(1, updatedMatchState.currentSet.secondPlayerScore)
        assertEquals(0, updatedMatchState.currentSet.firstPlayerScore)
        assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
    }

    @Test
    fun `first player still serving in ongoing first game`() {
        val matchState = MatchState("p1", "p2").copy(
            serving = Serving.FIRST_PLAYER
        )

        val updatedMatchState = updatedMatchStateFor(matchState, PlayerPoint.Player.SECOND)

        assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
    }

    private fun updatedMatchStateFor(matchState: MatchState, pointAuthor: PlayerPoint.Player): MatchState {
        val stubMatchRepository = StubMatchRepository(matchState)
        val usecase = PlayerPoint(stubMatchRepository)
        return usecase.run(pointAuthor)
    }
}
