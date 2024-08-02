package it.danielemegna.tennis.domain

import it.danielemegna.tennis.domain.MatchState
import it.danielemegna.tennis.domain.MatchState.Game.GameScore.*
import it.danielemegna.tennis.domain.MatchState.Serving
import it.danielemegna.tennis.domain.usecase.PlayerPoint
import it.danielemegna.tennis.domain.usecase.PlayerPoint.Player
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MatchStateScannerTest {

    private val scanner = MatchStateScanner()

    /*
    @Nested
    inner class GameNormalPoint {


        @Test
        fun `first player point on new game`() {
            val newMatchState = MatchState("p1", "p2")

            val updatedMatchState = scanner.updatedMatch(newMatchState, PlayerPoint.Player.FIRST)

            assertEquals(FIFTEEN, updatedMatchState.currentGame.firstPlayerScore)
            assertEquals(ZERO, updatedMatchState.currentGame.secondPlayerScore)
        }

        @Test
        fun `second player point on new game`() {
            val newMatchState = MatchState("p1", "p2")

            val updatedMatchState = scanner.updatedMatch(newMatchState, PlayerPoint.Player.SECOND)

            assertEquals(FIFTEEN, updatedMatchState.currentGame.secondPlayerScore)
            assertEquals(ZERO, updatedMatchState.currentGame.firstPlayerScore)
        }

        @Test
        fun `first player point on progress game`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(THIRTY, FIFTEEN)
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(FORTY, updatedMatchState.currentGame.firstPlayerScore)
            assertEquals(FIFTEEN, updatedMatchState.currentGame.secondPlayerScore)
        }

        @Test
        fun `second player point on progress game`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(THIRTY, FIFTEEN)
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(THIRTY, updatedMatchState.currentGame.secondPlayerScore)
            assertEquals(THIRTY, updatedMatchState.currentGame.firstPlayerScore)
        }

        @Test
        fun `first player close to set point`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(5, 4),
                currentGame = MatchState.Game(THIRTY, FIFTEEN)
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(FORTY, updatedMatchState.currentGame.firstPlayerScore)
            assertEquals(FIFTEEN, updatedMatchState.currentGame.secondPlayerScore)
            assertEquals(MatchState.Set(5, 4), updatedMatchState.currentSet)
            assertEquals(emptyList(), updatedMatchState.wonSets)
        }

        @Test
        fun `point to cancel a set point`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(5, 4),
                currentGame = MatchState.Game(FORTY, FIFTEEN)
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(FORTY, updatedMatchState.currentGame.firstPlayerScore)
            assertEquals(THIRTY, updatedMatchState.currentGame.secondPlayerScore)
            assertEquals(MatchState.Set(5, 4), updatedMatchState.currentSet)
            assertEquals(emptyList(), updatedMatchState.wonSets)
        }

        @Test
        fun `game close to tiebreak`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 6, secondPlayerScore = 5),
                currentGame = MatchState.Game(ZERO, THIRTY),
                serving = Serving.SECOND_PLAYER
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(MatchState.Game(ZERO, FORTY), updatedMatchState.currentGame)
            assertEquals(MatchState.Set(6, 5), updatedMatchState.currentSet)
            assertEquals(emptyList(), updatedMatchState.wonSets)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
            assertNull(matchState.currentSet.tieBreak)
        }

    }
    */

    @Nested
    inner class GameWinning {

        @Test
        fun `game won by first player`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(FORTY, FIFTEEN)
            )

            assertTrue(scanner.wouldBeGamePoint(matchState, Player.FIRST))
            assertFalse(scanner.wouldBeGamePoint(matchState, Player.SECOND))
            //assertFalse(scanner.wouldBeSetPoint(matchState, Player.FIRST))
            //assertFalse(scanner.wouldBeStartTieBreak(matchState, Player.FIRST))
        }

        @Test
        fun `game won by second player`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(THIRTY, FORTY)
            )

            assertTrue(scanner.wouldBeGamePoint(matchState, Player.SECOND))
            assertFalse(scanner.wouldBeGamePoint(matchState, Player.FIRST))
            //assertFalse(scanner.wouldBeSetPoint(matchState, Player.SECOND))
            //assertFalse(scanner.wouldBeStartTieBreak(matchState, Player.SECOND))
        }

    }

    /*
    @Nested
    inner class AdvantagePoints {

        @Test
        fun `first player goes to advantage on point when forty-forty`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(FORTY, FORTY)
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(MatchState.Game(ADVANTAGE, FORTY), updatedMatchState.currentGame)
            assertEquals(0, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.secondPlayerScore)
        }

        @Test
        fun `second player goes to advantage on point when forty-forty`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(FORTY, FORTY)
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(MatchState.Game(FORTY, ADVANTAGE), updatedMatchState.currentGame)
            assertEquals(0, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.secondPlayerScore)
        }

        @Test
        fun `players come back forty-forty on first player untapped advantage`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(ADVANTAGE, FORTY)
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(MatchState.Game(FORTY, FORTY), updatedMatchState.currentGame)
            assertEquals(0, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.secondPlayerScore)
        }

        @Test
        fun `players come back forty-forty on second player untapped advantage`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(FORTY, ADVANTAGE)
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(MatchState.Game(FORTY, FORTY), updatedMatchState.currentGame)
            assertEquals(0, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.secondPlayerScore)
        }

        @Test
        fun `first player win game after advantage point`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(ADVANTAGE, FORTY)
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(1, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
        }

        @Test
        fun `second player win game after advantage point`() {
            val matchState = MatchState("p1", "p2").copy(
                currentGame = MatchState.Game(FORTY, ADVANTAGE)
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(1, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
        }

    }

    @Nested
    inner class ServingPlayerUpdate {

        @Test
        fun `first player still serving in ongoing first game`() {
            val matchState = MatchState("p1", "p2").copy(
                serving = Serving.FIRST_PLAYER,
                currentGame = MatchState.Game(FIFTEEN, ZERO)
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `second player serve when a game served by first player is completed`() {
            val matchState = MatchState("p1", "p2").copy(
                serving = Serving.FIRST_PLAYER,
                currentGame = MatchState.Game(FORTY, ZERO)
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
        }

        @Test
        fun `first player serve when a game served by second player is completed`() {
            val matchState = MatchState("p1", "p2").copy(
                serving = Serving.SECOND_PLAYER,
                currentGame = MatchState.Game(FIFTEEN, FORTY)
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `first player serve also when a game served by second player is won by first player`() {
            val matchState = MatchState("p1", "p2").copy(
                serving = Serving.SECOND_PLAYER,
                currentGame = MatchState.Game(FORTY, ZERO)
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
        }

        @Test
        fun `second player starts new set when first player serves last game in previous set`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 5, secondPlayerScore = 1),
                currentGame = MatchState.Game(FORTY, ZERO),
                serving = Serving.FIRST_PLAYER // this means first player started the set
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
        }

        @Test
        fun `first player starts new set when second player serves last game in previous set`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 5, secondPlayerScore = 2),
                currentGame = MatchState.Game(FORTY, ZERO),
                serving = Serving.SECOND_PLAYER // this means first player started the set
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
        }

        @Test
        fun `first player starts tie-break when second player serves last game in previous set`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 6, secondPlayerScore = 5),
                currentGame = MatchState.Game(FIFTEEN, FORTY),
                serving = Serving.SECOND_PLAYER
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
            val expectedTieBreak = MatchState.TieBreak(
                playerStartedTheTieBreak = Serving.FIRST_PLAYER,
                firstPlayerScore = 0,
                secondPlayerScore = 0
            )
            assertEquals(expectedTieBreak, updatedMatchState.currentSet.tieBreak)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
        }

        @Test
        fun `second player starts tie-break when first player serves last game in previous set`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 5, secondPlayerScore = 6),
                currentGame = MatchState.Game(FORTY, THIRTY),
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
            val expectedTieBreak = MatchState.TieBreak(
                playerStartedTheTieBreak = Serving.SECOND_PLAYER,
                firstPlayerScore = 0,
                secondPlayerScore = 0
            )
            assertEquals(expectedTieBreak, updatedMatchState.currentSet.tieBreak)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
        }

        @Test
        fun `during tie-break serving is immediately changed after the first point`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(
                    firstPlayerScore = 6,
                    secondPlayerScore = 6,
                    tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 0, 0)
                ),
                currentGame = MatchState.Game(ZERO, ZERO),
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
            assertEquals(MatchState.TieBreak(Serving.FIRST_PLAYER, 1, 0), updatedMatchState.currentSet.tieBreak)
        }

        @Test
        fun `during tie-break serving is changed every two point`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(
                    firstPlayerScore = 6,
                    secondPlayerScore = 6,
                    tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 1, 0)
                ),
                serving = Serving.SECOND_PLAYER
            )

            var updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
            updatedMatchState = scanner.updatedMatch(updatedMatchState, PlayerPoint.Player.SECOND)
            assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
            updatedMatchState = scanner.updatedMatch(updatedMatchState, PlayerPoint.Player.FIRST)
            assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
            updatedMatchState = scanner.updatedMatch(updatedMatchState, PlayerPoint.Player.FIRST)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }

        @Nested
        @DisplayName("when tie-break ends the next game is served by player who did not started the tie-break")
        inner class TieBreakEnd {

            @Test
            fun `1st player started tie-break - 2nd player ends tie-break - 2nd player start next game`() {
                val matchState = MatchState("p1", "p2").copy(
                    currentSet = MatchState.Set(
                        firstPlayerScore = 6,
                        secondPlayerScore = 6,
                        tieBreak = MatchState.TieBreak(
                            playerStartedTheTieBreak = Serving.FIRST_PLAYER,
                            firstPlayerScore = 6,
                            secondPlayerScore = 4
                        )
                    ),
                    serving = Serving.SECOND_PLAYER
                )

                val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

                assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
            }

            @Test
            fun `1st player started tie-break - 1st player ends tie-break - 2nd player start next game`() {
                val matchState = MatchState("p1", "p2").copy(
                    currentSet = MatchState.Set(
                        firstPlayerScore = 6,
                        secondPlayerScore = 6,
                        tieBreak = MatchState.TieBreak(
                            playerStartedTheTieBreak = Serving.FIRST_PLAYER,
                            firstPlayerScore = 7,
                            secondPlayerScore = 8
                        )
                    ),
                    serving = Serving.FIRST_PLAYER
                )

                val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

                assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
            }

            @Test
            fun `2nd player started tie-break - 1st player ends tie-break - 1st player start next game`() {
                val matchState = MatchState("p1", "p2").copy(
                    currentSet = MatchState.Set(
                        firstPlayerScore = 6,
                        secondPlayerScore = 6,
                        tieBreak = MatchState.TieBreak(
                            playerStartedTheTieBreak = Serving.SECOND_PLAYER,
                            firstPlayerScore = 6,
                            secondPlayerScore = 3
                        )
                    ),
                    serving = Serving.FIRST_PLAYER
                )

                val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

                assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
            }

            @Test
            fun `2nd player started tie-break - 2nd player ends tie-break - 1st player start next game`() {
                val matchState = MatchState("p1", "p2").copy(
                    currentSet = MatchState.Set(
                        firstPlayerScore = 6,
                        secondPlayerScore = 6,
                        tieBreak = MatchState.TieBreak(
                            playerStartedTheTieBreak = Serving.SECOND_PLAYER,
                            firstPlayerScore = 9,
                            secondPlayerScore = 10
                        )
                    ),
                    serving = Serving.SECOND_PLAYER
                )

                val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

                assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
            }

        }
    }

    @Nested
    inner class SetWinning {

        @Test
        fun `first player wins the set on six games`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 5, secondPlayerScore = 1),
                currentGame = MatchState.Game(FORTY, ZERO),
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            val expectedWonSet = MatchState.Set(firstPlayerScore = 6, secondPlayerScore = 1)
            assertEquals(listOf(expectedWonSet), updatedMatchState.wonSets)
            assertEquals(0, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `second player wins the set on six games`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 4, secondPlayerScore = 5),
                currentGame = MatchState.Game(FORTY, ADVANTAGE),
                serving = Serving.SECOND_PLAYER
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            val expectedWonSet = MatchState.Set(firstPlayerScore = 4, secondPlayerScore = 6)
            assertEquals(listOf(expectedWonSet), updatedMatchState.wonSets)
            assertEquals(0, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(0, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
            assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `player do not wins the set on six games when five-all`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 5, secondPlayerScore = 5),
                currentGame = MatchState.Game(FORTY, FIFTEEN),
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(6, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(5, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(emptyList(), updatedMatchState.wonSets)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `first player wins the set on seven games when second has five`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 6, secondPlayerScore = 5),
                currentGame = MatchState.Game(FORTY, THIRTY),
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            val expectedWonSet = MatchState.Set(firstPlayerScore = 7, secondPlayerScore = 5)
            assertEquals(listOf(expectedWonSet), updatedMatchState.wonSets)
            assertEquals(MatchState.Set(0, 0), updatedMatchState.currentSet)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `second player wins the set on seven games when first has five`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 5, secondPlayerScore = 6),
                currentGame = MatchState.Game(FORTY, ADVANTAGE),
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            val expectedWonSet = MatchState.Set(firstPlayerScore = 5, secondPlayerScore = 7)
            assertEquals(listOf(expectedWonSet), updatedMatchState.wonSets)
            assertEquals(MatchState.Set(0, 0), updatedMatchState.currentSet)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }
    }

    @Nested
    inner class TieBreak {

        @Test
        fun `still not tiebreak on six-five`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 5, secondPlayerScore = 5),
                currentGame = MatchState.Game(FORTY, THIRTY),
            )
            assertNull(matchState.currentSet.tieBreak)

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertNull(matchState.currentSet.tieBreak)
            assertEquals(6, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(5, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(emptyList(), updatedMatchState.wonSets)
        }

        @Test
        fun `it starts on six-all`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(firstPlayerScore = 6, secondPlayerScore = 5),
                currentGame = MatchState.Game(FIFTEEN, FORTY),
                serving = Serving.SECOND_PLAYER
            )
            assertNull(matchState.currentSet.tieBreak)

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            val expectedTieBreak = MatchState.TieBreak(
                playerStartedTheTieBreak = Serving.FIRST_PLAYER,
                firstPlayerScore = 0,
                secondPlayerScore = 0
            )
            assertEquals(expectedTieBreak, updatedMatchState.currentSet.tieBreak)
            assertEquals(6, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(6, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(emptyList(), updatedMatchState.wonSets)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
            assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `during tie break points increase tie-break score instead of game score`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(
                    firstPlayerScore = 6,
                    secondPlayerScore = 6,
                    tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 0, 0)
                ),
                currentGame = MatchState.Game(ZERO, ZERO),
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(MatchState.TieBreak(Serving.FIRST_PLAYER, 1, 0), updatedMatchState.currentSet.tieBreak)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
            assertEquals(6, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(6, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(emptyList(), updatedMatchState.wonSets)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `first player wins tie-break on seven points`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(
                    firstPlayerScore = 6,
                    secondPlayerScore = 6,
                    tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 6, 2)
                ),
                currentGame = MatchState.Game(ZERO, ZERO),
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertNull(updatedMatchState.currentSet.tieBreak)
            val expectedWonSet = MatchState.Set(
                firstPlayerScore = 7,
                secondPlayerScore = 6,
                tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 7, 2)
            )
            assertEquals(listOf(expectedWonSet), updatedMatchState.wonSets)
            assertEquals(MatchState.Set(0, 0), updatedMatchState.currentSet)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `second player wins tie-break on seven points`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(
                    firstPlayerScore = 6,
                    secondPlayerScore = 6,
                    tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 5, 6)
                ),
                currentGame = MatchState.Game(ZERO, ZERO),
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertNull(updatedMatchState.currentSet.tieBreak)
            val expectedWonSet = MatchState.Set(
                firstPlayerScore = 6,
                secondPlayerScore = 7,
                tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 5, 7)
            )
            assertEquals(listOf(expectedWonSet), updatedMatchState.wonSets)
            assertEquals(MatchState.Set(0, 0), updatedMatchState.currentSet)
            assertEquals(MatchState.Game(ZERO, ZERO), updatedMatchState.currentGame)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `tie-break continues on seven-six`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(
                    firstPlayerScore = 6,
                    secondPlayerScore = 6,
                    tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 6, 6)
                ),
                currentGame = MatchState.Game(ZERO, ZERO),
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertEquals(MatchState.TieBreak(Serving.FIRST_PLAYER, 7, 6), updatedMatchState.currentSet.tieBreak)
            assertEquals(6, updatedMatchState.currentSet.firstPlayerScore)
            assertEquals(6, updatedMatchState.currentSet.secondPlayerScore)
            assertEquals(emptyList(), updatedMatchState.wonSets)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `tie-break continues indefinitely on one-point score gap`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(
                    firstPlayerScore = 6,
                    secondPlayerScore = 6,
                    tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 13, 13)
                ),
                currentGame = MatchState.Game(ZERO, ZERO),
                serving = Serving.SECOND_PLAYER
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            val expectedSet = MatchState.Set(
                firstPlayerScore = 6,
                secondPlayerScore = 6,
                tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 13, 14)
            )
            assertEquals(expectedSet, updatedMatchState.currentSet)
            assertEquals(emptyList(), updatedMatchState.wonSets)
            assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `first player wins tie-break on two-point score gap`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(
                    firstPlayerScore = 6,
                    secondPlayerScore = 6,
                    tieBreak = MatchState.TieBreak(Serving.SECOND_PLAYER, 9, 8)
                ),
                currentGame = MatchState.Game(ZERO, ZERO),
                serving = Serving.FIRST_PLAYER
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.FIRST)

            assertNull(updatedMatchState.currentSet.tieBreak)
            val expectedWonSet = MatchState.Set(
                firstPlayerScore = 7,
                secondPlayerScore = 6,
                tieBreak = MatchState.TieBreak(Serving.SECOND_PLAYER, 10, 8),
            )
            assertEquals(listOf(expectedWonSet), updatedMatchState.wonSets)
            assertEquals(Serving.FIRST_PLAYER, updatedMatchState.serving)
        }

        @Test
        fun `second player wins tie-break on two-point score gap`() {
            val matchState = MatchState("p1", "p2").copy(
                currentSet = MatchState.Set(
                    firstPlayerScore = 6,
                    secondPlayerScore = 6,
                    tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 14, 15)
                ),
                currentGame = MatchState.Game(ZERO, ZERO),
                serving = Serving.SECOND_PLAYER
            )

            val updatedMatchState = scanner.updatedMatch(matchState, PlayerPoint.Player.SECOND)

            assertNull(updatedMatchState.currentSet.tieBreak)
            val expectedWonSet = MatchState.Set(
                firstPlayerScore = 6,
                secondPlayerScore = 7,
                tieBreak = MatchState.TieBreak(Serving.FIRST_PLAYER, 14, 16)
            )
            assertEquals(listOf(expectedWonSet), updatedMatchState.wonSets)
            assertEquals(Serving.SECOND_PLAYER, updatedMatchState.serving)
        }

    }
    */
}
