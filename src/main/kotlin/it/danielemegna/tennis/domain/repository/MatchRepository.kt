package it.danielemegna.tennis.domain.repository

import it.danielemegna.tennis.domain.MatchState

interface MatchRepository {
    fun storeNewMatch(matchId: String, matchState: MatchState)
    fun getOngoingMatch(matchId: String): MatchState?
    fun updateOngoingMatch(matchId: String, matchState: MatchState)
}
