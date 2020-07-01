package com.flyingpigeon.library;

/**
 * @author xiaozhongcen
 * @date 20-6-15
 * @since 1.0.0
 */
public interface IServiceManager {

    void publish(Object service, Class<?>... interfaces);

    void publish(Object service);

    void unpublish(Object service);

    void unpublish(Object service, Class<?>... interfaces);

}
