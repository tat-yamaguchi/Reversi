package jp.taty.reversi.game

import jp.taty.reversi.BaseAI
import jp.taty.reversi.Reversi
import jp.taty.reversi.model.ReversiCondition
import jp.taty.reversi.model.ReversiConfig
import jp.taty.reversi.model.ai.AIModel

class GamePresenter(private val gameView: GameContract.View, config: ReversiConfig): GameContract.Presenter {

    private val ai: BaseAI
    private val playerColor: Reversi.CellState

    private val condition = ReversiCondition()
    private val executors = Reversi.Executors()

    /** 今がプレイヤーのターンであるか */
    private val isPlayerTurn: Boolean
        get() = condition.currentPlayer == playerColor

    /** 今がAIのターンであるか */
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
        onTurnChanged()
    }

    private fun cellsToPoints(cells: List<Reversi.Cell>): List<Reversi.Point> {
        val points = mutableListOf<Reversi.Point>()
        cells.forEach {
            points.add(Reversi.Point(it.x, it.y))
        }
        return points
    }

    /**
     * 選択されたセルが石の置ける場所であれば石を置き、ターンを進める
     */
    override fun onCellSelected(x: Int, y: Int) {
        if (!isPlayerTurn) return

        if (condition.isCandidateCells(playerColor, x, y)) {
            condition.putToCell(condition.currentPlayer, x, y)
            condition.changeTurn()
            onTurnChanged()
        } else {
            gameView.showWarning(Reversi.Warnings.CannotPutHare)
        }
    }

    /**
     * Viewが終了する際に呼ばれる
     * この後Presenterも破棄されるため後処理が必要であれば実行する
     */
    override fun onGameFinished() {
        // Nothing to do.
    }

    /**
     * 現在のプレイヤーが石を置ける場所があるか判断する
     */
    private fun existsCandidate(): Boolean {
        return condition.getCandidateCells(condition.currentPlayer).isNotEmpty()
    }

    /**
     * ゲームが続行可能であるか(手詰まりでないか)を判断する
     */
    private fun canContinueGame(): Boolean {
        return existsCandidate() || condition.getCandidateCells(condition.nextPlayer()).isNotEmpty()
    }

    /**
     * ターンが変更された際に必要な情報をViewへ通知し、ゲームが続行可能か判断を行う
     * また、次がAIのターンであればAIの思考を開始する
     */
    private fun onTurnChanged() {
        gameView.updateCandidatePoints(cellsToPoints(condition.getCandidateCells(condition.currentPlayer)))
        gameView.showBoard(condition.cells)
        gameView.showCurrentPlayer(condition.currentPlayer)
        gameView.showStoneCounts(condition.blackCells.size, condition.whiteCells.size)

        if (!canContinueGame()) {
            // ゲーム終了
            gameView.gameFinish(condition.getResult(), condition.blackCells.size, condition.whiteCells.size)
        } else if (!existsCandidate()) {
            // 置ける場所がない -> パス
            performPass()
        } else {
            if (isAITurn) {
                performAI()
            }
        }
    }

    /**
     * 現在のプレイヤーがパスをする
     */
    private fun performPass() {
        gameView.showPassedMessage(condition.currentPlayer)
        condition.changeTurn()
        onTurnChanged()
    }

    /**
     * AIのターンの処理を実行する
     * AIの処理は別スレッドにて行うため、この関数がすぐに処理を返却する
     */
    private fun performAI() {
        gameView.beginThinking()
        executors.thread.execute {
            val point = ai.compute(condition)
            executors.main.execute {
                condition.putToCell(ai.playerColor, point.x, point.y)
                condition.changeTurn()
                onTurnChanged()
                gameView.endThinking()
            }
        }
    }
}