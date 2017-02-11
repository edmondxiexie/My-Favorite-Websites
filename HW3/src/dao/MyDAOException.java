/**
 * 08-672 Assignment 3.
 * @author Jiayi Xie
 * @id jiayix
 * 11/25/2016
 */

/**
 * @author Class Sample: ToDoList3.
 */
public class MyDAOException extends Exception {
    private static final long serialVersionUID = 1L;

    public MyDAOException(Exception e) {
        super(e);
    }

    public MyDAOException(String s) {
        super(s);
    }
}