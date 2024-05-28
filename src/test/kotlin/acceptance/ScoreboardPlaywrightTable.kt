package acceptance

import com.microsoft.playwright.Locator
import com.microsoft.playwright.assertions.LocatorAssertions
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.AriaRole.CELL
import com.microsoft.playwright.options.AriaRole.ROW
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.regex.Pattern
import kotlin.test.assertEquals

class ScoreboardPlaywrightTable(scoreboardTable: Locator) {

    val firstPlayer: ScoreboardPlaywrightPlayerRow
    val secondPlayer: ScoreboardPlaywrightPlayerRow

    init {
        assertEquals(1, scoreboardTable.count())
        assertThat(scoreboardTable).isVisible()
        val tableRows = scoreboardTable.getByRole(ROW)
        assertEquals(2, tableRows.count(), "Unexpected table rows count: ${tableRows.count()}")
        firstPlayer = ScoreboardPlaywrightPlayerRow(tableRows.nth(0))
        secondPlayer = ScoreboardPlaywrightPlayerRow(tableRows.nth(1))
    }

}

class ScoreboardPlaywrightPlayerRow(playerRow: Locator) {

    val servingCell: Locator
    val playerName: Locator
    val currentSet: Locator
    val currentGame: Locator

    init {
        assertThat(playerRow).isVisible()
        val rowCells = playerRow.getByRole(CELL)
        assertTrue(rowCells.count() >= 4, "Unexpected player row cells count: ${rowCells.count()}")
        servingCell = rowCells.nth(0)
        playerName = rowCells.nth(1)
        currentSet = rowCells.nth(-2)
        currentGame = rowCells.nth(-1)
    }
}

fun LocatorAssertions.haveServingIndicator() {
    this.hasText(Pattern.compile("^·$"))
}

fun LocatorAssertions.hasScore(score: Int) {
    this.hasText(score.toString())
}
