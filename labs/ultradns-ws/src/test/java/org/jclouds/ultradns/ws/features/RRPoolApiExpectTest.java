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

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.ultradns.ws.UltraDNSWSApi;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiExpectTest;
import org.jclouds.ultradns.ws.parse.GetLoadBalancingPoolsByZoneResponseTest;
import org.jclouds.ultradns.ws.parse.GetResourceRecordsOfResourceRecordResponseTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "RRPoolApiExpectTest")
public class RRPoolApiExpectTest extends BaseUltraDNSWSApiExpectTest {

   HttpRequest list = HttpRequest.builder().method("POST")
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader("Host", "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_rrpools.xml", "application/xml")).build();

   HttpResponse listResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/lbpools.xml", "application/xml")).build();
   
   public void testListWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(list, listResponse);

      assertEquals(
            success.getRRPoolApiForZone("jclouds.org.").list().toString(),
            new GetLoadBalancingPoolsByZoneResponseTest().expected().toString());
   }

   HttpRequest listRecords = HttpRequest.builder().method("POST")
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader("Host", "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_rrrecords.xml", "application/xml")).build();

   HttpResponse listRecordsResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/records.xml", "application/xml")).build();

   public void testListRecordsWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(listRecords, listRecordsResponse);

      assertEquals(
            success.getRRPoolApiForZone("jclouds.org.").listRecords("04053D8E57C7931F").toString(),
            new GetResourceRecordsOfResourceRecordResponseTest().expected().toString());
   }

   HttpRequest delete = HttpRequest.builder().method("POST")
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader("Host", "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/delete_lbpool.xml", "application/xml")).build();

   HttpResponse deleteResponse = HttpResponse.builder().statusCode(404)
         .payload(payloadFromResourceWithContentType("/lbpool_deleted.xml", "application/xml")).build();

   public void testDeleteWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(delete, deleteResponse);
      success.getZoneApi().delete("04053D8E57C7931F");
   }

   HttpResponse poolDoesntExist = HttpResponse.builder().message("Server Epoolor").statusCode(500)
         .payload(payloadFromResource("/lbpool_doesnt_exist.xml")).build();
   
   public void testDeleteWhenResponseRRNotFound() {
      UltraDNSWSApi notFound = requestSendsResponse(delete, poolDoesntExist);
      notFound.getZoneApi().delete("04053D8E57C7931F");
   }
}
