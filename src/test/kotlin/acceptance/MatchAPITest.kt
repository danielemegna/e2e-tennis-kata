package acceptance

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Connection.Method
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test

class MatchAPITest {

    // a running instance available on localhost:8080
    // is needed to run this tests !

    @Test
    fun `init new match on root path`(): Unit = runBlocking {
        val response = Jsoup.connect("${HOST_UNDER_TEST}/").method(Method.GET).execute()

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

        val response = Jsoup.connect("${HOST_UNDER_TEST}/player/1/point").method(Method.POST).execute()

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

        Jsoup.connect("${HOST_UNDER_TEST}/player/1/point").method(Method.POST).execute()
        Jsoup.connect("${HOST_UNDER_TEST}/player/1/point").method(Method.POST).execute()
        Jsoup.connect("${HOST_UNDER_TEST}/player/2/point").method(Method.POST).execute()
        Jsoup.connect("${HOST_UNDER_TEST}/player/1/point").method(Method.POST).execute().let { response ->
            assertThat(response.statusCode()).isEqualTo(200)
            val htmlPage = response.parse()
            val playersScoreboardRows = htmlPage.select("#scoreboard tr")
            assertThat(playersScoreboardRows[0].select("td.current-game").text()).isEqualTo("40")
            assertThat(playersScoreboardRows[0].select("td.current-set").text()).isEqualTo("0")
            assertThat(playersScoreboardRows[1].select("td.current-game").text()).isEqualTo("15")
            assertThat(playersScoreboardRows[1].select("td.current-set").text()).isEqualTo("0")
        }
    }

    private fun initNewMatch() {
        Jsoup.connect("${HOST_UNDER_TEST}/").method(Method.GET).execute()
    }

    companion object {
        private const val HOST_UNDER_TEST = "http://localhost:8080"
    }
}