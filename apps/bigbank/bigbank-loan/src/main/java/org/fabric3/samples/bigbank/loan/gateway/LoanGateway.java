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
package org.fabric3.samples.bigbank.loan.gateway;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

import org.fabric3.samples.bigbank.api.loan.LoanService;
import org.fabric3.samples.bigbank.api.message.LoanApplication;

/**
 * Receives loan applications from a batch source such as the file system binding (binding.file).
 *
 * @version $Rev: 9526 $ $Date: 2010-10-10 15:32:06 +0200 (Sun, 10 Oct 2010) $
 */
@Scope("COMPOSITE")
public class LoanGateway {

    @Reference
    protected LoanService loanService;

    private JAXBContext context;

    public LoanGateway() throws JAXBException {
        context = JAXBContext.newInstance(LoanApplication.class);
    }

    public void process(InputStream stream) throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        LoanApplication application = (LoanApplication) unmarshaller.unmarshal(stream);
        loanService.apply(application);
    }

}
