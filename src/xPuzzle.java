import java.util.Stack;

/**
 * Created by pascal on 19.11.15.
 */
public class xPuzzle {

    static final char[][] START_CONFIG = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
    //static final char[][] START_CONFIG = {{0, 1, 2, 3}, {4, 5, 6, 7},{8, 9, 10, 11},{12, 13, 14, 15}};
    static final char[][] END_CONFIG = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
    //static final char[][] END_CONFIG = {{1, 2, 3, 4}, {5, 6, 7, 8},{9, 10, 11, 12},{13, 14, 15, 0}};
    static Stack<PuzzleState> Stack = new Stack<>();

    static PuzzleState endConfig = new PuzzleState();
    static PuzzleState start = new PuzzleState();

    public static void main(String args[]) throws Exception {
        //MPI.Init(args);

        PuzzleState.iXLength = 3;
        PuzzleState.iYLength = 3;

        start.init(START_CONFIG);
        endConfig.init(END_CONFIG);

        int bound = 23; //start.calcManhattanDistance(endConfig);
        int MAX_BOUND = bound * 10;
        int depth = 0;
        while (bound > 0 && bound < MAX_BOUND)
        {
            System.out.println("bound: " + bound);
            bound = expandState(start, depth, bound);
        }

        //System.out.print("Manhattan Distance = ");
        //System.out.println(calcManhattanDistance(START_CONFIG, END_CONFIG));
        //Stack.push(START_CONFIG);
        //MPI.Finalize();
    }


    private static int expandState(PuzzleState xState, int depth, int maxDepth) {
        int minPathLength = Integer.MAX_VALUE;
        PuzzleState[] states = xState.expand();
        for (int i = 0; i < states.length; i++)
        {
            if (!Stack.empty() && states[i].equals(Stack.peek()))
            {
                //do nothing
            }
            else if (states[i].equals(endConfig))
            {
                System.out.println("!!found Solution at depth " + depth + ":");
                states[i].printState();
                return -1;
            } else {
                if ((depth + xState.calcManhattanDistance(endConfig) <= maxDepth) && (minPathLength > 0)) {
                    depth++;
                    Stack.push(xState);
                    int PathLength = expandState(states[i], depth, maxDepth);
                    if (PathLength < minPathLength) minPathLength = PathLength;
                    Stack.pop();
                    depth--;
                }
            }
        }
        if (minPathLength == -1)
        {
            System.out.println("Step: " + depth);
            xState.printState();
        }
        return minPathLength;
    }
}

