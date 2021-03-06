/*
 * Copyright 2013 eXo Platform SAS
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

package httpcontext;

import juzu.Response;
import juzu.View;
import juzu.bridge.vertx.HttpContextTestCase;
import juzu.request.HttpContext;
import juzu.request.RequestContext;
import juzu.request.RequestLifeCycle;

public class A implements RequestLifeCycle {

  public void beginRequest(RequestContext context) {
    HttpContext http = context.getHttpContext();
    HttpContextTestCase.method = http.getMethod();
    HttpContextTestCase.scheme = http.getScheme();
    HttpContextTestCase.port = http.getServerPort();
    HttpContextTestCase.contextPath = http.getContextPath();
  }

  public void endRequest(RequestContext context) {
  }

  @View
  public Response.Content index() {
    return Response.ok("pass");
  }
}