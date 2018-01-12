/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.servicecomb.metrics.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.servicecomb.foundation.common.exceptions.ServiceCombException;

public class LongMetricValue extends MetricValue<Long> {
  public LongMetricValue(Long value, Map<String, String> dimensions) {
    super(value, dimensions);
  }

  public LongMetricValue(@JsonProperty("key") String key,
      @JsonProperty("value") Long value,
      @JsonProperty("dimensions") Map<String, String> dimensions) {
    super(key, value, dimensions);
  }

  public LongMetricValue merge(LongMetricValue value) {
    if (this.getKey().equals(value.getKey())) {
      return new LongMetricValue(this.getKey(), this.getValue() + value.getValue(), this.getDimensions());
    }
    throw new ServiceCombException("unable merge different key values,source key :" + value.getKey() +
        " target key :" + this.getKey());
  }

  public static List<LongMetricValue> merge(List<LongMetricValue> source, List<LongMetricValue> target) {
    Map<String, LongMetricValue> finalValues = new HashMap<>();
    for (LongMetricValue t : target) {
      finalValues.put(t.getKey(), t);
    }
    for (LongMetricValue s : source) {
      if (finalValues.containsKey(s.getKey())) {
        finalValues.put(s.getKey(), finalValues.get(s.getKey()).merge(s));
      }
    }
    return new ArrayList<>(finalValues.values());
  }
}