package jp.taty.reversi.model

import jp.taty.reversi.Reversi

class ReversiCondition {

    /** 盤面のセルの一覧 */
    val cells: List<List<Reversi.Cell>> =
        arrayOfNulls<List<Reversi.Cell>>(Reversi.BOARD_SIZE).mapIndexed { x, _ ->
            arrayOfNulls<Reversi.Cell>(Reversi.BOARD_SIZE).mapIndexed { y, _ ->
                Reversi.Cell(Reversi.CellState.None, x, y)
            }
        }

    var currentPlayer: Reversi.CellState = Reversi.CellState.Black
        private set

    /** 黒石が置かれているセルの一覧 */
    var blackCells: List<Reversi.Cell> = listOf()
        get() {
            updateCacheIfNecessary()
            return field
        }
        private set

    /** 白石が置かれているセルの一覧 */
    var whiteCells: List<Reversi.Cell> = listOf()
        get() {
            updateCacheIfNecessary()
            return field
        }
        private set

    /** 石が置かれていないセルの一覧 */
    var blankCells: List<Reversi.Cell> = listOf()
        get() {
            updateCacheIfNecessary()
            return field
        }
        private set

    private var isDirty = false

    init {
        val standardPoint = (Reversi.BOARD_SIZE / 2) - 1
        cells[standardPoint][standardPoint].state = Reversi.CellState.White
        cells[standardPoint + 1][standardPoint + 1].state = Reversi.CellState.White
        cells[standardPoint + 1][standardPoint].state = Reversi.CellState.Black
        cells[standardPoint][standardPoint + 1].state = Reversi.CellState.Black

        isDirty = true
        updateCacheIfNecessary()
    }

    /**
     * ターンを進める
     */
    fun changeTurn() {
        currentPlayer = nextPlayer()
    }

    fun nextPlayer(): Reversi.CellState {
        return anotherPlayer(currentPlayer)
    }

    fun anotherPlayer(player: Reversi.CellState): Reversi.CellState {
        return if (player == Reversi.CellState.Black)
            Reversi.CellState.White else Reversi.CellState.Black
    }

    fun getResult(): Reversi.Result {
        return when {
            blackCells.size == whiteCells.size -> Reversi.Result.Draw
            blackCells.size > whiteCells.size -> Reversi.Result.BlackWin
            else -> Reversi.Result.WhiteWin
        }
    }

    /**
     * 特定のセルに石を置き、リバーシのルールに従って相手の石をひっくり返す
     *
     * @param player 石を置くプレイヤー [Reversi.CellState.Black]または[Reversi.CellState.White]
     * @param x 石を置くセルのx座標
     * @param y 石を置くセルのy座標
     */
    fun putToCell(player: Reversi.CellState, x: Int, y: Int) {
        val cell = cells[x][y]

        cell.state = player
        reverseCellsOnLine(player, cell.linePointsLeft)
        reverseCellsOnLine(player, cell.linePointsUp)
        reverseCellsOnLine(player, cell.linePointsRight)
        reverseCellsOnLine(player, cell.linePointsDown)

        reverseCellsOnLine(player, cell.linePointsUpLeft)
        reverseCellsOnLine(player, cell.linePointsUpRight)
        reverseCellsOnLine(player, cell.linePointsDownRight)
        reverseCellsOnLine(player, cell.linePointsDownLeft)

        updateCacheIfNecessary()
    }

    /**
     * 指定されたライン上にある石をひっくり返す
     * @param player 石を置いたプレイヤー[Reversi.CellState.Black]または[Reversi.CellState.White]
     * @param linePoints 指定線の座標の一覧
     */
    private fun reverseCellsOnLine(player: Reversi.CellState, linePoints: List<Reversi.Point>) {
        if (isCandidateCell(player, linePoints)) {
            val enemy = cells[linePoints[0].x][linePoints[0].y].state // 隣のマスは必ず相手の石
            for (point in linePoints) {
                if (cells[point.x][point.y].state == enemy) {
                    cells[point.x][point.y].state = player
                    isDirty = true
                } else {
                    break;
                }
            }
        }
    }

    /** blackCells, whiteCells, BlankCellsを必要に応じて生成しなおす */
    private fun updateCacheIfNecessary() {
        if (isDirty) {
            isDirty = false;
            blackCells = cells.flatten().filter { it.state == Reversi.CellState.Black }
            whiteCells = cells.flatten().filter { it.state == Reversi.CellState.White }
            blankCells = cells.flatten().filter { it.state == Reversi.CellState.None }
        }
    }

    /***
     * 指定されたプレイヤーが次に石を置ける場所のリストを作成する
     */
    fun getCandidateCells(player: Reversi.CellState): List<Reversi.Cell>{
        // 本来指定されるはずがないが、Noneが指定された場合は候補なしで返す
        if(player == Reversi.CellState.None) return listOf()
        return blankCells.filter {
            isCandidateCells(player, it.x, it.y)
        }
    }

    /**
     * 指定座標が指定されたプレイヤーにとって石を置ける場所であるかチェックを行う
     *
     * @param player チェック対象のプレイヤー[Reversi.CellState.Black]または[Reversi.CellState.White]
     * @param x 指定座標のx座標
     * @param y 指定座標のy座標
     */
    fun isCandidateCells(player: Reversi.CellState, x: Int, y: Int): Boolean {
        if(player != Reversi.CellState.Black && player != Reversi.CellState.White) return false
        val cell = cells[x][y]
        return cell.state == Reversi.CellState.None && (
                isCandidateCell(player, cell.linePointsLeft) ||
                        isCandidateCell(player, cell.linePointsUp) ||
                        isCandidateCell(player, cell.linePointsRight) ||
                        isCandidateCell(player, cell.linePointsDown) ||
                        isCandidateCell(player, cell.linePointsUpLeft) ||
                        isCandidateCell(player, cell.linePointsUpRight) ||
                        isCandidateCell(player, cell.linePointsDownRight) ||
                        isCandidateCell(player, cell.linePointsDownLeft))
    }

    /**
     * 指定された線において、隣が相手の石であるか、その先に自分の石があるかをチェックすることで
     * 石を置けるかどうかを判定する
     *
     * @param player チェック対象のプレイヤー[Reversi.CellState.Black]または[Reversi.CellState.White]
     * @param linePoints 指定線の座標の一覧
     */
    fun isCandidateCell(player: Reversi.CellState, linePoints: List<Reversi.Point>): Boolean {
        if(player != Reversi.CellState.Black && player != Reversi.CellState.White) return false

        val enemy = anotherPlayer(player)
        if(linePoints.size > 1 && cells[linePoints[0].x][linePoints[0].y].state == enemy){
            for (p in linePoints) {
                val c = cells[p.x][p.y]
                when(c.state){
                    enemy -> { } // Nothing to do.
                    player -> return true
                    Reversi.CellState.None -> return false
                    else -> return false
                }
            }
        }
        return false;
    }
}