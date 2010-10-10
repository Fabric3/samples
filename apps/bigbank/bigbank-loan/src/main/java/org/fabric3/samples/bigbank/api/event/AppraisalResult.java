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
 * Issued when an property appraisal has been performed.
 *
 * @version $Revision$ $Date$
 */
public class AppraisalResult extends ApplicationEvent {
    private static final long serialVersionUID = 1094048646709453908L;
    public static int APPROVED = 1;
    public static int DECLINED = -1;
    private int result;
    private String[] comments;

    public AppraisalResult(long loanId, int result, String[] comments) {
        super(loanId);
        this.result = result;
        this.comments = comments;
    }

    public int getResult() {
        return result;
    }

    public String[] getComments() {
        return comments;
    }

}
