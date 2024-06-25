package it.danielemegna.tennis.domain.repository

import it.danielemegna.tennis.domain.MatchState

class InMemoryMatchRepository : MatchRepository {
    private val ongoingMatches: MutableMap<String, MatchState> = mutableMapOf()

    override fun storeNewMatch(matchId: String, matchState: MatchState) {
        ongoingMatches[matchId] = matchState;
    }

    override fun getOngoingMatch(matchId: String): MatchState? {
        return ongoingMatches[matchId]
    }

    override fun updateOngoingMatch(matchId: String, matchState: MatchState) {
        ongoingMatches[matchId] = matchState;
    }
}