package jp.taty.reversi.model.ai

import jp.taty.reversi.BaseAI
import jp.taty.reversi.Reversi

// TODO: DIに対応する
object AIModel {

    val aiList: List<BaseAI> = arrayListOf(
        SimpleAI(),
        Normal1AI(),
        Normal2AI()
    )

    private val fallbackAI: BaseAI = SimpleAI()

    fun getAI(name: Reversi.AINames): BaseAI {
        for(ai in aiList) {
            if (ai.aiName == name) return ai
        }
        return fallbackAI
    }
}