package cn.ittiger.im;


/**
 * Retrofit+RxJava的请求封装/服务端下发下游消息的封装。
 * Created by Chu_xi on 2016/8/5.
 */

public class WrapData<T> {

    private int code;
    private String msg;
    private T result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T data) {
        this.result = data;
    }
    public boolean isSuccessed(){
        return code == 0;
    }

}
