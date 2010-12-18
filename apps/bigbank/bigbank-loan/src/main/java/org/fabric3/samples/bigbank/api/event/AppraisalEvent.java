/*
 * Copyright (c) 2010 Metaform Systems
 *
 * See the NOTICE file distributed with this work for information
 * regarding copyright ownership.  This file is licensed
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
package org.fabric3.samples.bigbank.api.event;

/**
 * Base appraisal event.
 *
 * @version $Revision: 9533 $ $Date: 2010-10-10 23:30:03 +0200 (Sun, 10 Oct 2010) $
 */
public abstract class AppraisalEvent extends ApplicationEvent {
    public AppraisalEvent(long id) {
        super(id);
    }
}
