package tech.tablesaw.reducing;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;

/**
 * Functions that calculate values over the data of an entire column, such as sum, mean, std. dev, etc.
 */
public interface NumericReduceFunction {

    String functionName();

    double reduce(double[] data);

    default double reduce(FloatColumn data) {
        return this.reduce(data.toDoubleArray());
    }

    default double reduce(DoubleColumn doubles) {
        return this.reduce(doubles.toDoubleArray());
    }

    default double reduce(IntColumn data) {
        return this.reduce(data.toDoubleArray());
    }

    default double reduce(ShortColumn data) {
        return this.reduce(data.toDoubleArray());
    }

    default double reduce(LongColumn data) {
        return this.reduce(data.toDoubleArray());
    }
}
