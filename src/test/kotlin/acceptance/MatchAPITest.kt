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
import kotlin.test.assertNotNull

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
    fun `root path should redirect on a new match with random uuid`(): Unit = runBlocking {
        val response = getRequest("/").execute()
        assertThat(response.statusCode()).isEqualTo(302)
        val redirectUrl = response.header("location")
        assertNotNull(redirectUrl)
        assertThat(redirectUrl).matches("""^/[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$""")

        val redirectTargetResponse = getRequest(redirectUrl).execute()
        val htmlPage = redirectTargetResponse.parse()
        val scoreBoardTable = htmlPage.select("table#scoreboard")
        assertThat(scoreBoardTable.size).isGreaterThan(0)
    }

    @Test
    fun `create new match with custom id`(): Unit = runBlocking {
        val response = getRequest("/custom-id").execute()

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
        initNewMatchWith(id = "match-id")

        val response = postRequest("/match-id/player/1/point").execute()

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
        initNewMatchWith(id = "match-id")

        postRequest("/match-id/player/1/point").execute()
        postRequest("/match-id/player/1/point").execute()
        postRequest("/match-id/player/2/point").execute()
        postRequest("/match-id/player/1/point").execute().let { response ->
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
    fun `bad request response on wrong player number point request`(): Unit = runBlocking {
        initNewMatchWith(id = "match-id")

        postRequest("/match-id/player/wrong/point").ignoreHttpErrors(true).execute().let { response ->
            assertThat(response.statusCode()).isEqualTo(400)
            assertThat(response.bodyAsBytes()).isEmpty()
        }
        postRequest("/match-id/player/3/point").ignoreHttpErrors(true).execute().let { response ->
            assertThat(response.statusCode()).isEqualTo(400)
            assertThat(response.bodyAsBytes()).isEmpty()
        }
        postRequest("/match-id/player/0/point").ignoreHttpErrors(true).execute().let { response ->
            assertThat(response.statusCode()).isEqualTo(400)
            assertThat(response.bodyAsBytes()).isEmpty()
        }
    }

    @Test
    fun `not found response on unexisting route`(): Unit = runBlocking {
        getRequest("/unexisting/route").ignoreHttpErrors(true).execute().let { response ->
            assertThat(response.statusCode()).isEqualTo(404)
            assertThat(response.bodyAsBytes()).isEmpty()
        }
        postRequest("/unexisting/route").ignoreHttpErrors(true).execute().let { response ->
            assertThat(response.statusCode()).isEqualTo(404)
            assertThat(response.bodyAsBytes()).isEmpty()
        }
    }

    private fun initNewMatchWith(id: String) {
        val response = getRequest("/$id").execute()
        assertThat(response.statusCode()).isEqualTo(201)
    }

    private fun postRequest(apiPath: String): Connection {
        return Jsoup.connect("${HOST_UNDER_TEST}$apiPath").followRedirects(false).method(Method.POST)
    }

    private fun getRequest(apiPath: String): Connection {
        return Jsoup.connect("${HOST_UNDER_TEST}$apiPath").followRedirects(false).method(Method.GET)
    }

    companion object {
        private const val HOST_UNDER_TEST = "http://localhost:8080"
    }
}