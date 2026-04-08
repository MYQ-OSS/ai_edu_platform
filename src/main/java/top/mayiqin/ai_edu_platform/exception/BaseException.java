package top.mayiqin.ai_edu_platform.exception;

/**
 * 业务异常
 * @author m'y'q
 */
public class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

}
