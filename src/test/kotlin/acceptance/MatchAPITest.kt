package acceptance

import it.danielemegna.tennis.web.setupJettyApplicationEngine
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Connection
import org.jsoup.Connection.Method
import org.jsoup.Jsoup
import org.junit.jupiter.api.*
import java.util.*
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
        assertThat(redirectUrl).matches("^/$UUIDV4_REGEX$")

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
        val matchId = initNewMatch()
        val response = postRequest("/$matchId/player/1/point").execute()

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
        val matchId = initNewMatch()
        postRequest("/$matchId/player/1/point").execute()
        postRequest("/$matchId/player/1/point").execute()
        postRequest("/$matchId/player/2/point").execute()
        postRequest("/$matchId/player/1/point").execute().let { response ->
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
    fun `getting already started match should return 200 ok instead of 201 created`(): Unit = runBlocking {
        val matchId = initNewMatch()
        postRequest("/$matchId/player/1/point").execute()

        val response = getRequest("/$matchId").execute()

        assertThat(response.statusCode()).isEqualTo(200)
        val htmlPage = response.parse()
        val playersScoreboardRows = htmlPage.select("#scoreboard tr")
        assertThat(playersScoreboardRows[0].select("td.current-game").text()).isEqualTo("15")
        assertThat(playersScoreboardRows[0].select("td.current-set").text()).isEqualTo("0")
        assertThat(playersScoreboardRows[1].select("td.current-game").text()).isEqualTo("0")
        assertThat(playersScoreboardRows[1].select("td.current-set").text()).isEqualTo("0")
    }

    @Test
    fun `play multiple matches with different ids`(): Unit = runBlocking {
        val firstMatchId = initNewMatch()
        val secondMatchId = initNewMatch()
        postRequest("/$firstMatchId/player/1/point").execute()
        postRequest("/$firstMatchId/player/2/point").execute()
        postRequest("/$secondMatchId/player/1/point").execute()
        postRequest("/$secondMatchId/player/1/point").execute()
        postRequest("/$firstMatchId/player/1/point").execute()
        postRequest("/$secondMatchId/player/1/point").execute()

        getRequest("/$firstMatchId").execute().let { response ->
            assertThat(response.statusCode()).isEqualTo(200)
            val htmlPage = response.parse()
            val playersScoreboardRows = htmlPage.select("#scoreboard tr")
            assertThat(playersScoreboardRows[0].select("td.current-game").text()).isEqualTo("30")
            assertThat(playersScoreboardRows[0].select("td.current-set").text()).isEqualTo("0")
            assertThat(playersScoreboardRows[1].select("td.current-game").text()).isEqualTo("15")
            assertThat(playersScoreboardRows[1].select("td.current-set").text()).isEqualTo("0")
        }
        getRequest("/$secondMatchId").execute().let { response ->
            assertThat(response.statusCode()).isEqualTo(200)
            val htmlPage = response.parse()
            val playersScoreboardRows = htmlPage.select("#scoreboard tr")
            assertThat(playersScoreboardRows[0].select("td.current-game").text()).isEqualTo("40")
            assertThat(playersScoreboardRows[0].select("td.current-set").text()).isEqualTo("0")
            assertThat(playersScoreboardRows[1].select("td.current-game").text()).isEqualTo("0")
            assertThat(playersScoreboardRows[1].select("td.current-set").text()).isEqualTo("0")
        }
    }

    @Test
    fun `bad request response on wrong player number point request`(): Unit = runBlocking {
        val matchId = initNewMatch()
        postRequest("/$matchId/player/wrong/point").ignoreHttpErrors(true).execute().let { response ->
            assertThat(response.statusCode()).isEqualTo(400)
            assertThat(response.bodyAsBytes()).isEmpty()
        }
        postRequest("/$matchId/player/3/point").ignoreHttpErrors(true).execute().let { response ->
            assertThat(response.statusCode()).isEqualTo(400)
            assertThat(response.bodyAsBytes()).isEmpty()
        }
        postRequest("/$matchId/player/0/point").ignoreHttpErrors(true).execute().let { response ->
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

    private fun initNewMatch(): String {
        val matchId = UUID.randomUUID()
        val response = getRequest("/$matchId").execute()
        assertThat(response.statusCode()).isEqualTo(201)
        return matchId.toString()
    }

    private fun postRequest(apiPath: String): Connection {
        return Jsoup.connect("${HOST_UNDER_TEST}$apiPath").followRedirects(false).method(Method.POST)
    }

    private fun getRequest(apiPath: String): Connection {
        return Jsoup.connect("${HOST_UNDER_TEST}$apiPath").followRedirects(false).method(Method.GET)
    }

    companion object {
        private const val UUIDV4_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"
        private const val HOST_UNDER_TEST = "http://localhost:8080"
    }
}