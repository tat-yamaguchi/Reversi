package jp.taty.reversi.game

import jp.taty.reversi.BasePresenter
import jp.taty.reversi.BaseView
import jp.taty.reversi.Reversi

class GameContract {

    interface View : BaseView<Presenter> {
        fun showBoard(cells: List<List<Reversi.Cell>>)

        fun showResult(result: Reversi.Result, blackCount: Int, whiteCount: Int)

        fun showWarning(warning: Reversi.Warnings)

        fun updateCandidatePoints(candidates: List<Reversi.Point>)

        fun showCurrentPlayer(player: Reversi.CellState)
        fun showStoneCounts(blackCount: Int, whiteCount: Int)
    }

    interface Presenter : BasePresenter {
        fun onCellSelected(x: Int, y: Int)
    }
}