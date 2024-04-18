package it.danielemegna.tennis.domain.usecase

import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.MatchState.Game.GameScore
import it.danielemegna.tennis.domain.repository.StubMatchRepository
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PlayerPointTest {

    @Test
    fun `first player point on new game`() {
        val newMatchState = MatchState("p1", "p2")

        val updatedMatchState = updatedMatchStateFor(newMatchState, PlayerPoint.Player.FIRST)

        assertEquals(GameScore.FIFTEEN, updatedMatchState.currentGame.firstPlayerScore)
        assertEquals(GameScore.ZERO, updatedMatchState.currentGame.secondPlayerScore)
    }

    @Test
    fun `second player point on new game`() {
        val newMatchState = MatchState("p1", "p2")

        val updatedMatchState = updatedMatchStateFor(newMatchState, PlayerPoint.Player.SECOND)

        assertEquals(GameScore.FIFTEEN, updatedMatchState.currentGame.secondPlayerScore)
        assertEquals(GameScore.ZERO, updatedMatchState.currentGame.firstPlayerScore)
    }

    private fun updatedMatchStateFor(matchState: MatchState, pointAuthor: PlayerPoint.Player): MatchState {
        val stubMatchRepository = StubMatchRepository(matchState)
        val usecase = PlayerPoint(stubMatchRepository)
        return usecase.run(pointAuthor)
    }
}
