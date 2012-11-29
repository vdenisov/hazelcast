/*
 * Copyright (c) 2008-2012, Hazel Bilisim Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.spi;

import com.hazelcast.partition.PartitionInfo;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.impl.NodeServiceImpl;
import com.hazelcast.spi.impl.PartitionInvocationImpl;
import com.hazelcast.spi.impl.TargetInvocationImpl;

public class InvocationBuilder {

    private final NodeServiceImpl nodeService;
    private final String serviceName;
    private final Operation op;
    private final int partitionId;
    private Address target;
    private int replicaIndex = 0;
    private int tryCount = 100;
    private long tryPauseMillis = 500;

    public InvocationBuilder(NodeServiceImpl nodeService, String serviceName, Operation op, int partitionId) {
        this.nodeService = nodeService;
        this.serviceName = serviceName;
        this.op = op;
        this.partitionId = partitionId;
    }

    public InvocationBuilder(NodeServiceImpl nodeService, String serviceName, Operation op,
                             int partitionId, int replicaIndex, int tryCount, long tryPauseMillis) {
        this.nodeService = nodeService;
        this.serviceName = serviceName;
        this.op = op;
        this.partitionId = partitionId;
        this.replicaIndex = replicaIndex;
        this.tryCount = tryCount;
        this.tryPauseMillis = tryPauseMillis;
    }

    public InvocationBuilder setReplicaIndex(int replicaIndex) {
        this.replicaIndex = replicaIndex;
        return this;
    }

    public InvocationBuilder setTryCount(int tryCount) {
        this.tryCount = tryCount;
        return this;
    }

    public InvocationBuilder setTryPauseMillis(long tryPauseMillis) {
        this.tryPauseMillis = tryPauseMillis;
        return this;
    }

    public InvocationBuilder setTarget(final Address target) {
        this.target = target;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Operation getOp() {
        return op;
    }

    public PartitionInfo getPartitionInfo() {
        return partitionId > -1 ? nodeService.getPartitionInfo(partitionId) : null;
    }

    public int getReplicaIndex() {
        return replicaIndex;
    }

    public int getTryCount() {
        return tryCount;
    }

    public long getTryPauseMillis() {
        return tryPauseMillis;
    }

    public Address getTarget() {
        return target;
    }

    public Invocation build() {
        if (target == null) {
            return new PartitionInvocationImpl(nodeService, serviceName, op, partitionId, replicaIndex, tryCount, tryPauseMillis);
        } else {
            return new TargetInvocationImpl(nodeService, serviceName, op, partitionId, replicaIndex, target, tryCount, tryPauseMillis);
        }
    }
}