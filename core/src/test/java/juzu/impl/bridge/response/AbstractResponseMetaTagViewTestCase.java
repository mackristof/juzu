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

package juzu.impl.bridge.response;

import juzu.test.AbstractWebTestCase;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** @author <a href="mailto:benjamin.paillereau@exoplatform.com">Benjamin Paillereau</a> */
public abstract class AbstractResponseMetaTagViewTestCase extends AbstractWebTestCase {

  @Drone
  WebDriver driver;

  @Test
  public void testPathParam() throws Exception {

    driver.get(applicationURL().toString());
    WebElement trigger = driver.findElement(By.tagName("meta"));

    assertEquals("foo", trigger.getAttribute("name"));
    assertEquals("bar", trigger.getAttribute("content"));
  }
}
