package com.flyingpigeon.library;

/**
 * @author xiaozhongcen
 * @date 20-7-1
 * @since 1.0.0
 */
public class NotFoundRouteException extends RuntimeException {
    public NotFoundRouteException(String route) {
        super(route);
    }
}
