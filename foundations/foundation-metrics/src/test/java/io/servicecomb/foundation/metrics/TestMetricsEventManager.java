/*
 * Copyright 2017 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.servicecomb.foundation.metrics;

import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;

import io.servicecomb.foundation.metrics.event.BizkeeperProcessingRequestEvent;
import io.servicecomb.foundation.metrics.event.BizkeeperProcessingRequestFailedEvent;
import io.servicecomb.foundation.metrics.event.InvocationFinishedEvent;
import io.servicecomb.foundation.metrics.event.InvocationStartProcessingEvent;
import io.servicecomb.foundation.metrics.event.InvocationStartedEvent;
import io.servicecomb.foundation.metrics.event.MetricsEvent;
import io.servicecomb.foundation.metrics.event.MetricsEventListener;
import io.servicecomb.foundation.metrics.event.MetricsEventManager;

public class TestMetricsEventManager {

  @Test
  public void testManager() {
    AtomicBoolean bizkeeperProcessingRequestEventReceived = new AtomicBoolean(false);
    AtomicBoolean bizkeeperProcessingRequestFailedEventReceived = new AtomicBoolean(false);

    AtomicBoolean invocationStartedEventReceived = new AtomicBoolean(false);
    AtomicBoolean invocationStartProcessingEventReceived = new AtomicBoolean(false);
    AtomicBoolean invocationFinishedEventReceived = new AtomicBoolean(false);

    MetricsEventManager.registerEventListener(new MetricsEventListener() {
      @Override
      public Class<? extends MetricsEvent> getConcernedEvent() {
        return BizkeeperProcessingRequestEvent.class;
      }

      @Override
      public void process(MetricsEvent data) {
        bizkeeperProcessingRequestEventReceived.set(true);
      }
    });

    MetricsEventManager.registerEventListener(new MetricsEventListener() {
      @Override
      public Class<? extends MetricsEvent> getConcernedEvent() {
        return BizkeeperProcessingRequestFailedEvent.class;
      }

      @Override
      public void process(MetricsEvent data) {
        bizkeeperProcessingRequestFailedEventReceived.set(true);
      }
    });

    MetricsEventManager.registerEventListener(new MetricsEventListener() {
      @Override
      public Class<? extends MetricsEvent> getConcernedEvent() {
        return InvocationStartedEvent.class;
      }

      @Override
      public void process(MetricsEvent data) {
        invocationStartedEventReceived.set(true);
      }
    });

    MetricsEventManager.registerEventListener(new MetricsEventListener() {
      @Override
      public Class<? extends MetricsEvent> getConcernedEvent() {
        return InvocationStartProcessingEvent.class;
      }

      @Override
      public void process(MetricsEvent data) {
        invocationStartProcessingEventReceived.set(true);
      }
    });

    MetricsEventManager.registerEventListener(new MetricsEventListener() {
      @Override
      public Class<? extends MetricsEvent> getConcernedEvent() {
        return InvocationFinishedEvent.class;
      }

      @Override
      public void process(MetricsEvent data) {
        invocationFinishedEventReceived.set(true);
      }
    });

    MetricsEventManager.triggerEvent(new BizkeeperProcessingRequestEvent("",""));
    MetricsEventManager.triggerEvent(new BizkeeperProcessingRequestFailedEvent("",""));
    MetricsEventManager.triggerEvent(new InvocationStartedEvent("",System.nanoTime()));
    MetricsEventManager.triggerEvent(new InvocationStartProcessingEvent("",System.nanoTime(),100));
    MetricsEventManager.triggerEvent(new InvocationFinishedEvent("",System.nanoTime(),200));

    await().atMost(1, TimeUnit.SECONDS)
        .until(invocationFinishedEventReceived::get);

    Assert.assertTrue(bizkeeperProcessingRequestEventReceived.get());
    Assert.assertTrue(bizkeeperProcessingRequestFailedEventReceived.get());
    Assert.assertTrue(invocationFinishedEventReceived.get());
    Assert.assertTrue(invocationStartedEventReceived.get());
    Assert.assertTrue(invocationStartProcessingEventReceived.get());
  }
}