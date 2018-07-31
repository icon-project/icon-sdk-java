/*
 * Copyright 2018 theloop Inc.
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

package foundation.icon.icx.data;

import java.util.List;


public class ScoreApi {
    private String type;
    private String name;
    private List<Param> inputs;
    private List<Param> outputs;
    private String readonly;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<Param> getInputs() {
        return inputs;
    }

    public List<Param> getOutputs() {
        return outputs;
    }

    public String getReadonly() {
        return readonly;
    }

    class Param {
        private String type;
        private String name;

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

    }
}
