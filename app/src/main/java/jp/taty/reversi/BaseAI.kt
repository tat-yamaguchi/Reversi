package jp.taty.reversi

import jp.taty.reversi.model.ReversiCondition

interface BaseAI {

    /** AIの種類 */
    val aiName: Reversi.AINames

    /** AIの表示名のリソースID */
    val displayNameResId: Int

    /** AIの石の色 */
    val playerColor: Reversi.CellState

    /**
     * AIを初期化する
     * @param color AIの石の色
     */
    fun initialize(color: Reversi.CellState)

    /**
     * 次の一手を計算する
     * @param condition 現在の盤面の状態
     * @return 次にAIが配置する座標
     */
    fun compute(condition: ReversiCondition): Reversi.Point
}