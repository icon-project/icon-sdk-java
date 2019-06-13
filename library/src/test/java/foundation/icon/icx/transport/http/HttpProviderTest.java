/*
 * Copyright 2019 ICON Foundation
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

package foundation.icon.icx.transport.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpProviderTest {

    @Test
    void testHttpProviderURILegacy() {
        String[] validEndpoints = {
                "http://localhost:9000/api/v3",
                "https://ctz.solidwallet.io/api/v3",
                "http://localhost:9000/api/v3/channel",
        };
        for (String endpoint : validEndpoints) {
            assertDoesNotThrow(() -> {
                new HttpProvider(endpoint);
            });
        }
    }

    @Test
    void testHttpProviderURI() {
        String[] validEndpoints = {
                "http://localhost:9000",
                "https://ctz.solidwallet.io",
        };
        String[] invalidEndpoints = {
                "http://localhost:9000/",
                "http://localhost:9000/api/v3",
                "http://localhost:9000/api/v3/",
                "http://localhost:9000/api/v3/file",
                "https://ctz.solidwallet.io/",
                "https://ctz.solidwallet.io/api/v3",
        };
        for (String endpoint : validEndpoints) {
            assertDoesNotThrow(() -> {
                new HttpProvider(endpoint, 3);
            });
        }
        for (String endpoint : validEndpoints) {
            assertThrows(IllegalArgumentException.class, () -> {
                new HttpProvider(endpoint, 2);
            });
        }
        for (String endpoint : invalidEndpoints) {
            assertThrows(IllegalArgumentException.class, () -> {
                new HttpProvider(endpoint, 3);
            });
        }
    }
}
