package tech.tablesaw.columns.instant;

import tech.tablesaw.api.InstantColumn;
import tech.tablesaw.columns.temporal.TemporalMapFunctions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public interface InstantMapFunctions extends TemporalMapFunctions<Instant> {

    @Override
    InstantColumn plus(long amountToAdd, ChronoUnit unit);

    @Override
    default InstantColumn plusYears(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.YEARS);
    }

    @Override
    default InstantColumn plusMonths(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.MONTHS);
    }

    @Override
    default InstantColumn plusWeeks(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.WEEKS);
    }

    @Override
    default InstantColumn plusDays(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.DAYS);
    }

    @Override
    default InstantColumn plusHours(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.HOURS);
    }

    @Override
    default InstantColumn plusMinutes(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.MINUTES);
    }

    @Override
    default InstantColumn plusSeconds(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.SECONDS);
    }

    @Override
    default InstantColumn plusMillis(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.MILLIS);
    }

    @Override
    default InstantColumn plusMicros(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.MICROS);
    }
}
