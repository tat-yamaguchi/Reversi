package jp.taty.reversi.model.ai

import jp.taty.reversi.BaseAI
import jp.taty.reversi.R
import jp.taty.reversi.Reversi
import jp.taty.reversi.model.ReversiCondition

class Normal1AI: BaseAI {

    override val aiName = Reversi.AINames.Normal1
    override val displayNameResId = R.string.text_ai_normal1

    override var playerColor: Reversi.CellState = Reversi.CellState.None

    override fun initialize(color: Reversi.CellState) {
        playerColor = color
    }

    override fun compute(condition: ReversiCondition): Reversi.Point {
        val candidates = condition.getCandidateCells(playerColor)
        var maxCell = candidates.maxBy { c -> simulateReverseCount(condition, c.x, c.y) }
        return Reversi.Point(maxCell!!.x, maxCell!!.y)
    }

    private fun simulateReverseCount(condition: ReversiCondition, x: Int, y: Int): Int {
        val cell = condition.cells[x][y]
        return countReverseCellsOnLine(condition, cell.linePointsLeft) +
                countReverseCellsOnLine(condition, cell.linePointsUp) +
                countReverseCellsOnLine(condition, cell.linePointsRight) +
                countReverseCellsOnLine(condition, cell.linePointsDown) +
                countReverseCellsOnLine(condition, cell.linePointsUpLeft) +
                countReverseCellsOnLine(condition, cell.linePointsUpRight) +
                countReverseCellsOnLine(condition, cell.linePointsDownRight) +
                countReverseCellsOnLine(condition, cell.linePointsDownLeft)
    }

    /**
     * 指定されたライン上にあるひっくり返せる石を数える
     * @param condition 現在プレイされている[ReversiCondition]オブジェクト
     * @param linePoints 指定線の座標の一覧
     */
    private fun countReverseCellsOnLine(condition: ReversiCondition, linePoints: List<Reversi.Point>): Int {
        var reverseCount = 0
        if (condition.isCandidateCell(playerColor, linePoints)) {
            val enemy = condition.cells[linePoints[0].x][linePoints[0].y].state // 隣のマスは必ず相手の石
            for (point in linePoints) {
                if (condition.cells[point.x][point.y].state == enemy) {
                    reverseCount++
                } else {
                    break;
                }
            }
        }
        return reverseCount
    }
}