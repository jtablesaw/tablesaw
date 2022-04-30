# Apache Arrow IO Support

Provides basic support for Apache Arrow as an output format.

Note 1: To use this in Java 16 or later, you MUST add the add-opens option on the java command line

Note 2: Only those Arrow vectors needed to persist Tablesaw columns are supported.

## Tablesaw Column to Arrow FieldVector mapping

| Tablesaw Column | Arrow Field Vector     | Notes                                                        |
| --------------- |------------------------| ------------------------------------------------------------ |
| BooleanColumn   | BitVector              |                                                              |
| IntColumn       | IntVector              |                                                              |
| ShortColumn     | SmallIntVector         |                                                              |
| LongColumn      | BigIntVector           |                                                              |
| FloatColumn     | Float4Vector           |                                                              |
| DoubleColumn    | Float8Vector           |                                                              |
| DateColumn      | DateDayVector          |                                                              |
| TimeColumn      | TimeMilliVector        |                                                              |
| DateTimeColumn  | TimeStampMilliVector   |                                                              |
| InstantColumn   | TimeStampMilliTZVector | Instants written with timezone of UTC. No other timezone is supported |
| StringColumn    | VarCharVector          |                                                              |
