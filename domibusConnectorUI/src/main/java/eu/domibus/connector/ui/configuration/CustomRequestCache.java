package eu.domibus.connector.ui.configuration;

import org.slf4j.LoggerFactory;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpSessionRequestCache that avoids saving internal framework requests.
 */
class CustomRequestCache extends HttpSessionRequestCache {
    /**
     * {@inheritDoc}
     *
     * If the method is considered an internal request from the framework, we skip
     * saving it.
     *
     * @see SecurityUtils#isFrameworkInternalRequest(HttpServletRequest)
     */
    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        if (!SecurityUtils.isFrameworkInternalRequest(request)) {
            super.saveRequest(request, response);
        }
    }

}
