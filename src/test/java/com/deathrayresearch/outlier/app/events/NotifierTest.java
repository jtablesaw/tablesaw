package com.deathrayresearch.outlier.app.events;

import com.deathrayresearch.outlier.app.model.Project;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 *
 */
public class NotifierTest {

  private Notifier notifier;

  private AppEventListener listener = new AppEventListener() {

    @Override
    public void handleEvent(AppEvent event) {
      assertNotNull(event);
      System.out.println("so far so good");
      System.out.println(event.getPayload());
    }
    @Override
    public String toString() {
      return "Project Changed Listener";
    }
  };

  @Before
  public void setUp() throws Exception {
    notifier = Notifier.getInstance();
    notifier.clearRegistry();
  }

  @Test
  public void testSubscribe() {

    notifier.subscribe(AppEventType.PROJECT_CHANGED, listener);
    assertEquals(1, notifier.registry.size());
  }

  @Test
  public void testCancelSubscription() {
    notifier.subscribe(AppEventType.PROJECT_CHANGED, listener);
    assertEquals(1, notifier.registry.size());

    notifier.cancelSubscription(AppEventType.PROJECT_CHANGED, listener);
    assertEquals(0, notifier.registry.size());

  }

  @Test
  public void testPublish() {
    notifier.subscribe(AppEventType.PROJECT_CHANGED, listener);
    notifier.publish(new AppEvent<>(AppEventType.PROJECT_CHANGED, new Project("x", LocalDate.now(), "", "", "")));
  }
}