package acceptance

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import it.danielemegna.tennis.web.setupJettyApplicationEngine
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

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

        scoreboardTable.firstPlayer.shouldHaveColumnsCount(4)
        scoreboardTable.secondPlayer.shouldHaveColumnsCount(4)

        assertThat(scoreboardTable.firstPlayer.servingCell).haveServingIndicator()
        assertThat(scoreboardTable.secondPlayer.servingCell).not().haveServingIndicator()

        assertThat(scoreboardTable.firstPlayer.currentSet).hasScore(0)
        assertThat(scoreboardTable.secondPlayer.currentSet).hasScore(0)

        assertThat(scoreboardTable.firstPlayer.currentGame).hasScore(0)
        assertThat(scoreboardTable.secondPlayer.currentGame).hasScore(0)

        assertEquals(0, scoreboardTable.firstPlayer.wonSets.size)
        assertEquals(0, scoreboardTable.secondPlayer.wonSets.size)
    }

    @Test
    fun `score some points by click should update table`() {
        page.navigate(HOST_UNDER_TEST)
        val scoreboardTable = ScoreboardPlaywrightTable.from(page)

        scoreboardTable.firstPlayerPoint()
        assertThat(scoreboardTable.firstPlayer.currentGame).hasScore(15)
        assertThat(scoreboardTable.secondPlayer.currentGame).hasScore(0)
        assertThat(scoreboardTable.firstPlayer.servingCell).haveServingIndicator()

        scoreboardTable.firstPlayerPoint()
        scoreboardTable.secondPlayerPoint()
        assertThat(scoreboardTable.firstPlayer.currentGame).hasScore(30)
        assertThat(scoreboardTable.secondPlayer.currentGame).hasScore(15)
        assertThat(scoreboardTable.firstPlayer.servingCell).haveServingIndicator()

        scoreboardTable.firstPlayerPoint()
        scoreboardTable.firstPlayerPoint()
        assertThat(scoreboardTable.firstPlayer.currentSet).hasScore(1)
        assertThat(scoreboardTable.secondPlayer.currentSet).hasScore(0)
        assertThat(scoreboardTable.firstPlayer.currentGame).hasScore(0)
        assertThat(scoreboardTable.secondPlayer.currentGame).hasScore(0)
        assertThat(scoreboardTable.secondPlayer.servingCell).haveServingIndicator()
        assertThat(scoreboardTable.firstPlayer.servingCell).not().haveServingIndicator()

        assertEquals(0, scoreboardTable.firstPlayer.wonSets.size)
        assertEquals(0, scoreboardTable.secondPlayer.wonSets.size)
    }

    @Test
    fun `three set match`() {
        page.navigate(HOST_UNDER_TEST)
        val table = ScoreboardPlaywrightTable.from(page)

        // first set goes 5 - 4
        repeat(4) { table.firstPlayerPoint() }
        repeat(4) { table.secondPlayerPoint() }
        assertThat(table.firstPlayer.currentSet).hasScore(1)
        assertThat(table.secondPlayer.currentSet).hasScore(1)
        repeat(4 * 2) { table.firstPlayerPoint() }
        repeat(4 * 2) { table.secondPlayerPoint() }
        assertThat(table.firstPlayer.currentSet).hasScore(3)
        assertThat(table.secondPlayer.currentSet).hasScore(3)
        repeat(4 * 2) { table.firstPlayerPoint() }
        repeat(4) { table.secondPlayerPoint() }
        assertThat(table.firstPlayer.currentSet).hasScore(5)
        assertThat(table.secondPlayer.currentSet).hasScore(4)
        assertThat(table.secondPlayer.servingCell).haveServingIndicator()
        assertThat(table.firstPlayer.servingCell).not().haveServingIndicator()

        // first player miss set point advantage
        repeat(3) { table.firstPlayerPoint() }
        repeat(3) { table.secondPlayerPoint() }
        assertThat(table.firstPlayer.currentGame).hasScore(40)
        assertThat(table.secondPlayer.currentGame).hasScore(40)
        table.firstPlayerPoint()
        assertThat(table.firstPlayer.currentGame).hasAdvantageScore()
        assertThat(table.secondPlayer.currentGame).hasScore(40)
        table.secondPlayerPoint()
        assertThat(table.firstPlayer.currentGame).hasScore(40)
        assertThat(table.secondPlayer.currentGame).hasScore(40)

        // first player win first set
        repeat(2) { table.firstPlayerPoint() }
        table.firstPlayer.shouldHaveColumnsCount(5) // important to wait table update
        table.secondPlayer.shouldHaveColumnsCount(5) // important to wait table update
        assertEquals(1, table.firstPlayer.wonSets.size)
        assertEquals(1, table.secondPlayer.wonSets.size)
        assertThat(table.firstPlayer.wonSets.first()).hasScore(6)
        assertThat(table.secondPlayer.wonSets.first()).hasScore(4)
        assertThat(table.firstPlayer.currentSet).hasScore(0)
        assertThat(table.secondPlayer.currentSet).hasScore(0)
        assertThat(table.firstPlayer.currentGame).hasScore(0)
        assertThat(table.secondPlayer.currentGame).hasScore(0)

        // second player win second set
        repeat(4 * 5) { table.firstPlayerPoint() }
        repeat(4 * 5) { table.secondPlayerPoint() }
        assertThat(table.firstPlayer.currentSet).hasScore(5)
        assertThat(table.secondPlayer.currentSet).hasScore(5)
        repeat(4 * 2) { table.secondPlayerPoint() }
        table.firstPlayer.shouldHaveColumnsCount(6) // important to wait table update
        table.secondPlayer.shouldHaveColumnsCount(6) // important to wait table update
        assertThat(table.firstPlayer.wonSets[1]).hasScore(5)
        assertThat(table.secondPlayer.wonSets[1]).hasScore(7)
        assertThat(table.firstPlayer.wonSets[0]).hasScore(6)
        assertThat(table.secondPlayer.wonSets[0]).hasScore(4)

        // first player win third set with tiebreak
        repeat(4 * 5) { table.firstPlayerPoint() }
        repeat(4 * 5) { table.secondPlayerPoint() }
        repeat(4) { table.firstPlayerPoint() }
        repeat(4) { table.secondPlayerPoint() }
        assertThat(table.firstPlayer.currentSet).hasScore(6)
        assertThat(table.secondPlayer.currentSet).hasScore(6)
        repeat(6) { table.firstPlayerPoint() }
        repeat(3) { table.secondPlayerPoint() }
        table.firstPlayer.shouldHaveColumnsCount(6)
        assertThat(table.firstPlayer.currentGame).hasScore(6)
        assertThat(table.secondPlayer.currentGame).hasScore(3)
        assertThat(table.firstPlayer.currentSet).hasScore(6)
        assertThat(table.secondPlayer.currentSet).hasScore(6)
        table.firstPlayerPoint()
        table.firstPlayer.shouldHaveColumnsCount(7) // important to wait table update
        table.secondPlayer.shouldHaveColumnsCount(7) // important to wait table update
        assertThat(table.firstPlayer.wonSets[2]).hasScore(7)
        assertThat(table.secondPlayer.wonSets[2]).hasScore(6)
        assertThat(table.firstPlayer.wonSets[1]).hasScore(5)
        assertThat(table.secondPlayer.wonSets[1]).hasScore(7)
        assertThat(table.firstPlayer.wonSets[0]).hasScore(6)
        assertThat(table.secondPlayer.wonSets[0]).hasScore(4)
        assertThat(table.firstPlayer.currentGame).hasScore(0)
        assertThat(table.secondPlayer.currentGame).hasScore(0)
        assertThat(table.firstPlayer.currentSet).hasScore(0)
        assertThat(table.secondPlayer.currentSet).hasScore(0)
    }

    companion object {
        private const val HOST_UNDER_TEST = "http://localhost:8080"
    }
}
