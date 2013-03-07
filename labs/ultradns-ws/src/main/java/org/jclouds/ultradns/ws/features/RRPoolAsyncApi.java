/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
package org.jclouds.ultradns.ws.features;

import javax.inject.Named;
import javax.ws.rs.POST;

import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.ultradns.ws.domain.LBPool;
import org.jclouds.ultradns.ws.domain.ResourceRecord;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.xml.LBPoolListHandler;
import org.jclouds.ultradns.ws.xml.ResourceRecordListHandler;

import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see RRPoolApi
 * @see <a href="https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01?wsdl" />
 * @see <a href="https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf" />
 * @author Adrian Cole
 */
@RequestFilters(SOAPWrapWithPasswordAuth.class)
@VirtualHost
public interface RRPoolAsyncApi {

   /**
    * @see RRPoolApi#list()
    */
   @Named("getLoadBalancingPoolsByZone")
   @POST
   @XMLResponseParser(LBPoolListHandler.class)
   @Payload("<v01:getLoadBalancingPoolsByZone><zoneName>{zoneName}</zoneName><lbPoolType>RR</lbPoolType></v01:getLoadBalancingPoolsByZone>")
   ListenableFuture<FluentIterable<LBPool>> list() throws ResourceNotFoundException;

   /**
    * @see RRPoolApi#listRecords(String)
    */
   @Named("getRRPoolRecords")
   @POST
   @XMLResponseParser(ResourceRecordListHandler.class)
   @Payload("<v01:getRRPoolRecords><lbPoolId>{poolId}</lbPoolId></v01:getRRPoolRecords>")
   ListenableFuture<FluentIterable<ResourceRecord>> listRecords(@PayloadParam("poolId") String poolId)
         throws ResourceNotFoundException;

   /**
    * @see RRPoolApi#delete(String)
    */
   @Named("deleteRRPool")
   @POST
   @Payload("<v01:deleteRRPool><transactionID /><lbPoolID>{lbPoolID}</lbPoolID><DeleteAll>Yes</DeleteAll></v01:deleteRRPool>")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@PayloadParam("lbPoolID") String id);
}
