import java.io.Serializable;

/**
 * Created by pascal on 14.01.16.
 */


public class Message implements Serializable {
    public static final int MESSAGE_WORK = 0;
    public static final int MESSAGE_SPLIT = 1;

    public static final int MESSAGE_WORK_REQUEST = 0;
    public static final int MESSAGE_WORK_ANSWER = 1;
    public static final int MESSAGE_WORK_END = 2;

    public static final int MESSAGE_SPLIT_CHECK = 3;
    public static final int MESSAGE_SPLIT_NO = 4;
    public static final int MESSAGE_SPLIT_END = 5;
    public static final int MESSAGE_SPLIT_YES = 6;
    public static final int MESSAGE_SPLIT_ANWSER = 7;
    public static final int MESSAGE_SPLIT_REJECT = 8;

    public int messageType;
    public PuzzleStack stack;
    public int bound;

}
