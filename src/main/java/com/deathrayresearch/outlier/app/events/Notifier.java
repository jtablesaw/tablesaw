package com.deathrayresearch.outlier.app.events;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import java.util.List;

/**
 *
 */
public class Notifier {

  private static Notifier ourInstance = new Notifier();

  ListMultimap<AppEventType, AppEventListener> registry =
      MultimapBuilder.hashKeys().arrayListValues().build();;

  public static Notifier getInstance() {
    return ourInstance;
  }

  private Notifier() {}

  public void subscribe(AppEventType eventType, AppEventListener listener) {
    registry.put(eventType, listener);
  }

  public void cancelSubscription(AppEventType eventType, AppEventListener listener) {
    registry.remove(eventType, listener);
  }

  public void publish(AppEvent event) {
    for (AppEventListener listener : registry.get(event.getType())) {
      listener.handleEvent(event);
    }
  }

  public int subscriberCount() {
    return registry.size();
  }
}
