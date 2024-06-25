package it.danielemegna.tennis.domain.usecase

import it.danielemegna.tennis.domain.repository.MatchRepository
import it.danielemegna.tennis.domain.MatchState

class LoadOrInitGame(private val matchRepository: MatchRepository) {

    fun run(matchId: String): MatchState {
        val ongoingMatch = matchRepository.getOngoingMatch(matchId)
        if(ongoingMatch != null) return ongoingMatch

        val newMatchState = MatchState(
            firstPlayerName = "Sinner",
            secondPlayerName = "Djokovic",
        )
        matchRepository.storeNewMatch(matchId, newMatchState)
        return newMatchState
    }

}