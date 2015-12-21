import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by pascal on 19.11.15.
 */
public class xPuzzle {

    //static final char[][] START_CONFIG = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
    static final char[][] START_CONFIG = {{0, 1, 2, 3}, {4, 5, 6, 7},{8, 9, 10, 11},{12, 13, 15, 14}};
    //static final char[][] END_CONFIG = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
    static final char[][] END_CONFIG = {{1, 2, 3, 4}, {5, 6, 7, 8},{9, 10, 11, 12},{13, 14, 15, 0}};
    static Stack<LinkedList<PuzzleState>> ProgrammStack = new Stack<>();

    static PuzzleState endConfig = new PuzzleState();
    static PuzzleState start = new PuzzleState();

    public static void main(String args[]) throws Exception {
        //MPI.Init(args);

        PuzzleState.iXLength = 4;
        PuzzleState.iYLength = 4;

        start.init(START_CONFIG);
        endConfig.init(END_CONFIG);

        System.out.println("Manhattan Distance Start = " + start.calcManhattanDistance(endConfig));
        long startTime = System.nanoTime();
        int bound = start.calcManhattanDistance(endConfig);
        int MAX_BOUND = bound * 10;
        while (bound > 0 && bound < MAX_BOUND)
        {
            int minPathLength = Integer.MAX_VALUE;
            LinkedList<PuzzleState> startList = start.expand(null);
            ProgrammStack.push(startList);
            System.out.println("bound: " + bound);
            do {
                LinkedList<PuzzleState> StateListToExpand = ProgrammStack.peek();
                if (StateListToExpand.size() < 2)
                {
                    ProgrammStack.pop();
                    continue;
                }
                PuzzleState stateToExpand = StateListToExpand.remove(1);
                LinkedList<PuzzleState> expandStates = stateToExpand.expand(StateListToExpand.getFirst());

                for (int i = expandStates.size() - 1; i >= 1 ; i--)
                {
                    //don't check parent node again
                    PuzzleState candidate = expandStates.get(i);
                    int candidateDepth = ProgrammStack.size() + 1;
                    if (candidate.equals(endConfig))
                    {
                        System.out.println("!!found Solution at depth " + candidateDepth + ":");
                        candidate.printState();
                        bound = -1;
                    }
                    else
                    {
                        int candidateSolutionMin = candidateDepth + candidate.calcManhattanDistance(endConfig);
                        if (candidateSolutionMin > bound)
                        {
                            if (minPathLength > candidateSolutionMin)
                            {
                                minPathLength = candidateSolutionMin;
                            }
                            expandStates.remove(i);
                        }
                    }
                }
                ProgrammStack.push(expandStates);
            } while (!ProgrammStack.isEmpty());
            if (bound > 0) bound = minPathLength;
        }

        long stopTime = System.nanoTime();

        System.out.println("Used Time = " + (stopTime - startTime) / 1000000 +"ms");

        //MPI.Finalize();
    }
}

