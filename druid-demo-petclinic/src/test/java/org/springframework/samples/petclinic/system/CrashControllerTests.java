/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.system;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link CrashController}
 *
 * @author Colin But
 */
// Waiting https://github.com/spring-projects/spring-boot/issues/5574
@Disabled
@WebMvcTest(controllers = CrashController.class)
class CrashControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testTriggerException() throws Exception {
		mockMvc.perform(get("/oups")).andExpect(view().name("exception"))
				.andExpect(model().attributeExists("exception")).andExpect(forwardedUrl("exception"))
				.andExpect(status().isOk());
	}

}
