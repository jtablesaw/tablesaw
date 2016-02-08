package com.deathrayresearch.outlier.app.events;

/**
 *
 */
public interface AppEventListener<T> {

  void handleEvent(AppEvent<T> event);

}
