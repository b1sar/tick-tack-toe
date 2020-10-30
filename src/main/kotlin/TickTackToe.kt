package tictactoe

import kotlin.math.abs

sealed class GameState(var message: String)
class XWins() : GameState("X wins")
class OWins() : GameState("O wins")
class Draw() : GameState("Draw")
class NotFinished() : GameState("Game not finished")
class Impossible() : GameState("Impossible")

enum class Player {
    X {
        override fun name(): Char = 'X'
        override fun not(): Player = O
    },
    O {
        override fun name(): Char = 'O'
        override fun not(): Player = X
    };

    abstract fun name() : Char
    abstract operator fun not(): Player
}

val EMPTY_CELL = ' '
val wins = arrayOf(
    intArrayOf(0, 1, 2),
    intArrayOf(3, 4, 5),
    intArrayOf(6, 7, 8),
    intArrayOf(0, 3, 6),
    intArrayOf(1, 4, 7),
    intArrayOf(2, 5, 8),
    intArrayOf(0, 4, 8),
    intArrayOf(2, 4, 6),
)

fun main() {
    var grid = "         "
    var gameTurn: Player = Player.X

    do {
        printGrid(grid)
        println("Game turn: ${gameTurn.name()}")
        grid = gridWithNewInput(grid, gameTurn)
        gameTurn = !gameTurn

        val gameState = checkGameState(grid)
        when (gameState) {
            is XWins -> println(gameState.message)
            is OWins -> println(gameState.message)
            is Draw-> println(gameState.message)
            is Impossible -> println(gameState.message)
            is NotFinished -> continue
        }
    } while (gameState is NotFinished)
}

fun gridWithNewInput(grid: String, player: Player) : String {
    val newGrid = grid.toCharArray()
    newGrid[pairToIndex(getMoveFromUser(grid))] = player.name()
    return newGrid.joinToString("")
}

fun printGrid(grid: String) = println(
    """
        ---------
        | ${grid[0]} ${grid[1]} ${grid[2]} |
        | ${grid[3]} ${grid[4]} ${grid[5]} |
        | ${grid[6]} ${grid[7]} ${grid[8]} |
        ---------
    """.trimIndent()
)

fun getMoveFromUser(grid: String): Pair<Int, Int> {
    while (true) {
        print("Enter the coordinates: ")
        val input = readLine()!!.split(" ").map { it.toIntOrNull() }
        if (input.size != 2 || input.any { it == null }) {
            println("You should enter numbers!")
        } else if (input.any { it !in 1..3 }) {
            println("Coordinates should be from 1 to 3")
        } else if (!isCellEmpty(Pair(input[1]!!, input[0]!!), grid)) {
            println("This cell is occupied! Choose another one!")
        } else {
            return Pair(input[1]!!, input[0]!!)
        }
    }
}

fun isCellEmpty(indexes: Pair<Int, Int>, grid: String) : Boolean = grid[pairToIndex(indexes)] == EMPTY_CELL

fun pairToIndex(pair: Pair<Int, Int>) = 3*(pair.first-1) + pair.second - 1

fun checkGameState(grid: String) : GameState = when {
    !isPossible(grid) -> Impossible()
    isWinner(Player.X.name(), grid)-> XWins()
    isWinner(Player.O.name(), grid) -> OWins()
    else -> if (!gridHasEmptyCells(grid)) { Draw() } else { NotFinished() }
}

fun isPossible(grid: String) : Boolean {
    return abs(grid.filter { it==Player.O.name() }.count() - grid.filter { it == Player.X.name()}.count()) < 2
}

fun gridHasEmptyCells(grid: String): Boolean = EMPTY_CELL in grid

fun isWinner(symbol: Char, grid: String): Boolean = wins.any { winCells -> winCells.all { grid[it] == symbol } }