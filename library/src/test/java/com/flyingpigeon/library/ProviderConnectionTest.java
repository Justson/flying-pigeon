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
import android.net.Uri;
import android.os.Bundle;
import android.os.DeadObjectException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProviderConnectionTest {
    private ContentResolver resolver;
    private ContentProviderClient client;
    private Uri uri;
    private ProviderConnection connection;

    @Before
    public void setUp() {
        resolver = mock(ContentResolver.class);
        client = mock(ContentProviderClient.class);
        uri = mock(Uri.class);
        when(resolver.acquireUnstableContentProviderClient(uri)).thenReturn(client);
        connection = new ProviderConnection(resolver, uri);
    }

    @Test
    public void lazilyAcquiresAndReusesClientAcrossMethods() throws Exception {
        verifyZeroInteractions(resolver);
        Bundle extras = mock(Bundle.class);
        Bundle response = mock(Bundle.class);
        when(client.call("first", null, extras)).thenReturn(response);
        assertSame(response, connection.call("first", null, extras));
        connection.call("second", "arg", null);
        verify(resolver, times(1)).acquireUnstableContentProviderClient(uri);
        verify(client, never()).release();
        connection.close();
        connection.close();
        verify(client, times(1)).release();
    }

    @Test
    public void closingUnusedConnectionDoesNotAcquireClient() {
        connection.close();
        verifyZeroInteractions(resolver, client);
    }

    @Test
    public void closedConnectionCannotReacquireClient() throws Exception {
        connection.close();
        try {
            connection.call("method", null, null);
            fail("Expected a closed connection to reject the call");
        } catch (IllegalStateException expected) {
            verifyZeroInteractions(resolver, client);
        }
    }

    @Test
    public void deadProviderIsNotRetriedUntilNextRequest() throws Exception {
        DeadObjectException dead = mock(DeadObjectException.class);
        when(client.call("write", null, null)).thenThrow(dead);
        ContentProviderClient replacement = mock(ContentProviderClient.class);
        when(resolver.acquireUnstableContentProviderClient(uri)).thenReturn(client, replacement);
        try {
            connection.call("write", null, null);
            fail("Expected the original failure");
        } catch (DeadObjectException expected) {
            assertSame(dead, expected);
        }
        verify(resolver, times(1)).acquireUnstableContentProviderClient(uri);
        verify(client, times(1)).call("write", null, null);
        verify(client).release();
        connection.call("next", null, null);
        verify(replacement).call("next", null, null);
        connection.close();
        verify(replacement).release();
    }

    @Test
    public void businessFailureDoesNotDiscardHealthyConnection() throws Exception {
        when(client.call("bad", null, null)).thenThrow(new IllegalArgumentException("bad input"));
        try {
            connection.call("bad", null, null);
            fail("Expected the business failure");
        } catch (IllegalArgumentException expected) {
            assertEquals("bad input", expected.getMessage());
        }
        connection.call("good", null, null);
        verify(resolver, times(1)).acquireUnstableContentProviderClient(uri);
        connection.close();
        verify(client).release();
    }

    @Test
    public void missingProviderCanBeAcquiredOnLaterRequest() throws Exception {
        when(resolver.acquireUnstableContentProviderClient(uri)).thenReturn(null, client);
        try {
            connection.call("first", null, null);
            fail("Expected a missing provider to fail");
        } catch (IllegalStateException expected) {
            // No failed client is cached.
        }
        connection.call("second", null, null);
        verify(client).call("second", null, null);
        connection.close();
    }

    @Test
    public void queryAndCallShareClientAndNullCursorDoesNotLeakReference() throws Exception {
        assertNull(connection.query(uri, null, null, null, null));
        connection.call("method", null, null);
        verify(resolver, times(1)).acquireUnstableContentProviderClient(uri);
        connection.close();
        verify(client).release();
    }

    @Test
    public void deadQueryInvalidatesClientWithoutReplaying() throws Exception {
        DeadObjectException dead = mock(DeadObjectException.class);
        when(client.query(uri, null, null, null, null)).thenThrow(dead);
        try {
            connection.query(uri, null, null, null, null);
            fail("Expected the query to fail");
        } catch (DeadObjectException expected) {
            assertSame(dead, expected);
        }
        verify(client, times(1)).query(uri, null, null, null, null);
        verify(client).release();
        connection.close();
        verify(client, times(1)).release();
    }

    @Test
    public void closeDoesNotReleaseClientDuringActiveCallOrBlockOtherCalls() throws Exception {
        final CountDownLatch entered = new CountDownLatch(1);
        final CountDownLatch finish = new CountDownLatch(1);
        when(client.call("slow", null, null)).thenAnswer(new Answer<Bundle>() {
            @Override
            public Bundle answer(InvocationOnMock invocation) throws Exception {
                entered.countDown();
                assertTrue(finish.await(5, TimeUnit.SECONDS));
                return null;
            }
        });
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<Bundle> slow = executor.submit(new Callable<Bundle>() {
                @Override
                public Bundle call() throws Exception {
                    return connection.call("slow", null, null);
                }
            });
            assertTrue(entered.await(5, TimeUnit.SECONDS));
            Future<Bundle> fast = executor.submit(new Callable<Bundle>() {
                @Override
                public Bundle call() throws Exception {
                    return connection.call("fast", null, null);
                }
            });
            fast.get(5, TimeUnit.SECONDS);
            connection.close();
            verify(client, never()).release();
            finish.countDown();
            slow.get(5, TimeUnit.SECONDS);
            verify(resolver, times(1)).acquireUnstableContentProviderClient(uri);
            verify(client, times(1)).release();
        } finally {
            finish.countDown();
            executor.shutdownNow();
            connection.close();
        }
    }
}
