package com.deathrayresearch.outlier.app.events;

/**
 *
 */
public class AppEvent<T> {

  private final AppEventType type;
  private final T payload;

  public AppEvent(AppEventType type, T payload) {
    this.type = type;
    this.payload = payload;
  }

  public AppEventType getType() {
    return type;
  }

  public T getPayload() {
    return payload;
  }
}
