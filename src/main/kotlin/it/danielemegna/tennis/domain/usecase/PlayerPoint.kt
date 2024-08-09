package it.danielemegna.tennis.domain.usecase

import it.danielemegna.tennis.domain.MatchRelevantMoment
import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.MatchStateUpdater
import it.danielemegna.tennis.domain.repository.MatchRepository

class PlayerPoint(
    private val matchRepository: MatchRepository,
    private val matchStateUpdater: MatchStateUpdater
) {

    fun run(pointAuthor: Player, matchId: String): Result {
        val currentMatchState = matchRepository.getOngoingMatch(matchId)
            ?: throw RuntimeException("Cannot find ongoing match with id $matchId")

        val newMatchState = matchStateUpdater.updatedMatch(currentMatchState, pointAuthor)
        matchRepository.updateOngoingMatch(matchId, newMatchState);

        val matchRelevantMoment = MatchRelevantMoment.from(newMatchState)
        return Result(newMatchState, matchRelevantMoment)
    }

    enum class Player { FIRST, SECOND }

    data class Result(val newMatchState: MatchState, val matchRelevantMoment: MatchRelevantMoment?)
}
