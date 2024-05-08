package it.danielemegna.tennis.web

import freemarker.cache.ClassTemplateLoader
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.jetty.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.MatchState.Game.GameScore
import it.danielemegna.tennis.domain.MatchState.Serving
import it.danielemegna.tennis.domain.repository.InMemoryMatchRepository
import it.danielemegna.tennis.domain.usecase.InitNewGame
import it.danielemegna.tennis.domain.usecase.PlayerPoint
import it.danielemegna.tennis.web.view.ScoreBoardView

fun main() {
    embeddedServer(Jetty, port = 8080) {
        freeMarkerPlugin()
        exceptionHandlingPlugin()
        val matchRepository = InMemoryMatchRepository()

        routing {
            get("/") {
                val usecase = InitNewGame(matchRepository)
                val matchState = usecase.run()
                val scoreBoardView = scoreBoardViewFrom(matchState)
                call.respond(message = FreeMarkerContent("index.ftl", scoreBoardView), status = HttpStatusCode.Created)
            }
            post("/player/1/point") {
                val usecase = PlayerPoint(matchRepository)
                val matchState = usecase.run(PlayerPoint.Player.FIRST)
                val scoreBoardView = scoreBoardViewFrom(matchState)
                call.respond(message = FreeMarkerContent("scoreboard.ftl", scoreBoardView), status = HttpStatusCode.OK)
            }
            post("/player/2/point") {
                val usecase = PlayerPoint(matchRepository)
                val matchState = usecase.run(PlayerPoint.Player.SECOND)
                val scoreBoardView = scoreBoardViewFrom(matchState)
                call.respond(message = FreeMarkerContent("scoreboard.ftl", scoreBoardView), status = HttpStatusCode.OK)
            }
            staticResources("/assets", "assets")
        }
    }.start(wait = true)
}

private fun Application.exceptionHandlingPlugin() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            print("Exception occurred! -> ")
            cause.printStackTrace()

            call.respondText(
                text = "<pre style='font-size:20px; padding:20px'>500: $cause</pre>",
                status = HttpStatusCode.InternalServerError,
                contentType = ContentType.Text.Html
            )
        }
    }
}

private fun scoreBoardViewFrom(matchState: MatchState) = ScoreBoardView(
    isFirstPlayerServing = matchState.serving == Serving.FIRST_PLAYER,
    firstPlayerName = matchState.firstPlayerName,
    secondPlayerName = matchState.secondPlayerName,
    finishedSets = emptyList(),
    firstPlayerCurrentSetScore = matchState.currentSet.firstPlayerScore,
    secondPlayerCurrentSetScore = matchState.currentSet.secondPlayerScore,
    firstPlayerCurrentGameScore = matchState.currentGame.firstPlayerScore.toString(),
    secondPlayerCurrentGameScore = matchState.currentGame.secondPlayerScore.toString(),
)

private fun Application.freeMarkerPlugin() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
}
