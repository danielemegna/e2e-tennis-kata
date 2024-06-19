package acceptance

import it.danielemegna.tennis.web.setupJettyApplicationEngine
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Connection
import org.jsoup.Connection.Method
import org.jsoup.Jsoup
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MatchAPITest {

    private val jettyApplicationEngine = setupJettyApplicationEngine(port = 8080)

    @BeforeAll
    fun beforeAll() {
        jettyApplicationEngine.start()
    }

    @AfterAll
    fun afterAll() {
        jettyApplicationEngine.stop()
    }

    @Test
    fun `init new match on root path`(): Unit = runBlocking {
        val response = getRequest("/").execute()

        assertThat(response.statusCode()).isEqualTo(201)
        val htmlPage = response.parse()
        val playersScoreboardRows = htmlPage.select("#scoreboard tr")
        assertThat(playersScoreboardRows[0].select("td.current-game").text()).isEqualTo("0")
        assertThat(playersScoreboardRows[0].select("td.current-set").text()).isEqualTo("0")
        assertThat(playersScoreboardRows[1].select("td.current-game").text()).isEqualTo("0")
        assertThat(playersScoreboardRows[1].select("td.current-set").text()).isEqualTo("0")
    }

    @Test
    fun `register first player point`(): Unit = runBlocking {
        initNewMatch()

        val response = postRequest("/player/1/point").execute()

        assertThat(response.statusCode()).isEqualTo(200)
        val htmlPage = response.parse()
        val playersScoreboardRows = htmlPage.select("#scoreboard tr")
        assertThat(playersScoreboardRows[0].select("td.current-game").text()).isEqualTo("15")
        assertThat(playersScoreboardRows[0].select("td.current-set").text()).isEqualTo("0")
        assertThat(playersScoreboardRows[1].select("td.current-game").text()).isEqualTo("0")
        assertThat(playersScoreboardRows[1].select("td.current-set").text()).isEqualTo("0")
    }

    @Test
    fun `register some points in first game`(): Unit = runBlocking {
        initNewMatch()

        postRequest("/player/1/point").execute()
        postRequest("/player/1/point").execute()
        postRequest("/player/2/point").execute()
        postRequest("/player/1/point").execute().let { response ->
            assertThat(response.statusCode()).isEqualTo(200)
            val htmlPage = response.parse()
            val playersScoreboardRows = htmlPage.select("#scoreboard tr")
            assertThat(playersScoreboardRows[0].select("td.current-game").text()).isEqualTo("40")
            assertThat(playersScoreboardRows[0].select("td.current-set").text()).isEqualTo("0")
            assertThat(playersScoreboardRows[1].select("td.current-game").text()).isEqualTo("15")
            assertThat(playersScoreboardRows[1].select("td.current-set").text()).isEqualTo("0")
        }
    }

    @Test
    fun `not found response on unexisting route`(): Unit = runBlocking {
        getRequest("/unexisting").ignoreHttpErrors(true).execute().let { response ->
            assertThat(response.statusCode()).isEqualTo(404)
            assertThat(response.bodyAsBytes()).isEmpty()
        }
        postRequest("/unexisting").ignoreHttpErrors(true).execute().let { response ->
            assertThat(response.statusCode()).isEqualTo(404)
            assertThat(response.bodyAsBytes()).isEmpty()
        }
    }

    private fun initNewMatch() {
        getRequest("/").execute()
    }

    private fun postRequest(apiPath: String): Connection {
        return Jsoup.connect("${HOST_UNDER_TEST}$apiPath").method(Method.POST)
    }

    private fun getRequest(apiPath: String): Connection {
        return Jsoup.connect("${HOST_UNDER_TEST}$apiPath").method(Method.GET)
    }

    companion object {
        private const val HOST_UNDER_TEST = "http://localhost:8080"
    }
}