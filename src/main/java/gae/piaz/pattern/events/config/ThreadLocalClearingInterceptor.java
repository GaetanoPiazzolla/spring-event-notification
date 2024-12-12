package gae.piaz.pattern.events.config;

import gae.piaz.pattern.events.service.ApplicationEventQueue;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * This interceptor clears the thread local request events after the request is completed. It should
 * not be needed since spring use a new thread for each request. We do this for thread pool
 * scenarios.
 */
@RequiredArgsConstructor
@Slf4j
public class ThreadLocalClearingInterceptor implements HandlerInterceptor {

    private final ApplicationEventQueue applicationEventQueue;

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {
        log.trace("Clearing thread local request events");
        applicationEventQueue.clear(); // Clear ThreadLocal events after request completion
    }
}
