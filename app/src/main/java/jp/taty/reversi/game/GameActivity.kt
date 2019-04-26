package jp.taty.reversi.game

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import jp.taty.reversi.R
import jp.taty.reversi.Reversi
import jp.taty.reversi.model.ReversiConfig

class GameActivity: AppCompatActivity(), GameContract.View {

    lateinit var cellViews: List<List<ImageView>>
    lateinit var gridLayout: GridLayout

    lateinit var stoneDrawableBlack: Drawable
    lateinit var stoneDrawableWhite: Drawable

    lateinit var textViewCurrentPlayer: TextView
    lateinit var textViewBlackCount: TextView
    lateinit var textViewWhiteCount: TextView

    override lateinit var presenter: GameContract.Presenter

    lateinit var textViewThinkingMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        presenter = GamePresenter(this, parseConfigFromIntent(intent))
        initViews(window.decorView)
    }

    private fun parseConfigFromIntent(intent: Intent): ReversiConfig{
        return ReversiConfig(
            Reversi.AINames.fromInt(intent.getIntExtra(Reversi.EXTRA_AI_NAME, Reversi.AINames.Simple.value)),
            Reversi.CellState.fromInt(intent.getIntExtra(Reversi.EXTRA_PLAYER_COLOR, Reversi.CellState.Black.value))
        )
    }

    private fun initViews(root: View) {
        gridLayout = root.findViewById<GridLayout>(R.id.game_board_grid)

        cellViews = arrayOfNulls<List<ImageView>>(Reversi.BOARD_SIZE).mapIndexed { x, _ ->
            arrayOfNulls<ImageView>(Reversi.BOARD_SIZE).mapIndexed { y, _ ->
                val cellView = layoutInflater.inflate(R.layout.grid_cell, root as ViewGroup, false).apply{
                    setOnClickListener{
                        presenter.onCellSelected(x, y)
                    }
                }
                gridLayout.addView(cellView)
                cellView.findViewById(R.id.cell_image_view) as ImageView
            }
        }

        textViewCurrentPlayer = root.findViewById<TextView>(R.id.game_current_player_text)
        textViewBlackCount = root.findViewById<TextView>(R.id.game_black_player_count_text)
        textViewWhiteCount = root.findViewById<TextView>(R.id.game_white_player_count_text)
        textViewThinkingMessage = root.findViewById<TextView>(R.id.tv_thinking_message)

        stoneDrawableBlack = ContextCompat.getDrawable(this, R.drawable.stone_black)!!
        stoneDrawableWhite = ContextCompat.getDrawable(this, R.drawable.stone_white)!!

        root.findViewById<Button>(R.id.btn_game_finish).apply {
            setOnClickListener { finish() }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    //
    // GameContract.View interfaces.
    //

    override fun showBoard(cells: List<List<Reversi.Cell>>) {
        cells.flatten().forEach {
            val view = cellViews[it.x][it.y]
            when(it.state) {
                Reversi.CellState.Black -> view.setImageDrawable(stoneDrawableBlack)
                Reversi.CellState.White -> view.setImageDrawable(stoneDrawableWhite)
                else -> view.setImageDrawable(null)
            }
        }
        gridLayout.invalidate()
    }

    override fun updateCandidatePoints(candidates: List<Reversi.Point>) {

        val boardColor = ContextCompat.getColor(this, R.color.boardColor)
        val boardCandidateColor = ContextCompat.getColor(this, R.color.boardCandidateColor)
        cellViews.flatten().forEach {
            it.setBackgroundColor(boardColor)
        }
        candidates.forEach {
            val view = cellViews[it.x][it.y]
            view.setBackgroundColor(boardCandidateColor)
        }
    }

    override fun showWarning(warning: Reversi.Warnings) {
        val text = when (warning) {
            Reversi.Warnings.CannotPutHare -> "Cannot put hare" // TODO: string.xmlに移す
        }
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun showStoneCounts(blackCount: Int, whiteCount: Int) {
        textViewBlackCount.text = getString(R.string.fmt_text_black_count, blackCount)
        textViewWhiteCount.text = getString(R.string.fmt_text_white_count, whiteCount)
    }

    override fun showCurrentPlayer(player: Reversi.CellState) {
        textViewCurrentPlayer.text = when(player) {
            Reversi.CellState.Black -> getString(R.string.text_current_player_black)
            Reversi.CellState.White -> getString(R.string.text_current_player_white)
            else -> "Invalid..." // TODO: string.xmlに移す
        }
    }

    override fun gameFinish(result: Reversi.Result, blackCount: Int, whiteCount: Int) {
        val text = when(result) {
            Reversi.Result.BlackWin -> getString(R.string.text_black_win)
            Reversi.Result.WhiteWin -> getString(R.string.text_white_win)
            else -> getString(R.string.text_draw)
        }
        textViewCurrentPlayer.text = text

        AlertDialog.Builder(this)
            .setTitle(R.string.text_game_finished)
            .setMessage(text)
            .setPositiveButton(android.R.string.ok){ dialog, _ ->
                dialog.cancel()
            }.show()
        Toast.makeText(this, "$text $blackCount vs $whiteCount", Toast.LENGTH_LONG).show()
    }

    override fun showPassedMessage(player: Reversi.CellState) {
        Toast.makeText(this, getString(R.string.text_message_passed), Toast.LENGTH_SHORT).show()
    }

    override fun beginThinking() {
        textViewThinkingMessage.visibility = View.VISIBLE
    }

    override fun endThinking() {
        textViewThinkingMessage.visibility = View.INVISIBLE
    }
}