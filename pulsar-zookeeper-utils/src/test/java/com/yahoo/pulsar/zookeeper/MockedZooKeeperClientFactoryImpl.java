/**
 * Copyright 2016 Yahoo Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yahoo.pulsar.zookeeper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.bookkeeper.util.ZkUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.MockZooKeeper;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;

import com.yahoo.pulsar.zookeeper.ZooKeeperClientFactory;
import com.yahoo.pulsar.zookeeper.ZookeeperClientFactoryImpl;

public class MockedZooKeeperClientFactoryImpl implements ZooKeeperClientFactory {

    @Override
    public CompletableFuture<ZooKeeper> create(String serverList, SessionType sessionType, int zkSessionTimeoutMillis) {
        MockZooKeeper mockZooKeeper = MockZooKeeper.newInstance();
        // not used for mock mode
        List<ACL> dummyAclList = new ArrayList<ACL>(0);

        try {
            ZkUtils.createFullPathOptimistic(mockZooKeeper, "/ledgers/available/192.168.1.1:" + 5000,
                    "".getBytes(ZookeeperClientFactoryImpl.ENCODING_SCHEME), dummyAclList, CreateMode.PERSISTENT);

            mockZooKeeper.create("/ledgers/LAYOUT", "1\nflat:1".getBytes(ZookeeperClientFactoryImpl.ENCODING_SCHEME),
                    dummyAclList, CreateMode.PERSISTENT);
            return CompletableFuture.completedFuture(mockZooKeeper);

        } catch (KeeperException | InterruptedException e) {
            CompletableFuture<ZooKeeper> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }
}
