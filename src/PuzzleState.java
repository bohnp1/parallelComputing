import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by pascal on 26.11.15.
 */
public class PuzzleState implements Serializable
{
    static public int iXLength;
    static public int iYLength;


    private int depth;

    public ArrayList<Character> stateArray = null;

    private enum Direction {UP, DOWN, RIGHT, LEFT}

    public PuzzleState(int depth){
        this.depth = depth;
    }

    public PuzzleState (Character[] charState)
    {
        stateArray = new ArrayList<>(Arrays.asList(charState));
        this.depth = 0;
    }

    public PuzzleState (ArrayList<Character> charState)
    {
        stateArray = charState;
    }


    public int getDepth() {
        return depth;
    }

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
            for (int j=0; j < sEndconfig.stateArray.size(); j++)
            {
                if (stateArray.get(i).compareTo(sEndconfig.stateArray.get(j)) == 0 && stateArray.get(i).charValue() != 0)
                {
                    ManhattanDistance += Math.abs((i / PuzzleState.iYLength) - (j / PuzzleState.iYLength));
                    ManhattanDistance += Math.abs((i % PuzzleState.iXLength) - (j % PuzzleState.iXLength));
                    break;
                }
            }
        }
        return ManhattanDistance;
    }

    public PuzzleStackElement expand (PuzzleState stateToAvoid)
    {
        PuzzleStackElement results = new PuzzleStackElement();
        results.add(this);
        int newDepth = this.depth + 1;
        for (int i = 0; i < iYLength; i++)
        {
            for(int j = 0; j < iXLength; j++)
            {
                if (stateArray.get(i * iYLength + j) == 0)
                {
                    if (i != 0)
                    {
                        //Down
                        PuzzleState candidate = new PuzzleState(newDepth);
                        candidate.stateArray = (ArrayList<Character>)stateArray.clone();
                        candidate.stateArray.set(i * iYLength + j , stateArray.get((i - 1) * iYLength + j));
                        candidate.stateArray.set((i - 1) * iYLength + j, stateArray.get(i * iYLength + j));

                        if (!candidate.equals(stateToAvoid)) results.add(candidate);
                    }
                    if (i != iYLength - 1)
                    {
                        //Up
                        PuzzleState candidate = new PuzzleState(newDepth);
                        candidate.stateArray = (ArrayList<Character>)stateArray.clone();
                        candidate.stateArray.set((i * iYLength + j), stateArray.get((i + 1) * iYLength + j));
                        candidate.stateArray.set((i + 1) * iYLength + j ,stateArray.get(i * iYLength + j));
                        if (!candidate.equals(stateToAvoid)) results.add(candidate);
                    }
                    if (j != 0)
                    {
                        //Right
                        PuzzleState candidate = new PuzzleState(newDepth);
                        candidate.stateArray = (ArrayList<Character>)stateArray.clone();
                        candidate.stateArray.set((i * iYLength + j) , stateArray.get(i * iYLength + j - 1));
                        candidate.stateArray.set((i * iYLength + j - 1), stateArray.get(i * iYLength + j));
                        if (!candidate.equals(stateToAvoid)) results.add(candidate);
                    }
                    if (j != iXLength - 1)
                    {
                        //Left
                        PuzzleState candidate = new PuzzleState(newDepth);
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
                result += stateArray.get(i * iYLength + j) + 0;
            }
        }
        return result;
    }
}
