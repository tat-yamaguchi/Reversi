package jp.taty.reversi.game

import jp.taty.reversi.BasePresenter
import jp.taty.reversi.BaseView
import jp.taty.reversi.Reversi

/** "game" におけるMVPパターンのView <-> Presenterのinterface定義 */
class GameContract {

    interface View : BaseView<Presenter> {

        /** 盤面の状態を表示する */
        fun showBoard(cells: List<List<Reversi.Cell>>)

        /** ゲームが終了したことをViewへ通知する */
        fun gameFinish(result: Reversi.Result, blackCount: Int, whiteCount: Int)

        /** 警告情報を表示するようにViewへ要求する */
        fun showWarning(warning: Reversi.Warnings)

        /** 石を置ける場所の一覧が更新されたことをViewに通知する */
        fun updateCandidatePoints(candidates: List<Reversi.Point>)

        /** 現在のプレイヤーの色(白/黒)を通知する */
        fun showCurrentPlayer(player: Reversi.CellState)

        /** 現在の両プレイヤーの石の数を通知する */
        fun showStoneCounts(blackCount: Int, whiteCount: Int)

        /** パスされた旨のメッセージを表示するようにViewへ要求する */
        fun showPassedMessage(player: Reversi.CellState)

        /** AIが思考を開始したことを通知する */
        fun beginThinking()

        /** AIが思考を終了したことを通知する */
        fun endThinking()
    }

    interface Presenter : BasePresenter {
        /** セルが選択されたことを通知する */
        fun onCellSelected(x: Int, y: Int)

        /** ゲームが終了しViewが終了することを通知する */
        fun onGameFinished()
    }
}