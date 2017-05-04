package org.polyfill.interfaces;

/**
 * Created by smo on 2/26/17.
 * Service to parse user agent string.
 */
public interface UserAgentParserService {

    /**
     * Parse user agent string and construct a UserAgent object from that.
     * @param userAgentString user agent string retrieved from request header
     * @return a UserAgent object representing the userAgentString
     */
    UserAgent parse(String userAgentString);
}
