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
import javax.xml.bind.JAXBException;

/**
 * Receives loan applications from a batch source such as the file system binding (binding.file).
 *
 * @version $Rev: 11193 $ $Date: 2012-10-24 11:30:17 +0200 (Wed, 24 Oct 2012) $
 */
public interface LoanGateway {

    public void process(InputStream stream) throws JAXBException;

}
