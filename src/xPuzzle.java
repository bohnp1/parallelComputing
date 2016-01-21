import mpi.MPI;
import mpi.Request;
import mpi.Status;

import java.util.LinkedList;

/**
 * Created by pascal on 19.11.15.
 */
public class xPuzzle {

    /*private enum STATE
    {
        ServiceMessageWithWork,
        ServiceMessageWithoutWork,
        DoWork,
        RequestWork
    }*/

/*
    static final Character[] START_CONFIG = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    static final Character[] START_CONFIG = {0, 1, 2, 3, 4, 5, 6, 7,8, 9, 10, 11,12, 13, 15, 14};
    static final Character[] END_CONFIG = {1, 2, 3, 4, 5, 6, 7, 8, 0};
    static final Character[] END_CONFIG = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,13, 14, 15, 0};
*/
    static PuzzleStack ProgramStack;

    private static PuzzleState endState;
    private static PuzzleState startState;
    private static int bound;
    private static int maxBound = 60;
    private static int minPathLength = Integer.MAX_VALUE;
    private static boolean finished = false;
    private static int [] states; // 0: has work, 1: need Work 2: finished
    //private static STATE actualState = STATE.RequestWork;


    public static void generatePuzzle(int x, int y)
    {
        PuzzleState.iXLength = x;
        PuzzleState.iYLength = y;

        Character [] startConfig = new Character[x*y];
        Character [] endConfig = new Character[x*y];

        for (Character i=0; i<(x*y); i++)
        {
            startConfig[i] = i;
            if (i != (x*y - 1))
                endConfig[i] = (char)(i + 1);
            else
                endConfig[i] = 0;
        }

        if ((x%2 == 0) && (y%2 == 0))
        {
            startConfig[(x*y - 2)] = (char)(x*y - 1);
            startConfig[(x*y - 1)] = (char)(x*y - 2);
        }

        startState = new PuzzleState(startConfig);
        endState = new PuzzleState(endConfig);

    }


    public static void main(String args[]) throws Exception {
        MPI.Init(args);

        generatePuzzle(4, 4);

        if (MPI.COMM_WORLD.Rank() == 0)
        {

            long startTime = System.nanoTime();
            log("Manhattan Distance Start = " + startState.calcManhattanDistance(endState));
            bound = startState.calcManhattanDistance(endState);
            states = new int[MPI.COMM_WORLD.Size() + 1];

            Message messages[] = new Message[1];
            Request request = MPI.COMM_WORLD.Irecv(messages, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, MPI.ANY_TAG);

            while (bound > 0 && bound < maxBound) {
                boolean BoundFinished = false;
                PuzzleStackElement startList = startState.expand(null);
                ProgramStack = new PuzzleStack();
                ProgramStack.push(startList);
                log("bound: " + bound);



                while (!BoundFinished) {
                    if (ProgramStack != null && states[1] == 1) {
                        messages[0] = new Message();
                        messages[0].messageType = Message.MESSAGE_WORK_ANSWER;
                        messages[0].bound = bound;
                        messages[0].stack = ProgramStack;
                        log("Send work to: 1");
                        MPI.COMM_WORLD.Send(messages, 0, 1, MPI.OBJECT, 1, Message.MESSAGE_WORK);
                        states[1] = 0;
                        ProgramStack = null;
                    }
                    Status state = request.Test();
                    if (state != null) {
                        request = MPI.COMM_WORLD.Irecv(messages, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, MPI.ANY_TAG);
                        int dest = state.source;
                        if (messages[0].messageType == Message.MESSAGE_SPLIT_CHECK) {
                            if (minPathLength < 0)
                            {
                                messages[0].messageType = Message.MESSAGE_SPLIT_END;
                                messages[0].bound = 0;
                                messages[0].stack = null;
                                log ("send split end to: " + dest);
                                MPI.COMM_WORLD.Send(messages, 0, 1, MPI.OBJECT, dest, Message.MESSAGE_SPLIT);
                                states[dest] = 2;           //set State as work requested to be able to finish
                            }
                            else {
                                boolean sendSplitRequest = false;
                                for (int i = 1; i < MPI.COMM_WORLD.Size(); i++) {
                                    if (states[i] == 1) {
                                        messages[0].messageType = Message.MESSAGE_SPLIT_YES;
                                        messages[0].bound = 0;
                                        messages[0].stack = null;
                                        log("Send split request to: " + dest);
                                        MPI.COMM_WORLD.Send(messages, 0, 1, MPI.OBJECT, dest, Message.MESSAGE_SPLIT);
                                        sendSplitRequest = true;
                                        break;
                                    }
                                }
                                if (!sendSplitRequest) {
                                    messages[0].messageType = Message.MESSAGE_SPLIT_NO;
                                    messages[0].bound = 0;
                                    messages[0].stack = null;
                                    MPI.COMM_WORLD.Send(messages, 0, 1, MPI.OBJECT, dest, Message.MESSAGE_SPLIT);
                                }
                            }
                        } else if (messages[0].messageType == Message.MESSAGE_WORK_REQUEST) {

                            if (minPathLength < 0)
                            {
                                messages[0].messageType = Message.MESSAGE_WORK_END;
                                messages[0].bound = 0;
                                messages[0].stack = null;
                                log("send work end to 1 :" + dest);
                                MPI.COMM_WORLD.Send(messages, 0, 1, MPI.OBJECT, dest, Message.MESSAGE_WORK);
                                states[dest] = 2;

                            }
                            log("Received Request from: " + dest);
                            if (minPathLength > messages[0].bound) {
                                minPathLength = messages[0].bound;
                                log("Set bound to: " + minPathLength);
                                if (minPathLength < 0)
                                {
                                    messages[0].messageType = Message.MESSAGE_WORK_END;
                                    messages[0].bound = 0;
                                    messages[0].stack = null;
                                    log("send work end to 2 :" + dest);
                                    MPI.COMM_WORLD.Send(messages, 0, 1, MPI.OBJECT, dest, Message.MESSAGE_WORK);
                                    states[dest] = 2;

                                    for (int i = 1; i <= MPI.COMM_WORLD.Size(); i++) {
                                        if (states[i] == 1) {
                                            messages[0].messageType = Message.MESSAGE_WORK_END;
                                            messages[0].bound = 0;
                                            messages[0].stack = null;
                                            log("send work end to 3 :" + i);
                                            MPI.COMM_WORLD.Send(messages, 0, 1, MPI.OBJECT, i, Message.MESSAGE_WORK);
                                            states[i] = 2;
                                        }
                                    }
                                }
                            }
                            if (ProgramStack != null) {
                                messages[0].messageType = Message.MESSAGE_WORK_ANSWER;
                                messages[0].bound = bound;
                                messages[0].stack = ProgramStack;
                                log("Send work to :" + dest);
                                MPI.COMM_WORLD.Send(messages, 0, 1, MPI.OBJECT, dest, Message.MESSAGE_WORK);
                                states[dest] = 0;
                                ProgramStack = null;
                            } else {
                                states[dest] = 1;
                            }
                        } else if (messages[0].messageType == Message.MESSAGE_SPLIT_ANWSER) {
                            messages[0].messageType = Message.MESSAGE_WORK_ANSWER;
                            for (int i = 1; i < MPI.COMM_WORLD.Size(); i++) {
                                if (states[i] == 1) {
                                    log("Send splited work to :" + i + " from " + dest);
                                    MPI.COMM_WORLD.Send(messages, 0, 1, MPI.OBJECT, i, Message.MESSAGE_WORK);
                                    states[i] = 0;
                                    break;
                                }
                            }

                        } else if (messages[0].messageType == Message.MESSAGE_SPLIT_REJECT) {
                            log("Split rejected by:" + dest);
                        }

                        BoundFinished = true;
                        for (int i = 1; i < MPI.COMM_WORLD.Size(); i++) {
                            if (states[i] == 0) {
                                BoundFinished = false;
                                break;
                            }
                        }
                    }
                }
                bound = minPathLength;
                minPathLength = Integer.MAX_VALUE;
            }
            long stopTime = System.nanoTime();

            System.out.println("Used Time = " + (stopTime - startTime) / 1000000 + "ms");
        }
        else
        {
            Worker();
        }
        MPI.Finalize();
    }

    private static void doWork ()
    {
        PuzzleStackElement StateListToExpand = ProgramStack.peek();
        if (StateListToExpand.size() < 2) {
            ProgramStack.pop();
            return;
        }
        PuzzleState stateToExpand = StateListToExpand.remove(1);
        PuzzleStackElement expandStates = stateToExpand.expand(StateListToExpand.getFirst());

        for (int i = expandStates.size() - 1; i >= 1; i--) {
            //don't check parent node again
            PuzzleState candidate = expandStates.get(i);
            int candidateDepth = candidate.getDepth();
            if (candidate.equals(endState)) {
                log("!!found Solution at depth " + candidateDepth + ":");
                candidate.printState();
                bound = -1;
                minPathLength = -1;
            } else {
                int candidateSolutionMin = candidateDepth + candidate.calcManhattanDistance(endState);
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
        ProgramStack.push(expandStates);
    }

    private static boolean hasWork()
    {
        return (ProgramStack != null) && !ProgramStack.isEmpty() /*&& bound > 0*/;
    }

    private static void Worker()
    {
        int counter = 0;
        while (!finished)
        {
            if (hasWork() && counter++ < 100000)
            {
                doWork();
            }
            else
            {
                counter = 0;
                if (hasWork())
                {
                    checkForSplit();
                }
                else
                {
                    requestWork();
                }
            }
        }
    }

    private static void checkForSplit()
    {
        Message messages [] = {new Message()};
        messages[0].bound = 0;
        messages[0].messageType = Message.MESSAGE_SPLIT_CHECK;
        messages[0].stack = null;
        MPI.COMM_WORLD.Send(messages, 0, 1, MPI.OBJECT, 0, Message.MESSAGE_SPLIT);
        MPI.COMM_WORLD.Recv(messages, 0, 1, MPI.OBJECT, 0, Message.MESSAGE_SPLIT);

        if (messages[0].messageType == Message.MESSAGE_SPLIT_END)
        {
            finished = true;
            log ("received split end");
            return;
        }
        else if (messages[0].messageType == Message.MESSAGE_SPLIT_YES)
        {
            if (hasWork())
            {
                messages[0].stack = splitStack(ProgramStack);
                messages[0].bound = bound;
                messages[0].messageType = Message.MESSAGE_SPLIT_ANWSER;
            }
            else
            {
                messages[0].stack = null;
                messages[0].bound = 0;
                messages[0].messageType = Message.MESSAGE_SPLIT_REJECT;
            }
            MPI.COMM_WORLD.Send(messages, 0, 1, MPI.OBJECT, 0, Message.MESSAGE_SPLIT);
            return;
        }
        else if (messages[0].messageType == Message.MESSAGE_SPLIT_NO)
        {
            //return to work
            return;
        }
        else
        {
            log("Received not expected MessageType: " + messages[0].messageType);
        }
    }

    private static void requestWork()
    {
        Message messages [] = {new Message()};
        messages[0].bound = minPathLength;
        messages[0].messageType = Message.MESSAGE_WORK_REQUEST;
        messages[0].stack = null;
        MPI.COMM_WORLD.Send(messages, 0, 1, MPI.OBJECT, 0, Message.MESSAGE_WORK);
        log("Request for Work");
        MPI.COMM_WORLD.Recv(messages, 0, 1, MPI.OBJECT, 0, Message.MESSAGE_WORK);

        //log("Received Work Message");

        if (messages[0].messageType == Message.MESSAGE_WORK_END)
        {
            finished = true;
            log("received work end");
        }
        else if (messages[0].messageType == Message.MESSAGE_WORK_ANSWER)
        {
            minPathLength = Integer.MAX_VALUE;
            ProgramStack = messages[0].stack;
            bound = messages[0].bound;
            log("Received program stack with size: " + ProgramStack.size() + " with a bound: " + bound);
        }
        else
        {
            log("Received not expected MessageType: " + messages[0].messageType);
        }
    }

    public static PuzzleStack splitStack (PuzzleStack stackToSplit)
    {
        boolean unevenToNewStack = false;
        PuzzleStack newStack = new PuzzleStack();

        for (int i=0; i < stackToSplit.size(); i++)
        {
            PuzzleStackElement newStackList = new PuzzleStackElement();
            PuzzleStackElement oldStackList = stackToSplit.get(i);
            newStackList.add(oldStackList.getFirst());
            int splitAt;
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

    private static void log (String toLog)
    {
        System.out.println(MPI.COMM_WORLD.Rank() + ": " + toLog );
    }

    public static PuzzleState getEndConfig() {
        return endState;
    }

    public static PuzzleState getStart() {
        return startState;
    }
}


