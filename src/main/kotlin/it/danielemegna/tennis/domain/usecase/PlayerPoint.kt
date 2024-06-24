package it.danielemegna.tennis.domain.usecase

import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.MatchStateUpdater
import it.danielemegna.tennis.domain.repository.MatchRepository

class PlayerPoint(
    private val matchRepository: MatchRepository,
    private val matchStateUpdater: MatchStateUpdater = MatchStateUpdater()
) {

    fun run(pointAuthor: Player, matchId: String): MatchState {
        val currentMatchState = matchRepository.getOngoingMatch()
        val newMatchState = matchStateUpdater.updatedMatch(currentMatchState, pointAuthor)
        matchRepository.updateOngoingMatch(newMatchState);
        return newMatchState
    }

    enum class Player { FIRST, SECOND }
}
