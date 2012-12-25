/*
 * Copyright (c) 2008-2012, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.queue;

import com.hazelcast.core.ItemEventType;
import com.hazelcast.nio.Data;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.KeyBasedOperation;
import com.hazelcast.spi.impl.AbstractNamedOperation;

import java.util.Collection;

/**
 * @ali 12/6/12
 */
public abstract class QueueOperation extends AbstractNamedOperation implements KeyBasedOperation {

    transient Object response;

    protected QueueOperation() {
    }

    protected QueueOperation(String name) {
        super(name);
    }

    public QueueContainer getContainer() {
        QueueService queueService = getService();
        boolean fromBackup = false;
        if (this instanceof BackupOperation){
            fromBackup = true;
        }
        return queueService.getContainer(name, fromBackup);
    }

    public Object getResponse() {
        return response;
    }

    public String getServiceName() {
        return QueueService.NAME;
    }

    public int getKeyHash() {
        return name.hashCode();
    }

    public boolean hasListener(){
        EventService eventService = getNodeEngine().getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations(getServiceName(), name);
        return registrations.size() > 0;
    }

    public void publishEvent(ItemEventType eventType, Data data){
        EventService eventService = getNodeEngine().getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations(getServiceName(), name);
        for (EventRegistration registration: registrations){
            QueueEventFilter filter = (QueueEventFilter)registration.getFilter();
            QueueEvent event = new QueueEvent(name, filter.isIncludeValue() ? data : null, eventType);
            eventService.publishEvent(getServiceName(), registration, event);
        }
    }


}