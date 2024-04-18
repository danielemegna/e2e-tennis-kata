package it.danielemegna.tennis.domain.repository

import it.danielemegna.tennis.domain.MatchState

class StubMatchRepository(private val ongoingMatch: MatchState) : MatchRepository {

    override fun storeNewMatch(matchState: MatchState) {
    }

    override fun getOngoingMatch(): MatchState {
        return this.ongoingMatch
    }

    override fun updateOngoingMatch(matchState: MatchState) {
    }

}
