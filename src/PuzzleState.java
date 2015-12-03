import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by pascal on 26.11.15.
 */
public class PuzzleState
{
    static public int iXLength;
    static public int iYLength;

    public char[] stateArray = null;

    private enum Direction {UP, DOWN, RIGHT, LEFT}

    public void init(char[][] charState)
    {
        stateArray = new char[iXLength * iYLength];
        for (int i = 0; i < iYLength; i++)
        {
            for(int j = 0; j < iXLength; j++)
            {
                stateArray[i * iYLength + j] = charState[i][j];
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
                result[i][j] = stateArray[i * iYLength + j];
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
                System.out.print(stateArray[i * iYLength + j] +0);
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
                if (stateArray[i * iYLength + j] == 0)
                {
                    if (i != 0)
                    {
                        //Down
                        PuzzleState candidate = new PuzzleState();
                        candidate.stateArray = stateArray.clone();
                        candidate.stateArray[i * iYLength + j] = stateArray[(i - 1) * iYLength + j];
                        candidate.stateArray[(i - 1) * iYLength + j] = stateArray[i * iYLength + j];
                        results.add(candidate);
                    }
                    if (i != iYLength - 1)
                    {
                        //Up
                        PuzzleState candidate = new PuzzleState();
                        candidate.stateArray = stateArray.clone();
                        candidate.stateArray[i * iYLength + j] = stateArray[(i + 1) * iYLength + j];
                        candidate.stateArray[(i + 1) * iYLength + j] = stateArray[i * iYLength + j];
                        results.add(candidate);
                    }
                    if (j != 0)
                    {
                        //Right
                        PuzzleState candidate = new PuzzleState();
                        candidate.stateArray = stateArray.clone();
                        candidate.stateArray[i * iYLength + j] = stateArray[i * iYLength + j - 1];
                        candidate.stateArray[i * iYLength + j - 1] = stateArray[i * iYLength + j];
                        results.add(candidate);
                    }
                    if (j != iXLength - 1)
                    {
                        //Left
                        PuzzleState candidate = new PuzzleState();
                        candidate.stateArray = stateArray.clone();
                        candidate.stateArray[i * iYLength + j] = stateArray[i * iYLength + j + 1];
                        candidate.stateArray[i * iYLength + j + 1] = stateArray[i * iYLength + j];
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
        return Arrays.hashCode(stateArray);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PuzzleState))
            return false;
        if (obj == this)
            return true;

        PuzzleState toCompare = (PuzzleState) obj;
        return Arrays.equals(toCompare.stateArray, stateArray);
    }

    @Override
    public String toString()
    {
        String result = new String();
        for (int i = 0; i < iYLength; i++)
        {
            for(int j = 0; j < iXLength; j++)
            {
                result += stateArray[i * iYLength + j] +0;
            }
        }
        return result;
    }
}
