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

    val servingCell: Locator
    val playerName: Locator
    val wonSets: List<Locator>
    val currentSet: Locator
    val currentGame: Locator

    init {
        assertThat(playerRow).isVisible()
        val rowCells = playerRow.getByRole(CELL)
        assertTrue(rowCells.count() >= 4, "Unexpected player row cells count: ${rowCells.count()}")
        servingCell = rowCells.nth(0)
        playerName = rowCells.nth(1)
        wonSets = extractWonSets()
        currentSet = rowCells.nth(-2)
        currentGame = rowCells.nth(-1)
    }

    private fun extractWonSets(): List<Locator> {
        return emptyList()
    }
}

fun LocatorAssertions.haveServingIndicator() {
    this.hasText(Pattern.compile("^·$"))
}

fun LocatorAssertions.hasScore(score: Int) {
    this.hasText(score.toString())
}
