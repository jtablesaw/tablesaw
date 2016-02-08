package com.deathrayresearch.outlier.app.ui.project;

import com.deathrayresearch.outlier.app.events.AppEvent;
import com.deathrayresearch.outlier.app.events.AppEventListener;

/**
 *
 */
public class ProjectView implements AppEventListener {

  @Override
  public void handleEvent(AppEvent event) {
    switch (event.getType()) {
      case PROJECT_CHANGED:

        break;
    }
  }
}
