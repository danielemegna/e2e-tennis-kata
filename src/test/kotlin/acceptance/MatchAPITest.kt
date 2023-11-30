package acceptance

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test

class MatchAPITest {

    @Test
    fun `init new match on root path`(): Unit = runBlocking {
        val doc = Jsoup.connect("http://localhost:8080").get()

        val playersScoreboardRows = doc.select("#scoreboard tr")
        assertThat(playersScoreboardRows[0].select("td.current-game").text()).isEqualTo("0")
        assertThat(playersScoreboardRows[0].select("td.current-set").text()).isEqualTo("0")
        assertThat(playersScoreboardRows[1].select("td.current-game").text()).isEqualTo("0")
        assertThat(playersScoreboardRows[1].select("td.current-set").text()).isEqualTo("0")
    }
}