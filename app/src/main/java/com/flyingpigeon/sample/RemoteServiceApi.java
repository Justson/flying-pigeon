package com.flyingpigeon.sample;

/**
 * @author xiaozhongcen
 * @date 20-6-15
 * @since 1.0.0
 */
public interface RemoteServiceApi {

	void queryItems(int id, double score, long timestamp, short gender, float ring, byte b, boolean isABoy);

}
