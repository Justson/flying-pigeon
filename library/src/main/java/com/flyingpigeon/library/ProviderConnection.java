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

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.RemoteException;

import java.io.Closeable;

/** One lazily acquired, reusable unstable provider client per Pigeon. */
final class ProviderConnection implements Closeable {
    private final ContentResolver resolver;
    private final Uri base;
    private final Object lock = new Object();
    private ClientReference current;
    private boolean closed;

    ProviderConnection(ContentResolver resolver, Uri base) {
        this.resolver = resolver;
        this.base = base;
    }

    Bundle call(String method, String arg, Bundle extras) throws RemoteException {
        ClientReference reference = acquire();
        try {
            return reference.client.call(method, arg, extras);
        } catch (DeadObjectException e) {
            invalidate(reference);
            // The remote method may have run. Never replay this request automatically.
            throw e;
        } finally {
            release(reference);
        }
    }

    Cursor query(Uri uri, String[] projection, String selection,
                 String[] selectionArgs, String sortOrder) throws RemoteException {
        final ClientReference reference = acquire();
        boolean handedOff = false;
        try {
            Cursor cursor = reference.client.query(uri, projection, selection, selectionArgs, sortOrder);
            if (cursor == null) {
                return null;
            }
            Cursor result = new CursorWrapper(cursor) {
                private boolean cursorClosed;

                @Override
                public synchronized void close() {
                    if (!cursorClosed) {
                        cursorClosed = true;
                        try {
                            super.close();
                        } finally {
                            release(reference);
                        }
                    }
                }
            };
            // Keep the client alive until the caller finishes reading the cursor.
            handedOff = true;
            return result;
        } catch (DeadObjectException e) {
            invalidate(reference);
            throw e;
        } finally {
            if (!handedOff) {
                release(reference);
            }
        }
    }

    private ClientReference acquire() {
        synchronized (lock) {
            if (closed) {
                throw new IllegalStateException("Pigeon is closed");
            }
            if (current == null) {
                ContentProviderClient client = resolver.acquireUnstableContentProviderClient(base);
                if (client == null) {
                    throw new IllegalStateException("Provider not found: " + base);
                }
                current = new ClientReference(client);
            }
            current.users++;
            return current;
        }
    }

    private void invalidate(ClientReference reference) {
        synchronized (lock) {
            if (current == reference) {
                current = null;
            }
        }
    }

    @SuppressWarnings("deprecation") // release() also supports API 19-23.
    private void release(ClientReference reference) {
        synchronized (lock) {
            reference.users--;
            if (reference.users == 0 && current != reference) {
                reference.client.release();
            }
        }
    }

    /** Does not interrupt calls or cursors already in use. */
    @Override
    @SuppressWarnings("deprecation")
    public void close() {
        synchronized (lock) {
            closed = true;
            ClientReference reference = current;
            current = null;
            if (reference != null && reference.users == 0) {
                reference.client.release();
            }
        }
    }

    private static final class ClientReference {
        final ContentProviderClient client;
        int users;

        ClientReference(ContentProviderClient client) {
            this.client = client;
        }
    }
}
