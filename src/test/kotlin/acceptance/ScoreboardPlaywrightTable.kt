package acceptance

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.LocatorAssertions
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.AriaRole.*
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.regex.Pattern

class ScoreboardPlaywrightTable private constructor(scoreboardTable: Locator) {

    private val tableRows: Locator = scoreboardTable.getByRole(ROW)

    val firstPlayer get() = ScoreboardPlaywrightPlayerRow(tableRows.nth(0))
    val secondPlayer get() = ScoreboardPlaywrightPlayerRow(tableRows.nth(1))

    init {
        assertThat(tableRows).hasCount(2)
    }

    fun firstPlayerPoint() = this.firstPlayer.playerName.click()
    fun secondPlayerPoint() = this.secondPlayer.playerName.click()

    companion object {
        fun from(page: Page) = ScoreboardPlaywrightTable(page.getByRole(TABLE))
    }

}

class ScoreboardPlaywrightPlayerRow(playerRow: Locator) {

    private val rowCells: Locator = playerRow.getByRole(CELL)

    val servingCell: Locator get() = rowCells.nth(0)
    val playerName: Locator get() = rowCells.nth(1)
    val wonSets: List<Locator> get() = rowCells.all().drop(2).dropLast(2)
    val currentSet: Locator get() = rowCells.nth(-2)
    val currentGame: Locator get() = rowCells.nth(-1)

    init {
        assertTrue(rowCells.count() >= 4, "Unexpected player row cells count: ${rowCells.count()}")
    }

    fun shouldHaveColumnsCount(count: Int) {
        assertThat(this.rowCells).hasCount(count)
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
