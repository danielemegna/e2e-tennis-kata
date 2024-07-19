package it.danielemegna.tennis.domain.usecase

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.MatchState.Game
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.FIFTEEN
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.THIRTY
import it.danielemegna.tennis.domain.MatchState.Serving
import it.danielemegna.tennis.domain.repository.MatchRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LoadOrInitMatchTest {

    private val mockMatchRepository = mockk<MatchRepository>()
    private val usecase = LoadOrInitMatch(mockMatchRepository)

    @Test
    fun `returns new match on null response from repository`() {
        every { mockMatchRepository.getOngoingMatch(any()) } returns null
        justRun { mockMatchRepository.storeNewMatch(any(), any()) }

        val actual = usecase.run("aMatchId")

        assertEquals(actual, initNewMatch())
    }

    @Test
    fun `returns match from repository when present`() {
        val ongoingMatch = anOngoingMatch()
        every { mockMatchRepository.getOngoingMatch("ongoing-match-id") } returns ongoingMatch

        val actual = usecase.run("ongoing-match-id")

        assertEquals(actual, ongoingMatch)
    }

    @Test
    fun `store new created match in repository`() {
        every { mockMatchRepository.getOngoingMatch(any()) } returns null
        justRun { mockMatchRepository.storeNewMatch(any(), any()) }

        usecase.run("new-match-id")

        verify(exactly = 1) { mockMatchRepository.storeNewMatch("new-match-id", initNewMatch()) }
    }

    @Test
    fun `do not store loaded ongoing match in repository`() {
        every { mockMatchRepository.getOngoingMatch("ongoing-match-id") } returns anOngoingMatch()

        usecase.run("ongoing-match-id")

        verify(exactly = 0) { mockMatchRepository.storeNewMatch(any(), any()) }
    }

    private fun initNewMatch(): MatchState {
        return MatchState("Sinner", "Djokovic")
    }

    private fun anOngoingMatch(): MatchState {
        return MatchState("Alice", "Bob").copy(
            serving = Serving.SECOND_PLAYER, currentGame = Game(THIRTY, FIFTEEN)
        )
    }

}
