import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by pascal on 26.11.15.
 */
public class PuzzleState
{
    static public int iXLength;
    static public int iYLength;


    public ArrayList<Character> stateArray = null;

    private enum Direction {UP, DOWN, RIGHT, LEFT}

    public PuzzleState ()
    {
    }

    public PuzzleState (Character[] charState)
    {
        stateArray = new ArrayList<>(Arrays.asList(charState));
    }

    public PuzzleState (ArrayList<Character> charState)
    {
        stateArray = charState;
    }

//    public Character[][] convertToCharArray()
//    {
//        Character[][] result = new Character[iYLength][iXLength];
//
//        for (int i = 0; i < iYLength; i++)
//        {
//            for(int j = 0; j < iXLength; j++)
//            {
//                result[i][j] = stateArray[i * iYLength + j];
//            }
//        }
//        return result;
//    }

    public void printState()
    {
        for (int i = 0; i < iYLength; i++)
        {
            for(int j = 0; j < iXLength; j++)
            {
                System.out.print(stateArray.get(i * iYLength + j)+0);
            }
            System.out.println("");
        }
        System.out.println("-------");
    }

    public int calcManhattanDistance (PuzzleState sEndconfig)
    {
        int ManhattanDistance = 0;
        for (int i=0; i < stateArray.size(); i++)
        {
            for (int j=0; j < stateArray.size(); j++)
            {
                if (stateArray.get(i) == sEndconfig.stateArray.get(j) && stateArray.get(i) != 0)
                {
                    ManhattanDistance += Math.abs((i / PuzzleState.iYLength) - (j / PuzzleState.iYLength));
                    ManhattanDistance += Math.abs((i % PuzzleState.iXLength) - (j % PuzzleState.iXLength));
                    break;
                }
            }
        }
        return ManhattanDistance;
    }

    public LinkedList<PuzzleState> expand (PuzzleState stateToAvoid)
    {
        LinkedList<PuzzleState> results = new LinkedList<>();
        results.add(this);
        for (int i = 0; i < iYLength; i++)
        {
            for(int j = 0; j < iXLength; j++)
            {
                if (stateArray.get(i * iYLength + j) == 0)
                {
                    if (i != 0)
                    {
                        //Down
                        PuzzleState candidate = new PuzzleState();
                        candidate.stateArray = (ArrayList<Character>)stateArray.clone();
                        candidate.stateArray.set(i * iYLength + j , stateArray.get((i - 1) * iYLength + j));
                        candidate.stateArray.set((i - 1) * iYLength + j, stateArray.get(i * iYLength + j));
                        if (!candidate.equals(stateToAvoid)) results.add(candidate);
                    }
                    if (i != iYLength - 1)
                    {
                        //Up
                        PuzzleState candidate = new PuzzleState();
                        candidate.stateArray = (ArrayList<Character>)stateArray.clone();
                        candidate.stateArray.set((i * iYLength + j), stateArray.get((i + 1) * iYLength + j));
                        candidate.stateArray.set((i + 1) * iYLength + j ,stateArray.get(i * iYLength + j));
                        if (!candidate.equals(stateToAvoid)) results.add(candidate);
                    }
                    if (j != 0)
                    {
                        //Right
                        PuzzleState candidate = new PuzzleState();
                        candidate.stateArray = (ArrayList<Character>)stateArray.clone();
                        candidate.stateArray.set((i * iYLength + j) , stateArray.get(i * iYLength + j - 1));
                        candidate.stateArray.set((i * iYLength + j - 1), stateArray.get(i * iYLength + j));
                        if (!candidate.equals(stateToAvoid)) results.add(candidate);
                    }
                    if (j != iXLength - 1)
                    {
                        //Left
                        PuzzleState candidate = new PuzzleState();
                        candidate.stateArray = (ArrayList<Character>)stateArray.clone();
                        candidate.stateArray.set(i * iYLength + j , stateArray.get(i * iYLength + j + 1));
                        candidate.stateArray.set(i * iYLength + j + 1, stateArray.get(i * iYLength + j));
                        if (!candidate.equals(stateToAvoid)) results.add(candidate);
                    }
                }
            }
        }

        return results;
    }

    @Override
    public int hashCode() {
        return stateArray.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PuzzleState))
            return false;
        if (obj == this)
            return true;

        PuzzleState toCompare = (PuzzleState) obj;
        return toCompare.stateArray.equals(stateArray);
    }

    @Override
    public String toString()
    {
        String result = new String();
        for (int i = 0; i < iYLength; i++)
        {
            for(int j = 0; j < iXLength; j++)
            {
                result += stateArray.get(i * iYLength + j).toString();
            }
        }
        return result;
    }
}
