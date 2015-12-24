import junit.framework.TestCase;
import org.junit.Test;

import java.util.LinkedList;

/**
 * Created by pascal on 19.12.15.
 */
public class xPuzzleTest extends TestCase {

    static final Character[] START_CONFIG= {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private LinkedList<LinkedList<PuzzleState>> TestStack = new LinkedList<>();
    private PuzzleState start = new PuzzleState(START_CONFIG);

    public void setUp() throws Exception {
        super.setUp();

        PuzzleState.iXLength = 3;
        PuzzleState.iYLength = 3;
        TestStack.add(start.expand(null));

        PuzzleState secondState = TestStack.get(0).remove(1);
        TestStack.add(secondState.expand(start));

        PuzzleState thirdState = TestStack.get(1).remove(1);
        TestStack.add(thirdState.expand(TestStack.get(1).get(0)));

    }

    @Test
    public void testSplitStack() throws Exception {
        LinkedList<LinkedList<PuzzleState>> newStack;

        newStack = xPuzzle.splitStack(TestStack);
        assertEquals(3, newStack.size());
        assertEquals(1, newStack.get(0).size());
        assertEquals(2, newStack.get(1).size());
        assertEquals(1, newStack.get(2).size());
        assertEquals(3, TestStack.size());
        assertEquals(2, TestStack.get(0).size());
        assertEquals(1, TestStack.get(1).size());
        assertEquals(2, TestStack.get(2).size());

    }
}