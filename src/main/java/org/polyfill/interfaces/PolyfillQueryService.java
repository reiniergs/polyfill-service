package org.polyfill.interfaces;

import org.polyfill.components.Polyfill;

import java.util.List;

/**
 * Created by smo on 2/4/17.
 */
// TODO: need to decide the shape later
public interface PolyfillQueryService {
    List<Polyfill> getPolyfillsByUserAgent(UserAgent userAgent);
}
