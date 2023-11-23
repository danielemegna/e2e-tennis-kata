package it.danielemegna.tennis.web

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.jetty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import it.danielemegna.tennis.web.view.ScoreBoardView

fun main() {
    embeddedServer(Jetty, port = 8080) {
        freeMarkerPlugin()

        routing {
            get("/") {
                val scoreBoardView = ScoreBoardView(
                    firstPlayerName = "Sinner",
                    secondPlayerName = "Djokovic",
                    isFirstPlayerServing = true,
                    firstPlayerCurrentGameScore = 40,
                    secondPlayerCurrentGameScore = 15,
                    firstPlayerCurrentSetScore = 3,
                    secondPlayerCurrentSetScore = 2
                )
                call.respond(FreeMarkerContent("index.ftl", scoreBoardView))
            }
            staticResources("/assets", "assets")
        }
    }.start(wait = true)
}

private fun Application.freeMarkerPlugin() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
}
