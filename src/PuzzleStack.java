import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by pascal on 24.12.15.
 */
public class PuzzleStack extends LinkedList<LinkedList<PuzzleState>> {

    PuzzleStack(Character[] init)
    {
        LinkedList<PuzzleState> expandStatesList = new LinkedList<>();
        int sizeCount = PuzzleState.iYLength * PuzzleState.iXLength;
        ArrayList<Character> State = new ArrayList<>(PuzzleState.iYLength * PuzzleState.iXLength);
        for (int i = 0; i < init.length; i++)
        {
            if (init[i] == Character.MAX_VALUE)
            {
                this.add(expandStatesList);
                expandStatesList = new LinkedList<>();
                continue;
            }
            else if (sizeCount-- == 0)
            {
                expandStatesList.add(new PuzzleState(State));
                sizeCount = PuzzleState.iYLength * PuzzleState.iXLength;
                State = new ArrayList<>(PuzzleState.iYLength * PuzzleState.iXLength);
            }
            State.add(init[i]);
        }
    }

    Character[] toCharArray()
    {
        LinkedList<Character> result = new LinkedList<>();
        Character[] resultType = new Character[1];

        for (int i=0; i < this.size(); i++)
        {
            LinkedList<PuzzleState> expandStatesList = this.get(i);
            for (int j=0; j < expandStatesList.size(); j++)
            {
                result.addAll(expandStatesList.get(j).stateArray);
            }
            result.add(Character.MAX_VALUE);
        }
        return result.toArray(resultType);
    }
}
