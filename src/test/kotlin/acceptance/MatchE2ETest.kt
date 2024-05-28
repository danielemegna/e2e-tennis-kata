package acceptance

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.AriaRole.TABLE
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MatchE2ETest {

    // a running instance available on localhost:8080
    // is needed to run this tests !

    private val playwright = Playwright.create()
    private val browser: Browser = playwright.chromium().launch()
    private lateinit var context: BrowserContext
    private lateinit var page: Page

    @BeforeEach
    fun setUp() {
        context = browser.newContext()
        page = context.newPage()
    }

    @Test
    fun `render the scoreboard with player names`() {
        page.navigate(HOST_UNDER_TEST)

        val firstPlayerName = page.getByText("SINNER")
        assertThat(firstPlayerName).isVisible();
        val secondPlayerName = page.getByText("DJOKOVIC")
        assertThat(secondPlayerName).isVisible();
    }

    @Test
    fun `render the new match scoreboard`() {
        page.navigate(HOST_UNDER_TEST)

        val scoreboardTable = ScoreboardPlaywrightTable(page.getByRole(TABLE))

        assertThat(scoreboardTable.firstPlayer.servingCell).haveServingIndicator()
        assertThat(scoreboardTable.secondPlayer.servingCell).not().haveServingIndicator()

        assertThat(scoreboardTable.firstPlayer.currentSet).hasScore(0)
        assertThat(scoreboardTable.secondPlayer.currentSet).hasScore(0)

        assertThat(scoreboardTable.firstPlayer.currentGame).hasScore(0)
        assertThat(scoreboardTable.secondPlayer.currentGame).hasScore(0)
    }

    companion object {
        private const val HOST_UNDER_TEST = "http://localhost:8080"
    }
}
