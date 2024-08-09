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
import it.danielemegna.tennis.domain.MatchStateScanner
import it.danielemegna.tennis.domain.MatchStateUpdater
import it.danielemegna.tennis.domain.repository.InMemoryMatchRepository
import it.danielemegna.tennis.domain.usecase.LoadOrInitMatch
import it.danielemegna.tennis.domain.usecase.LoadOrInitMatch.Result.MatchOrigin
import it.danielemegna.tennis.domain.usecase.PlayerPoint
import it.danielemegna.tennis.web.view.ScoreBoardView
import java.util.*

fun main() {
    setupJettyApplicationEngine(8080).start(wait = true)
}

fun setupJettyApplicationEngine(port: Int): JettyApplicationEngine {
    return embeddedServer(Jetty, port = port) {
        freeMarkerPlugin()
        exceptionHandlingPlugin()
        val matchRepository = InMemoryMatchRepository()

        routing {
            get("/") {
                val newRandomMatchId = UUID.randomUUID()
                call.respondRedirect("/$newRandomMatchId", permanent = false)
            }
            get("/{matchId}") {
                val matchId = call.parameters["matchId"]!!
                val usecase = LoadOrInitMatch(matchRepository)

                val usecaseResult = usecase.run(matchId)

                val scoreBoardView = ScoreBoardView.from(matchId, usecaseResult.matchState)
                val httpStatusCode = when (usecaseResult.matchOrigin) {
                    MatchOrigin.NEW_MATCH -> HttpStatusCode.Created
                    MatchOrigin.ONGOING_MATCH -> HttpStatusCode.OK
                }
                call.respond(message = FreeMarkerContent("index.ftl", scoreBoardView), status = httpStatusCode)
            }
            post("/{matchId}/player/{playerNumber}/point") {
                val matchId = call.parameters["matchId"]!!
                val pointAuthor = playerNumberParameterFrom(call)
                if (pointAuthor == null) {
                    call.response.status(HttpStatusCode.BadRequest)
                    return@post
                }

                val usecase = PlayerPoint(
                    matchRepository = matchRepository,
                    matchStateUpdater = MatchStateUpdater(
                        matchStateScanner = MatchStateScanner()
                    )
                )
                val usecaseResult = usecase.run(pointAuthor, matchId)

                val scoreBoardView = ScoreBoardView.from(matchId, usecaseResult.newMatchState)
                call.respond(message = FreeMarkerContent("scoreboard.ftl", scoreBoardView), status = HttpStatusCode.OK)
            }
            staticResources("/assets", "assets")
        }
    }
}

private fun playerNumberParameterFrom(call: ApplicationCall): PlayerPoint.Player? {
    val playerNumberParam = call.parameters["playerNumber"]
    return when (playerNumberParam) {
        "1" -> PlayerPoint.Player.FIRST
        "2" -> PlayerPoint.Player.SECOND
        else -> null
    }
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

private fun Application.freeMarkerPlugin() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
}
