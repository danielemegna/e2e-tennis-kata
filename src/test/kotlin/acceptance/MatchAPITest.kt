package acceptance

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Connection.Method
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test

class MatchAPITest {

    @Test
    fun `init new match on root path`(): Unit = runBlocking {
        val response = Jsoup.connect("http://localhost:8080").method(Method.GET).execute()

        assertThat(response.statusCode()).isEqualTo(200)
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

        val response = Jsoup.connect("http://localhost:8080/player/1/point").method(Method.POST).execute()

        assertThat(response.statusCode()).isEqualTo(200)
        val htmlPage = response.parse()
        val playersScoreboardRows = htmlPage.select("#scoreboard tr")
        assertThat(playersScoreboardRows[0].select("td.current-game").text()).isEqualTo("15")
        assertThat(playersScoreboardRows[0].select("td.current-set").text()).isEqualTo("0")
        assertThat(playersScoreboardRows[1].select("td.current-game").text()).isEqualTo("0")
        assertThat(playersScoreboardRows[1].select("td.current-set").text()).isEqualTo("0")
    }

    private fun initNewMatch() {
        Jsoup.connect("http://localhost:8080").method(Method.GET).execute()
    }
}