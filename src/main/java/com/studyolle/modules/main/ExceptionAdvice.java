package com.studyolle.modules.main;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>Exception 핸들러 클래스</h1>
 */
@Slf4j
@ControllerAdvice
public class ExceptionAdvice
{
    /**
     * RuntimeException 핸들러
     * @param account
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler
    public String handleRuntimeException(@CurrentUser Account account,
                                         HttpServletRequest request,
                                         RuntimeException exception)
    {
        if (account != null)
        {
            log.info("'{}' requested '{}'", account.getNickname(), request.getRequestURI());
        }
        else
        {
            log.info("requested '{}'", request.getRequestURI());
        }

        log.error("Bad request", exception);
        return "error";
    }
}
