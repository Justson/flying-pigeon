package com.flyingpigeon.library;

/**
 * @author xiaozhongcen
 * @date 20-6-22
 * @since 1.0.0
 */
public final class LargeFlyPigeon {

    private String route;
    private Object[] params;
    private Pigeon pigeon;

    LargeFlyPigeon(Pigeon pigeon, String route, Object[] params) {
        this.route = route;
        this.params = params;
        this.pigeon = pigeon;
    }

    public RequestLargeFlyPigeon resquestLarge() {
        return new RequestLargeFlyPigeon(this);
    }

    public ResponseLargeFlyPigeon responseLarge() {
        return new ResponseLargeFlyPigeon(this);
    }

    public interface Fly {
        String fly();
    }

    public interface Fly0 {
        <T> T fly();
    }

    public static final class RequestLargeFlyPigeon implements Fly {

        private LargeFlyPigeon mLargeFlyPigeon;

        RequestLargeFlyPigeon(LargeFlyPigeon largeFlyPigeon) {
            this.mLargeFlyPigeon = largeFlyPigeon;
        }

        @Override
        public String fly() {
            return mLargeFlyPigeon.pigeon.routeLargeRequest(mLargeFlyPigeon.route, mLargeFlyPigeon.params);
        }
    }

    public static final class ResponseLargeFlyPigeon implements Fly0 {

        private LargeFlyPigeon mLargeFlyPigeon;


        ResponseLargeFlyPigeon(LargeFlyPigeon largeFlyPigeon) {
            this.mLargeFlyPigeon = largeFlyPigeon;
        }

        @Override
        public <T> T fly() {
            return mLargeFlyPigeon.pigeon.routeLargeResponse(mLargeFlyPigeon.route, mLargeFlyPigeon.params);
        }
    }
}
