package jp.taty.reversi.entry

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import jp.taty.reversi.BaseAI
import jp.taty.reversi.R
import jp.taty.reversi.Reversi
import jp.taty.reversi.game.GameActivity
import jp.taty.reversi.model.ai.AIModel

/*
 * 非常に小さいためViewとPresenterをActivityに実装をする
 * 機能を追加する場合はViewとPresenterを分ける
 */

/**
 * ゲームを始める準備をするActivity
 * アプリ起動時に立ち上がる
 */
class EntryActivity : AppCompatActivity() {

    lateinit var spinnerAi: Spinner
    lateinit var spinnerPlayer: Spinner

    lateinit var spinnerAiAdapter: ArrayAdapter<AISpinnerItem>
    lateinit var spinnerPlayerAdapter: ArrayAdapter<PlayerSpinnerItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        initViews(window.decorView)
    }

    private fun initViews(root: View) {
        spinnerAi = root.findViewById<Spinner>(R.id.spinner_ai).apply {
            spinnerAiAdapter = createAISpinnerAdapter()
            adapter = spinnerAiAdapter
        }
        spinnerPlayer = root.findViewById<Spinner>(R.id.spinner_color).apply {
            spinnerPlayerAdapter = createPlayerSpinnerAdapter()
            adapter = spinnerPlayerAdapter
        }

        root.findViewById<Button>(R.id.btn_game_start).also {
            it.setOnClickListener {
                startActivity(Intent(this, GameActivity::class.java).apply {
                    putExtra(Reversi.EXTRA_AI_NAME, spinnerAiAdapter.getItem(spinnerAi.selectedItemPosition).aiName.value)
                    putExtra(Reversi.EXTRA_PLAYER_COLOR, spinnerPlayerAdapter.getItem(spinnerPlayer.selectedItemPosition).color.value)
                })
            }
        }
    }

    private fun createAISpinnerAdapter(): ArrayAdapter<AISpinnerItem> {
        return ArrayAdapter<AISpinnerItem>(this, android.R.layout.simple_spinner_item).apply {
            AIModel.aiList.forEach {
                add(AISpinnerItem(it.aiName, getString(it.displayNameResId)))
            }
        }
    }

    private fun createPlayerSpinnerAdapter(): ArrayAdapter<PlayerSpinnerItem> {
        return ArrayAdapter<PlayerSpinnerItem>(this, android.R.layout.simple_spinner_item).apply {
            add(PlayerSpinnerItem(Reversi.CellState.Black, getString(R.string.text_player_black)))
            add(PlayerSpinnerItem(Reversi.CellState.White, getString(R.string.text_player_white)))
        }
    }

    private fun setupAISpinner(spinner: Spinner) {
        var adapter = ArrayAdapter<AISpinnerItem>(this, android.R.layout.simple_spinner_item)
        AIModel.aiList.forEach {
            adapter.add(AISpinnerItem(it.aiName, getString(it.displayNameResId)))
        }
        spinner.adapter = adapter;
1    }

    class AISpinnerItem(val aiName: Reversi.AINames, val displayText: String) {
        override fun toString(): String {
            return displayText;
        }
    }

    class PlayerSpinnerItem(val color: Reversi.CellState, val displayText: String) {
        override fun toString(): String {
            return displayText;
        }
    }
}