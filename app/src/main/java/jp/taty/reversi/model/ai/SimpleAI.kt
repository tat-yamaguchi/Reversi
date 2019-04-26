package jp.taty.reversi.model.ai

import jp.taty.reversi.BaseAI
import jp.taty.reversi.R
import jp.taty.reversi.Reversi
import jp.taty.reversi.model.ReversiCondition

/**
 * シンプルなAI
 * 石を置ける場所のうち一番最初に発見した場所に石を置く
 */
class SimpleAI: BaseAI {

    override val aiName = Reversi.AINames.Simple
    override val displayNameResId = R.string.text_ai_simple

    override var playerColor: Reversi.CellState = Reversi.CellState.None

    override fun initialize(color: Reversi.CellState) {
        playerColor = color
    }

    override fun compute(condition: ReversiCondition): Reversi.Point {
        // 早すぎるので考えてる感を出すために1秒寝る
        Thread.sleep(1000)

        val candidates = condition.getCandidateCells(playerColor)
        return Reversi.Point(candidates[0].x, candidates[0].y)
    }
}