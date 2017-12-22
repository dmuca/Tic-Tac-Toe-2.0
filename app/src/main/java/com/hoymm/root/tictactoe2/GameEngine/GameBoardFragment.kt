package com.hoymm.root.tictactoe2.GameEngine

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.GridView
import android.widget.LinearLayout
import com.hoymm.root.tictactoe2.R
import java.lang.ClassCastException

class GameBoardFragment : Fragment() {
    private lateinit var gameBoardGridView : GridView
    private var boardSize: BoardSize? = null
    private var fieldLength : Int = 0
    private var fieldsSeparatorLength: Int = 0
    private lateinit var currentAppDataInfo : CurrentAppDataInfo

    private val howManyFieldsInRow: Int get() =
       when (boardSize) {
            BoardSize.board3x3 -> 3
            BoardSize.board5x5 -> 5
            BoardSize.board7x7 -> 7
            else -> 3
        }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try{
            currentAppDataInfo = context as CurrentAppDataInfo
        }
        catch (exception : ClassCastException){
            throw ClassCastException(activity.toString() + " must implement CurrentAppDataInfo");
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater!!.inflate(R.layout.game_board_fragment, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        boardSize = arguments.get(GameEngine.GAME_BOARD_SIZE_KEY) as BoardSize

        view!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                getView()!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                generateGameBoard()
            }
        })
    }

    private fun generateGameBoard() {
        gameBoardGridView = activity.findViewById<GridView>(R.id.game_board_fragment_id)
        calculateFieldAndSeparatorLength()
        setNewWidthAndHeightForGameBoardView(gameBoardGridView)
        setAdapter(gameBoardGridView)
        setNumOfColumnsAndSpacingGridViewShouldHave(gameBoardGridView)
    }

    private fun calculateFieldAndSeparatorLength() {
        val parentLayoutShorterSideLength =  Math.min(getParentLinearLayout().height, getParentLinearLayout().width)
        fieldsSeparatorLength = parentLayoutShorterSideLength/((Math.sqrt(howManyFieldsInRow.toDouble())*50).toInt())
        fieldLength = getTotalLengthOfFieldsInRow(parentLayoutShorterSideLength)/howManyFieldsInRow
    }

    private fun setNewWidthAndHeightForGameBoardView(gameBoardGridView: GridView) {
        val gameBoardSideLength = getTotalLengthOfSeparatorsInRow() + fieldLength * howManyFieldsInRow
        gameBoardGridView.layoutParams.width = gameBoardSideLength
        gameBoardGridView.layoutParams.height = gameBoardSideLength
    }

    private fun setAdapter(gameBoardLayout: GridView) {
        gameBoardLayout.adapter = GameBoardAdapter(context, howManyFieldsInRow, fieldLength)
    }

    private fun setNumOfColumnsAndSpacingGridViewShouldHave(gameBoardLayout: GridView) {
        gameBoardLayout.numColumns = howManyFieldsInRow
        gameBoardLayout.horizontalSpacing = fieldsSeparatorLength
        gameBoardLayout.verticalSpacing = fieldsSeparatorLength
    }

    private fun getParentLinearLayout() =
            activity.findViewById<LinearLayout>(R.id.game_board_background_layout)

    private fun getTotalLengthOfFieldsInRow(shorterParentSideLength: Int) =
            shorterParentSideLength - getTotalLengthOfSeparatorsInRow()

    private fun getTotalLengthOfSeparatorsInRow() =
            howManyFieldSeparatorsInRow() * fieldsSeparatorLength

    private fun howManyFieldSeparatorsInRow() = (howManyFieldsInRow - 1)

    fun checkIfSomebodyWon(): Shape? {
        val howManyWins = if (boardSize == BoardSize.board3x3) 3 else 4
        var whoWon: Shape?

        whoWon = checkIfAnyWonInRows(howManyWins)
        if (whoWon != null) return whoWon

        whoWon = checkIfAnyWonInColumns(howManyWins)
        if (whoWon != null) return whoWon

        whoWon = checkIfAnyWonInDiagonal(howManyWins)
        if (whoWon != null) return whoWon

        return null
    }

    enum class Table {
        Column, Row
    }

    private fun checkIfAnyWonInRows(howManyPointsWin: Int): Shape? = checkIfAnyRowOrColumnWins(howManyPointsWin, Table.Row)

    private fun checkIfAnyWonInColumns(howManyPointsWin : Int): Shape? = checkIfAnyRowOrColumnWins(howManyPointsWin, Table.Column)

    private fun checkIfAnyRowOrColumnWins(howManyPointsWin: Int, checkingDirection : Table): Shape? {
        for (row in 0 until gameBoardGridView.numColumns) {
            var lastShape: Shape? = null
            var howManyPoints = 0
            for (column in 0 until gameBoardGridView.numColumns) {
                val curField = when (checkingDirection){
                    Table.Column -> gameBoardGridView.getChildAt(row + column*gameBoardGridView.numColumns) as GameField
                    Table.Row -> gameBoardGridView.getChildAt(row*gameBoardGridView.numColumns + column) as GameField
                }
                val curShape = curField.whatShape()

                howManyPoints = when {
                    curShape == null -> 0
                    lastShape != curShape -> 1
                    else -> howManyPoints + 1
                }
                lastShape = curShape

                if (howManyPoints == howManyPointsWin)
                    return curShape
            }
        }
        return null
    }

    private fun checkIfAnyWonInDiagonal(howManyWins : Int): Shape? {
        return null // TODO
    }
}
