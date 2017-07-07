package org.polyfillservice.api.interfaces;

/**
 * Created by smo on 2/26/17.
 * Service to parse user agent string into user agent object.
 */
public interface UserAgentParserService {

    /**
     * Parse user agent string and construct a UserAgent object from that.
     * @param uaString user agent string retrieved from request header
     * @return a UserAgent object representing the uaString
     */
    UserAgent parse(String uaString);
}
