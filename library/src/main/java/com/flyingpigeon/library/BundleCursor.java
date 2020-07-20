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

import android.database.MatrixCursor;
import android.os.Bundle;

/**
 * @author xiaozhongcen
 * @date 20-6-24
 * @since 1.0.0
 */
public final class BundleCursor extends MatrixCursor {
    private Bundle mBundle;
    private static final String TAG = Config.PREFIX + BundleCursor.class.getSimpleName();

    public BundleCursor(Bundle extras, String[] data) {
        super(data, 1);
        mBundle = extras;
    }

    @Override
    public Bundle getExtras() {
        return mBundle;
    }

    @Override
    public Bundle respond(Bundle extras) {
        mBundle = extras;
        return mBundle;
    }
}