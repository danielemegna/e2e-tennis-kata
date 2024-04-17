package it.danielemegna.tennis.domain.repository

import it.danielemegna.tennis.domain.MatchState

class InMemoryMatchRepository : MatchRepository {
    private var ongoingMatch: MatchState? = null;

    override fun storeNewMatch(matchState: MatchState) {
        ongoingMatch = matchState
    }

    override fun getOngoingMatch(): MatchState {
        if(ongoingMatch == null)
            throw RuntimeException("Cannot find ongoing match")
        return ongoingMatch!!
    }

    override fun updateOngoingMatch(matchState: MatchState) {
        ongoingMatch = matchState
    }
}