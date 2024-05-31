package acceptance

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.LocatorAssertions
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.AriaRole.*
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.regex.Pattern
import kotlin.test.assertEquals

class ScoreboardPlaywrightTable private constructor(scoreboardTable: Locator) {

    val firstPlayer: ScoreboardPlaywrightPlayerRow
    val secondPlayer: ScoreboardPlaywrightPlayerRow

    init {
        val tableRows = scoreboardTable.getByRole(ROW)
        assertEquals(2, tableRows.count(), "Unexpected table rows count: ${tableRows.count()}")
        firstPlayer = ScoreboardPlaywrightPlayerRow(tableRows.nth(0))
        secondPlayer = ScoreboardPlaywrightPlayerRow(tableRows.nth(1))
    }

    fun firstPlayerPoint() = this.firstPlayer.playerName.click()
    fun secondPlayerPoint() = this.secondPlayer.playerName.click()

    companion object {
        fun from(page: Page): ScoreboardPlaywrightTable {
            val scoreboardTable = page.getByRole(TABLE)
            assertEquals(1, scoreboardTable.count(), "Unexpected table count: ${scoreboardTable.count()}")
            assertThat(scoreboardTable).isVisible()
            return ScoreboardPlaywrightTable(scoreboardTable.first())
        }
    }

}

class ScoreboardPlaywrightPlayerRow(playerRow: Locator) {

    private val tableRowCells: Locator

    val servingCell: Locator get() = tableRowCells.nth(0)
    val playerName: Locator get() = tableRowCells.nth(1)
    val wonSets: List<Locator> get() = emptyList()
    val currentSet: Locator get() = tableRowCells.nth(-2)
    val currentGame: Locator get() = tableRowCells.nth(-1)

    init {
        assertThat(playerRow).isVisible()
        tableRowCells = playerRow.getByRole(CELL)
        assertTrue(tableRowCells.count() >= 4, "Unexpected player row cells count: ${tableRowCells.count()}")
    }
}

fun LocatorAssertions.haveServingIndicator() {
    this.hasText(Pattern.compile("^·$"))
}

fun LocatorAssertions.hasScore(score: Int) {
    this.hasText(score.toString())
}

fun LocatorAssertions.hasAdvantageScore() {
    this.hasText("A")
}
