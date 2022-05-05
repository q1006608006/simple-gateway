package top.ivan.simple.gateway.core;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ivan
 * @description
 * @date 2020/5/20
 */
public interface GlobalExecuteController {

    @ResponseBody
    Object execute(@RequestBody(required = false) byte[] body, @RequestHeader HttpHeaders headers, HttpServletRequest req, HttpServletResponse rsp) throws Exception;
}
