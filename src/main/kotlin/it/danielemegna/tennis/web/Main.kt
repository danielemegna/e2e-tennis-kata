package it.danielemegna.tennis.web

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.jetty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.MatchState.Game.GameScore
import it.danielemegna.tennis.domain.MatchState.Serving
import it.danielemegna.tennis.domain.repository.InMemoryMatchRepository
import it.danielemegna.tennis.domain.usecase.InitNewGame
import it.danielemegna.tennis.web.view.ScoreBoardView

fun main() {
    embeddedServer(Jetty, port = 8080) {
        freeMarkerPlugin()
        val matchRepository = InMemoryMatchRepository()

        routing {
            get("/") {
                val usecase = InitNewGame(matchRepository)
                val matchState = usecase.run()
                val scoreBoardView = scoreBoardViewFrom(matchState)
                call.respond(FreeMarkerContent("index.ftl", scoreBoardView))
            }
            post("/player/1/point") {
                val scoreBoardView = ScoreBoardView(
                    firstPlayerName = "Sinner",
                    secondPlayerName = "Djokovic",
                    isFirstPlayerServing = true,
                    firstPlayerCurrentGameScore = 15,
                    secondPlayerCurrentGameScore = 0,
                    firstPlayerCurrentSetScore = 0,
                    secondPlayerCurrentSetScore = 0
                )
                call.respond(FreeMarkerContent("scoreboard.ftl", scoreBoardView))
            }
            staticResources("/assets", "assets")
        }
    }.start(wait = true)
}

private fun scoreBoardViewFrom(matchState: MatchState) = ScoreBoardView(
    firstPlayerName = matchState.firstPlayerName,
    secondPlayerName = matchState.secondPlayerName,
    isFirstPlayerServing = matchState.serving == Serving.FIRST_PLAYER,
    firstPlayerCurrentGameScore = toInt(matchState.currentGame.firstPlayerScore),
    secondPlayerCurrentGameScore = toInt(matchState.currentGame.secondPlayerScore),
    firstPlayerCurrentSetScore = 0,
    secondPlayerCurrentSetScore = 0
)

private fun toInt(gameScore: GameScore) = when (gameScore) {
    GameScore.ZERO -> 0
    GameScore.FIFTEEN -> 15
    GameScore.THIRTY -> 30
    GameScore.FORTY -> 40
    GameScore.ADVANTAGE -> TODO("integer could not be the right choice :-)")
}

private fun Application.freeMarkerPlugin() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
}
