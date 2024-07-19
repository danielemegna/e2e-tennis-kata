package it.danielemegna.tennis.domain.usecase

import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.repository.MatchRepository
import it.danielemegna.tennis.domain.usecase.LoadOrInitMatch.Result.MatchOrigin.NEW_MATCH
import it.danielemegna.tennis.domain.usecase.LoadOrInitMatch.Result.MatchOrigin.ONGOING_MATCH

class LoadOrInitMatch(private val matchRepository: MatchRepository) {

    fun run(matchId: String): Result {
        val ongoingMatch = matchRepository.getOngoingMatch(matchId)
        if (ongoingMatch != null)
            return Result(matchState = ongoingMatch, matchOrigin = ONGOING_MATCH)

        val newMatchState = MatchState(
            firstPlayerName = "Sinner",
            secondPlayerName = "Djokovic",
        )
        matchRepository.storeNewMatch(matchId, newMatchState)
        return Result(matchState = newMatchState, matchOrigin = NEW_MATCH)
    }


    data class Result(val matchState: MatchState, val matchOrigin: MatchOrigin) {
        enum class MatchOrigin { NEW_MATCH, ONGOING_MATCH }
    }
}
