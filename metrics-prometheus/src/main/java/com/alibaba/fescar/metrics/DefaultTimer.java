package com.alibaba.fescar.metrics;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class DefaultTimer implements Timer {
  private final Id id;

  private final Id measurementCountId;

  private final Id measurementTotalId;

  private final Id measurementMaxId;

  private volatile TimerValue value;

  private final Clock clock;

  public DefaultTimer(Id id) {
    this(id, SystemClock.INSTANCE);
  }

  public DefaultTimer(Id id, Clock clock) {
    this.id = id;
    this.measurementCountId = new Id(id.getName()).addTag(id.getTags())
        .addTag(IdConstants.STATISTIC_KEY, IdConstants.STATISTIC_VALUE_COUNT);
    this.measurementTotalId = new Id(id.getName()).addTag(id.getTags())
        .addTag(IdConstants.STATISTIC_KEY, IdConstants.STATISTIC_VALUE_TOTAL);
    this.measurementMaxId = new Id(id.getName()).addTag(id.getTags())
        .addTag(IdConstants.STATISTIC_KEY, IdConstants.STATISTIC_VALUE_MAX);
    this.value = new TimerValue();
    this.clock = clock;
  }

  @Override
  public Id getId() {
    return id;
  }

  @Override
  public void record(long value, TimeUnit unit) {
    this.value = this.value.record(value, unit);
  }

  @Override
  public long count() {
    return this.value.getCount();
  }

  @Override
  public long total() {
    return this.value.getTotal();
  }

  @Override
  public long max() {
    return this.value.getMax();
  }

  @Override
  public Iterable<Measurement> measure() {
    long timestamp = clock.getTimestamp();
    TimerValue value = this.value;
    this.value = new TimerValue();
    return Arrays.asList(new Measurement(measurementCountId, timestamp, value.getCount()),
        new Measurement(measurementTotalId, timestamp, value.getTotal()),
        new Measurement(measurementMaxId, timestamp, value.getMax()));
  }
}
