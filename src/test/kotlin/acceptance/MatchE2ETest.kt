package acceptance

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import it.danielemegna.tennis.web.setupJettyApplicationEngine
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MatchE2ETest {

    private val jettyApplicationEngine = setupJettyApplicationEngine(port = 8080)

    private val playwright = Playwright.create()
    private val browser: Browser = playwright.chromium().launch()
    private lateinit var context: BrowserContext
    private lateinit var page: Page

    @BeforeAll
    fun beforeAll() {
        jettyApplicationEngine.start()
    }

    @AfterAll
    fun afterAll() {
        jettyApplicationEngine.stop()
    }

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

        val scoreboardTable = ScoreboardPlaywrightTable.from(page)

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
