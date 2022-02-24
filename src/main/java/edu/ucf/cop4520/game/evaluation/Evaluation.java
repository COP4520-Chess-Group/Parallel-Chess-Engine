package edu.ucf.cop4520.game.evaluation;

import edu.ucf.cop4520.game.Board;
import edu.ucf.cop4520.game.Move;

public class Evaluation {

    private double evaluation;
    private Move move;
    private Board board;

    public Evaluation(Board board, Move move) {
        this.move = move;
        this.board = board.copyOf().move(move);
        this.evaluation = this.evaluate();
    }

    private double evaluate() {
        return evaluation;
    }

}
