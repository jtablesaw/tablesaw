package com.deathrayresearch.outlier.app.events;

/**
 *
 */
public interface AppEventPublisher {

  default void publish(AppEvent event) {
    Notifier.getInstance().publish(event);
  }
}
