/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package loanapp.store.memory;

import loanapp.domain.LoanRecord;
import loanapp.store.StoreException;
import loanapp.store.StoreService;
import org.oasisopen.sca.annotation.Scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory StoreService that uses a Map for persistence.
 *
 * @version $Revision: 8744 $ $Date: 2010-03-25 19:43:45 +0100 (Thu, 25 Mar 2010) $
 */
@Scope("COMPOSITE")
public class MemoryStoreComponent implements StoreService {
    private long counter;
    private Map<Long, LoanRecord> cache = new ConcurrentHashMap<Long, LoanRecord>();
    private Map<String, LoanRecord> ssnCache = new ConcurrentHashMap<String, LoanRecord>();

    public void save(LoanRecord record) throws StoreException {
        long id = ++counter;
        record.setId(id);
        cache.put(record.getId(), record);
        ssnCache.put(record.getSsn(), record);
    }

    public void update(LoanRecord record) throws StoreException {
    }

    public void remove(long id) throws StoreException {
        LoanRecord record = cache.remove(id);
        if (record != null) {
            ssnCache.remove(record.getSsn());
        }
    }

    public LoanRecord find(long id) throws StoreException {
        return cache.get(id);
    }

    public LoanRecord findBySSN(String ssn) throws StoreException {
        return ssnCache.get(ssn);
    }

}
