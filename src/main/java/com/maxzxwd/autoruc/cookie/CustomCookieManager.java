package com.maxzxwd.autoruc.cookie;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CustomCookieManager extends CookieManager {

    private CookiePolicy policyCallback;
    public final CustomCookieStore cookieJar;

    public CustomCookieManager() {
        this(null, null);
    }

    public CustomCookieManager(CustomCookieStore store,
                         CookiePolicy cookiePolicy) {
        super(Objects.requireNonNullElseGet(store, CustomCookieStore::new), Objects.requireNonNullElse(cookiePolicy, CookiePolicy.ACCEPT_ORIGINAL_SERVER));

        cookieJar = (CustomCookieStore) this.getCookieStore();
        policyCallback = Objects.requireNonNullElse(cookiePolicy, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    @Override
    public void setCookiePolicy(CookiePolicy cookiePolicy) {

        super.setCookiePolicy(cookiePolicy);

        if (cookiePolicy != null) {
            policyCallback = cookiePolicy;
        }
    }

    public void
    put(URI uri, Map<String, List<String>> responseHeaders)
            throws IOException
    {
        super.put(uri, responseHeaders);

        // pre-condition check
        if (uri == null || responseHeaders == null) {
            throw new IllegalArgumentException("Argument is null");
        }

        for (String headerKey : responseHeaders.keySet()) {
            // RFC 2965 3.2.2, key must be 'Set-Cookie2'
            // we also accept 'Set-Cookie' here for backward compatibility
            if (headerKey == null
                    || !(headerKey.equalsIgnoreCase("Set-Cookie2")
                    || headerKey.equalsIgnoreCase("Set-Cookie")
            )
            )
            {
                continue;
            }

            for (String headerValue : responseHeaders.get(headerKey)) {
                try {
                    var cookieInfo = new HttpCookieInfo(System.currentTimeMillis(), headerValue);
                    List<HttpCookie> cookies;
                    try {
                        cookies = HttpCookie.parse(headerValue);
                    } catch (IllegalArgumentException e) {
                        // Bogus header, make an empty list and log the error
                        cookies = java.util.Collections.emptyList();
                    }
                    for (HttpCookie cookie : cookies) {

                        String ports = cookie.getPortlist();
                        if (ports != null) {
                            int port = uri.getPort();
                            if (port == -1) {
                                port = "https".equals(uri.getScheme()) ? 443 : 80;
                            }
                            // Only store cookies with a port list
                            // IF the URI port is in that list, as per
                            // RFC 2965 section 3.3.2
                            if (isInPortList(ports, port) &&
                                    shouldAcceptInternal(uri, cookie)) {
                                cookieJar.add(uri, cookie, cookieInfo);
                            }
                        } else {
                            if (shouldAcceptInternal(uri, cookie)) {
                                cookieJar.add(uri, cookie, cookieInfo);
                            }
                        }
                    }
                } catch (IllegalArgumentException e) {
                    // invalid set-cookie header string
                    // no-op
                }
            }
        }
    }

    private static boolean isInPortList(String lst, int port) {
        int i = lst.indexOf(',');
        int val;
        while (i > 0) {
            try {
                val = Integer.parseInt(lst, 0, i, 10);
                if (val == port) {
                    return true;
                }
            } catch (NumberFormatException ignored) {
            }
            lst = lst.substring(i+1);
            i = lst.indexOf(',');
        }
        if (!lst.isEmpty()) {
            try {
                val = Integer.parseInt(lst);
                if (val == port) {
                    return true;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return false;
    }

    private boolean shouldAcceptInternal(URI uri, HttpCookie cookie) {

        try {
            return policyCallback.shouldAccept(uri, cookie);
        } catch (Exception ignored) {
            return false;
        }
    }
}
