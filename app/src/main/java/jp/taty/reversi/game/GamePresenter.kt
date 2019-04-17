package jp.taty.reversi.game

import jp.taty.reversi.BaseAI
import jp.taty.reversi.Reversi
import jp.taty.reversi.model.ReversiCondition
import jp.taty.reversi.model.ReversiConfig
import jp.taty.reversi.model.ai.AIModel

class GamePresenter(private val gameView: GameContract.View, private val config: ReversiConfig): GameContract.Presenter {

    private val ai: BaseAI
    private val playerColor: Reversi.CellState

    private val condition = ReversiCondition()
    private var isActionFrozen = false

    private val isPlayerTurn: Boolean
        get() = condition.currentPlayer == playerColor

    private val isAITurn: Boolean
        get() = !isPlayerTurn

    init {
        gameView.presenter = this

        playerColor = config.playerColor
        ai = AIModel.getAI(config.aiName)
        ai.initialize(condition.anotherPlayer(playerColor))
    }

    override fun start() {
        gameView.showBoard(condition.cells)
        gameView.updateCandidatePoints(cellsToPoints(condition.getCandidateCells(condition.currentPlayer)))
    }

    private fun cellsToPoints(cells: List<Reversi.Cell>): List<Reversi.Point> {
        var points = mutableListOf<Reversi.Point>()
        cells.forEach {
            points.add(Reversi.Point(it.x, it.y))
        }
        return points
    }

    override fun onCellSelected(x: Int, y: Int) {
        if (isActionFrozen) return;

        if (condition.isCandidateCells(condition.currentPlayer, x, y)) {
            putToCell(x, y)
        } else {
            gameView.showWarning(Reversi.Warnings.CannotPutHare)
        }
    }

    private fun putToCell(x: Int, y: Int) {
        isActionFrozen = true;
        condition.putToCell(condition.currentPlayer, x, y)
        condition.changeTurn()
        onTurnChanged()
    }

    private fun onTurnChanged() {
        var isGameFinished = false
        val candidates = condition.getCandidateCells(condition.currentPlayer)
        if (candidates.isEmpty()) {
            // 置ける場所がない
            val nextCandidates = condition.getCandidateCells(condition.nextPlayer())
            if (nextCandidates.isEmpty()) {
                // game finished.
                gameView.showResult(condition.getResult(), condition.blackCells.size, condition.whiteCells.size)
                isGameFinished = true
            } else {
                // pass
                condition.changeTurn()
            }
        }

        gameView.updateCandidatePoints(cellsToPoints(condition.getCandidateCells(condition.currentPlayer)))
        gameView.showBoard(condition.cells)
        gameView.showCurrentPlayer(condition.currentPlayer)
        gameView.showStoneCounts(condition.blackCells.size, condition.whiteCells.size)

        if (isGameFinished) return

        // TODO: AIの長考に耐えられるようにタスク化
        if (isAITurn) {
            // AIのターン
            performAI()
        }

        isActionFrozen = false
    }

    private fun performAI() {
        val point = ai.compute(condition)
        condition.putToCell(ai.playerColor, point.x, point.y)
        condition.changeTurn()
        onTurnChanged()
    }
}