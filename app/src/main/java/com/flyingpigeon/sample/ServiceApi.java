package com.flyingpigeon.sample;

import com.flyingpigeon.library.anotation.RequestLarge;
import com.flyingpigeon.library.anotation.ResponseLarge;

import java.util.ArrayList;

/**
 * @author xiaozhongcen
 * @date 20-6-8
 * @since 1.0.0
 */
public interface ServiceApi {

    void queryTest(int id);

    void queryItems(int id, double score, long timestamp, short gender, float ring, byte b, boolean isABoy);

    void submitInformation(String uuid, int hash, Information information);

    int createPoster(Poster poster);

    Poster queryPoster(String posterId);

    double testDouble();

    long testLong();

    short testShort();

    float testFloat();

    byte testByte();

    boolean testBoolean();

    Information testParcelable();

    @RequestLarge
    String testLargeBlock(String param, byte[] data);

    @ResponseLarge
    byte[] testLargeBlock(String param, int hash);

    void testArrayList(ArrayList<String> items);
}
