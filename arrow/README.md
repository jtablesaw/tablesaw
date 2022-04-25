# Apache Arrow IO Support

Provides basic support for Apache Arrow as an output format

Note: Only those Arrow vectors needed to persist Tablesaw columns are supported.

## Tablesaw Column to Arrow FieldVector mapping

| Tablesaw Column | Arrow Field Vector     | Notes                                                        |
| --------------- | ---------------------- | ------------------------------------------------------------ |
| BooleanColumn   | BitVector              |                                                              |
| IntColumn       | UInt4Vector            |                                                              |
| ShortColumn     | UInt2Vector            |                                                              |
| LongColumn      | Uint8Vector            |                                                              |
| FloatColumn     | Float4Vector           |                                                              |
| DoubleColumn    | Float8Vector           |                                                              |
| DateColumn      | DateDayVector          |                                                              |
| TimeColumn      | TimeMilliVector        |                                                              |
| DateTimeColumn  | TimeStampMilliVector   |                                                              |
| InstantColumn   | TimeStampMilliTZVector | Instants written with timezone of UTC. No other timezone is supported |
| StringColumn    | VarCharVector          |                                                              |

