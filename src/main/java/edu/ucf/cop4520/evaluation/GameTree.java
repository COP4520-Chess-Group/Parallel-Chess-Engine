package edu.ucf.cop4520.evaluation;

import edu.ucf.cop4520.game.Board;
import edu.ucf.cop4520.game.Move;
import edu.ucf.cop4520.game.pieces.Piece;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class GameTree {
    private GameTreeNode head;
    private double propEval;

    public GameTree(Board position) {
        this.head = new GameTreeNode(position, null, null);
    }

    public Move findBestMove(int depth) {
        // Find dfs children to depth and return the evaluation
        GameTreeNode current = head;
        int originalDepth = depth;
        while(depth >= 0) {
            synchronized (current) {
                current.findChild();
            }
            current = current.children.first();
            depth--;
        }
        this.propEval = current.evaluation;
        // Continue dfs for other children
        depth = originalDepth;
        while(depth >= 0) {
            // if evaluation after opponent move > first eval, stop and continue
            // if evaluation after opponent move < first eval, continue in this one
                // change eval
            // if evaluation after our move > first eval, continue in this one
                // change eval
            // if evaluation after our move < first eval, stop and continue
        }
        return head.children.first().fromParent;
    }

    public GameTreeNode bestMoveHelper(double eval) {

    }

    // Move head (to avoid having to do more calculations than necessary)

    private class GameTreeNode implements Comparable {
        private GameTreeNode parent;
        private double evaluation;
        private Board board;
        private Move fromParent;
        private TreeSet<GameTreeNode> children;
        private Iterator<Move> iterator;


        protected GameTreeNode(Board position, Move fromParent, GameTreeNode parent) {
            this.parent = parent;
            this.evaluation = position.evaluate();
            this.fromParent = fromParent;
            this.board = position;
            children = new TreeSet<>();
        }

        protected void findChild() {
            if(iterator == null) {
                iterator = board.generateMoves().iterator();
            } else {
                synchronized (iterator) {
                    if(iterator.hasNext()) {
                        Move nextMove = iterator.next();
                        children.add(new GameTreeNode(board.move(nextMove), nextMove, this));
                    } else {
                        return;
                    }
                }
            }
        }

        @Override
        public int compareTo(Object o) {
            if(o instanceof GameTreeNode) {
                if(this.board.getToMove().equals(Piece.Color.LIGHT)) {
                    return this.evaluation > ((GameTreeNode) o).evaluation ? -1 : 1;
                } else {
                    return this.evaluation > ((GameTreeNode) o).evaluation ? 1 : -1;
                }
            }
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof GameTreeNode) {
                return ((GameTreeNode) o).board.equals(this.board);
            }
            return false;
        }
    }
}
