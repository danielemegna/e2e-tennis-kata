package acceptance

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.AriaRole.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.regex.Pattern
import kotlin.test.assertEquals

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

        // main single table visible
        val scoreboardTable = page.getByRole(TABLE)
        assertEquals(1, scoreboardTable.count())
        assertThat(scoreboardTable).isVisible()

        // scoreboard table has two rows
        val tableRows = scoreboardTable.getByRole(ROW)
        assertEquals(2, tableRows.count())

        // scoreboard rows cells are four on match start
        val firstPlayerRowCells = tableRows.nth(0).getByRole(CELL)
        assertEquals(4, firstPlayerRowCells.count())
        val secondPlayerRowCells = tableRows.nth(1).getByRole(CELL)
        assertEquals(4, firstPlayerRowCells.count())

        // check serving indicator: first player starts
        assertThat(firstPlayerRowCells.nth(0)).containsText(Pattern.compile("^·$"))
        assertThat(secondPlayerRowCells.nth(0)).containsText(Pattern.compile("^$"))

        // check set scoring to zero
        assertThat(firstPlayerRowCells.nth(2)).containsText("0")
        assertThat(secondPlayerRowCells.nth(2)).containsText("0")

        // check game scoring to zero
        assertThat(firstPlayerRowCells.nth(3)).containsText("0")
        assertThat(secondPlayerRowCells.nth(3)).containsText("0")
    }

    companion object {
        private const val HOST_UNDER_TEST = "http://localhost:8080"
    }
}