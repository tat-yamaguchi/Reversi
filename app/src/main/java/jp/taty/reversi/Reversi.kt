package jp.taty.reversi

class Reversi {

    companion object{
        const val BOARD_SIZE = 8

        const val EXTRA_AI_NAME = "extra_ai_name"
        const val EXTRA_PLAYER_COLOR = "extra_player_color"
    }

    enum class AINames constructor(val value: Int) {
        Simple(1), Normal1(2), Normal2(3);

        companion object {
            fun fromInt(value: Int): AINames {
                return values().firstOrNull { it.value == value } ?: Simple
            }
        }
    }

    enum class CellState constructor(val value: Int) {
        None(1), Black(2), White(3);

        companion object {
            fun fromInt(value: Int): CellState {
                return values().firstOrNull { it.value == value } ?: None
            }
        }
    }

    enum class Result {
        BlackWin,
        WhiteWin,
        Draw
    }

    enum class Warnings{
        /** 石を置けない場所に石を置こうとした */
        CannotPutHare,
    }

    class Point(val x: Int, val y: Int){

        override fun toString(): String {
            return "Point{$x, $y}"
        }
    }

    class Cell(var state: CellState, val x: Int, val y: Int) {

        /** このマスから左方向にある座標の一覧 */
        val linePointsLeft: List<Point>
        /** このマスから上方向にある座標の一覧 */
        val linePointsUp: List<Point>
        /** このマスから右方向にある座標の一覧 */
        val linePointsRight: List<Point>
        /** このマスから下方向にある座標の一覧 */
        val linePointsDown: List<Point>

        /** このマスから左上方向にある座標の一覧 */
        val linePointsUpLeft: List<Point>
        /** このマスから右上方向にある座標の一覧 */
        val linePointsUpRight: List<Point>
        /** このマスから右下方向にある座標の一覧 */
        val linePointsDownRight: List<Point>
        /** このマスから左下方向にある座標の一覧 */
        val linePointsDownLeft: List<Point>

        init{
            linePointsLeft = arrayOfNulls<Point>(x).mapIndexed{ c, _ -> Point(x - c - 1, y)  }
            linePointsUp = arrayOfNulls<Point>(y).mapIndexed{ c, _ -> Point(x, y - c - 1) }
            linePointsRight = arrayOfNulls<Point>(BOARD_SIZE - x - 1).mapIndexed{ c, _ -> Point(x + c + 1, y) }
            linePointsDown = arrayOfNulls<Point>(BOARD_SIZE - y - 1).mapIndexed{ c, _ -> Point(x,y + c + 1) }

            linePointsUpLeft = arrayOfNulls<Point>(minOf(x, y)).mapIndexed{ c, _ -> Point(x - c - 1, y - c - 1) }
            linePointsUpRight = arrayOfNulls<Point>(minOf(BOARD_SIZE - x - 1, y)).mapIndexed{ c, _ -> Point(x + c + 1, y - c - 1) }
            linePointsDownRight = arrayOfNulls<Point>(minOf(BOARD_SIZE - x - 1, BOARD_SIZE - y - 1)).mapIndexed{ c, _ -> Point(x + c + 1, y + c + 1) }
            linePointsDownLeft = arrayOfNulls<Point>(minOf(x, BOARD_SIZE - y - 1)).mapIndexed{ c, p -> Point(x - c - 1, y + c + 1) }
        }

        override fun toString(): String {
            return "Cell{($x, $y), State:$state}"
        }
    }

}