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
import it.danielemegna.tennis.domain.repository.InMemoryMatchRepository
import it.danielemegna.tennis.domain.usecase.InitNewGame
import it.danielemegna.tennis.domain.usecase.PlayerPoint
import it.danielemegna.tennis.web.view.ScoreBoardView

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
                val usecase = InitNewGame(matchRepository)
                val matchState = usecase.run()
                val scoreBoardView = ScoreBoardView.from(matchState)
                call.respond(message = FreeMarkerContent("index.ftl", scoreBoardView), status = HttpStatusCode.Created)
            }
            post("/player/{playerNumber}/point") {
                val pointAuthor = playerNumberParameterFrom(call)
                if(pointAuthor == null) {
                    call.response.status(HttpStatusCode.BadRequest)
                    return@post
                }

                val usecase = PlayerPoint(matchRepository)
                val matchState = usecase.run(pointAuthor)
                val scoreBoardView = ScoreBoardView.from(matchState)
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
