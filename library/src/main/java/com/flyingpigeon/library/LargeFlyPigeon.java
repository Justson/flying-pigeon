/*
 * Copyright (C)  Justson(https://github.com/Justson/flying-pigeon)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        <T> T fly();
    }

    public static final class RequestLargeFlyPigeon implements Fly {

        private LargeFlyPigeon mLargeFlyPigeon;

        RequestLargeFlyPigeon(LargeFlyPigeon largeFlyPigeon) {
            this.mLargeFlyPigeon = largeFlyPigeon;
        }

        @Override
        public <T> T fly() {
            return mLargeFlyPigeon.pigeon.routeLargeRequest(mLargeFlyPigeon.route, mLargeFlyPigeon.params);
        }
    }

    public static final class ResponseLargeFlyPigeon implements Fly {

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
