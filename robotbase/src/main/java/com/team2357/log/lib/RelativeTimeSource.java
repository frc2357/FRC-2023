package com.team2357.log.lib;

@FunctionalInterface
public interface RelativeTimeSource {
  public long convertToRelativeNanos(long nanos);
}
