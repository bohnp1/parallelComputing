import java.util.ArrayList;

/**
 * Created by pascal on 26.11.15.
 */
public class PuzzleState
{
    static public int iXLength;
    static public int iYLength;

    public long lState;

    private enum Direction {UP, DOWN, RIGHT, LEFT}

    public void init(char[][] charState)
    {
        for (int i = 0; i < iYLength; i++)
        {
            for(int j = 0; j < iXLength; j++)
            {
                int base = iXLength * iYLength;
                lState += Math.pow(base,i * iYLength + j) * charState[i][j];
            }
        }

    }

    public char[][] convertToCharArray()
    {
        char[][] result = new char[iYLength][iXLength];

        for (int i = 0; i < iYLength; i++)
        {
            for(int j = 0; j < iXLength; j++)
            {
                int base = iXLength * iYLength;
                result[i][j] = (char) (lState / Math.pow(base,i * iYLength + j) % base);
            }
        }
        return result;
    }

    public void printState()
    {
        for (int i = 0; i < iYLength; i++)
        {
            for(int j = 0; j < iXLength; j++)
            {
                int base = iXLength * iYLength;
                System.out.print((char)(lState / Math.pow(base,i * iYLength + j) % base) +0);
            }
            System.out.println("");
        }
        System.out.println("-------");
    }

    public int calcManhattanDistance (PuzzleState sEndconfig)
    {
        int ManhattanDistance = 0;
        for (int i=0; i < iYLength; i++)
        {
            for (int j=0; j < iXLength; j++)
            {
                //if (xState[i] == endConfig[j])
                //{
                //    ManhattanDistance += Math.abs((i / PuzzleState.iYLength) - (j / PuzzleState.iYLength));
                //    ManhattanDistance += Math.abs((i % PuzzleState.iXLength) - (j % PuzzleState.iXLength));
                //    break;
                //}
            }
        }
        return ManhattanDistance;
    }

    public PuzzleState[] expand ()
    {
        int base = iXLength * iYLength;

        ArrayList<PuzzleState> results = new ArrayList<>();
        for (int i = 0; i < iYLength; i++)
        {
            for(int j = 0; j < iXLength; j++)
            {
                if ((char)(lState / Math.pow(base,i * iYLength + j) % base) == 0)
                {
                    double baseToSwap = Math.pow(base,i * iYLength + j);
                    double baseFromSwap;

                    if (i != 0)
                    {
                        //Down
                        baseFromSwap = Math.pow(base,(i - 1) * iYLength + j);
                        char toSwap = (char) (lState/baseFromSwap % base);
                        PuzzleState candidate = new PuzzleState();
                        candidate.lState = (long) (lState - toSwap * baseFromSwap + toSwap * baseToSwap);
                        results.add(candidate);
                    }
                    if (i != iYLength - 1)
                    {
                        //Up
                        baseFromSwap = Math.pow(base,(i + 1) * iYLength + j);
                        char toSwap = (char) (lState/baseFromSwap % base);
                        PuzzleState candidate = new PuzzleState();
                        candidate.lState = (long) (lState - toSwap * baseFromSwap + toSwap * baseToSwap);
                        results.add(candidate);
                    }
                    if (j != 0)
                    {
                        //Right
                        baseFromSwap = Math.pow(base,i * iYLength + j - 1);
                        char toSwap = (char) ((lState/baseFromSwap) % base);
                        PuzzleState candidate = new PuzzleState();
                        candidate.lState = (long) (lState - toSwap * baseFromSwap + toSwap * baseToSwap);
                        results.add(candidate);
                    }
                    if (j != iXLength - 1)
                    {
                        //Left
                        baseFromSwap = Math.pow(base, i * iYLength + j + 1);
                        char toSwap = (char) (lState / baseFromSwap % base);
                        PuzzleState candidate = new PuzzleState();
                        candidate.lState = (long) (lState - toSwap * baseFromSwap + toSwap * baseToSwap);
                        results.add(candidate);
                    }
                }
            }
        }

        PuzzleState[] resultArray = new PuzzleState[results.size()];
        results.toArray(resultArray);
        return resultArray;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(lState).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PuzzleState))
            return false;
        if (obj == this)
            return true;

        PuzzleState toCompare = (PuzzleState) obj;
        return this.lState == toCompare.lState;
    }

    @Override
    public String toString()
    {
        String result = new String();
        for (int i = 0; i < iYLength; i++)
        {
            for(int j = 0; j < iXLength; j++)
            {

                int base = iXLength * iYLength;
                result += ((char)(lState / Math.pow(base,i * iYLength + j) % base) +0);
            }
        }
        return result;
    }
}
