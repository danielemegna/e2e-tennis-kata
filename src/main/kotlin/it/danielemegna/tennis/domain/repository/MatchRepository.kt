package it.danielemegna.tennis.domain.repository

import it.danielemegna.tennis.domain.MatchState

interface MatchRepository {
    fun storeNewMatch(matchState: MatchState)
}
