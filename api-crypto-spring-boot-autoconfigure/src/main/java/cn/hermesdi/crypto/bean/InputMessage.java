package cn.hermesdi.crypto.bean;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * 请求数据输入流转换
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
public class InputMessage implements HttpInputMessage {

    /**
     * 请求体
     */
    private InputStream body;

    /**
     * 请求头
     */
    private HttpHeaders headers;


    public InputMessage() {
    }

    public InputMessage(InputStream body, HttpHeaders headers) {
        this.body = body;
        this.headers = headers;
    }

    public void setBody(InputStream body) {
        this.body = body;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    @Override
    public InputStream getBody() throws IOException {
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}