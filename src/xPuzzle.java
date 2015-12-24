import mpi.MPI;
import mpi.Status;

import java.util.LinkedList;

/**
 * Created by pascal on 19.11.15.
 */
public class xPuzzle {

    //static final Character[] START_CONFIG = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    static final Character[] START_CONFIG = {0, 1, 2, 3, 4, 5, 6, 7,8, 9, 10, 11,12, 13, 15, 14};
    //static final Character[] END_CONFIG = {1, 2, 3, 4, 5, 6, 7, 8, 0};
    static final Character[] END_CONFIG = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,13, 14, 15, 0};
    static LinkedList<LinkedList<PuzzleState>> ProgramStack = new LinkedList<>();

    private static PuzzleState endConfig = new PuzzleState(END_CONFIG);
    private static PuzzleState start = new PuzzleState(START_CONFIG);
    private static int bound;

    private static final int WORK_REQUEST = 0;


    public static void main(String args[]) throws Exception {
        MPI.Init(args);

        if (MPI.COMM_WORLD.Rank() == 0) {
            PuzzleState.iXLength = 4;
            PuzzleState.iYLength = 4;

            System.out.println("Manhattan Distance Start = " + start.calcManhattanDistance(endConfig));
            long startTime = System.nanoTime();
            bound = start.calcManhattanDistance(endConfig);
            int MAX_BOUND = bound * 10;
            while (bound > 0 && bound < MAX_BOUND) {
                LinkedList<PuzzleState> startList = start.expand(null);
                ProgramStack.push(startList);
                System.out.println("bound: " + bound);
                int minPathLength = Integer.MAX_VALUE;
                do {
                    int PathLength = doWork();
                    if (minPathLength > PathLength) minPathLength = PathLength;
                } while (!isDepthSearched());
                if (bound > 0) bound = minPathLength;
            }

            long stopTime = System.nanoTime();

            System.out.println("Used Time = " + (stopTime - startTime) / 1000000 + "ms");
        }
        MPI.Finalize();
    }

    private static int doWork ()
    {
        int minPathLength = Integer.MAX_VALUE;
        LinkedList<PuzzleState> StateListToExpand = ProgramStack.peek();
        if (StateListToExpand.size() < 2) {
            ProgramStack.pop();
            return minPathLength;
        }
        PuzzleState stateToExpand = StateListToExpand.remove(1);
        LinkedList<PuzzleState> expandStates = stateToExpand.expand(StateListToExpand.getFirst());

        for (int i = expandStates.size() - 1; i >= 1; i--) {
            //don't check parent node again
            PuzzleState candidate = expandStates.get(i);
            int candidateDepth = ProgramStack.size() + 1;
            if (candidate.equals(endConfig)) {
                System.out.println("!!found Solution at depth " + candidateDepth + ":");
                candidate.printState();
                bound = -1;
            } else {
                int candidateSolutionMin = candidateDepth + candidate.calcManhattanDistance(endConfig);
                if (candidateSolutionMin > bound) {
                    if (minPathLength > candidateSolutionMin) {
                        minPathLength = candidateSolutionMin;
                    }
                    expandStates.remove(i);
                }
            }
        }
        ProgramStack.push(expandStates);
        return  minPathLength;
    }

    private static boolean isDepthSearched()
    {
        return ProgramStack.isEmpty();
    }

    private static void serviceMessages()
    {
        Character [] ReceiveBuffer = new Character[1000];
        Status pendingState = MPI.COMM_WORLD.Iprobe(MPI.ANY_SOURCE, WORK_REQUEST);

        if (pendingState.count > 0)
        {
            MPI.COMM_WORLD.Recv(ReceiveBuffer, 0, pendingState.count, MPI.CHAR, pendingState.source, WORK_REQUEST);
        }
    }

    public static LinkedList<LinkedList<PuzzleState>> splitStack (LinkedList<LinkedList<PuzzleState>> stackToSplit)
    {
        boolean unevenToNewStack = false;
        LinkedList<LinkedList<PuzzleState>> newStack = new LinkedList<>();

        for (int i=0; i < stackToSplit.size(); i++)
        {
            LinkedList<PuzzleState> newStackList = new LinkedList<>();
            LinkedList<PuzzleState> oldStackList = stackToSplit.get(i);
            newStackList.add(oldStackList.getFirst());
            int splitAt = 0;
            int childrenCount = oldStackList.size() - 1;
            boolean hasEvenChildCount = ((childrenCount & 1) == 0);

            if (!hasEvenChildCount)
            {
                //uneven children
                if (unevenToNewStack)
                {
                    splitAt = oldStackList.size()/2;
                }
                else
                {
                    splitAt = oldStackList.size()/2 + 1;

                }
                unevenToNewStack = !unevenToNewStack;
            } else {
                //even children
                splitAt = stackToSplit.get(i).size()/2 + 1;
            }
            for (int j = splitAt; j < oldStackList.size() ; j++)
            {
                newStackList.add(oldStackList.get(j));
            }
            for (int j = splitAt; j < oldStackList.size() ; j++)
            {
                oldStackList.removeLast();
            }
            newStack.add(newStackList);
        }

        return newStack;
    }
}

