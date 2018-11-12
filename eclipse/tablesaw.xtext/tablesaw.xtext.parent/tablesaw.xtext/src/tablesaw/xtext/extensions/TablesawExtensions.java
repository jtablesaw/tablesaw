package tablesaw.xtext.extensions;

import tablesaw.xtext.lib.Table1;
import tablesaw.xtext.lib.Table2;
import tablesaw.xtext.lib.Table3;
import tablesaw.xtext.lib.Table4;
import tablesaw.xtext.lib.Table5;
import tablesaw.xtext.lib.Table6;

public class TablesawExtensions {

	public static <T1> Table1<T1> operator_add(final Table1<T1> table, final Table1.Row1<T1> row) {
		table.append(row);
		return table;
	}
	public static <T1, T2> Table2<T1, T2> operator_add(final Table2<T1, T2> table, final Table2.Row2<T1, T2> row) {
		table.append(row);
		return table;
	}
	public static <T1, T2, T3> Table3<T1, T2, T3> operator_add(final Table3<T1, T2, T3> table, final Table3.Row3<T1, T2, T3> row) {
		table.append(row);
		return table;
	}
	public static <T1, T2, T3, T4> Table4<T1, T2, T3, T4> operator_add(final Table4<T1, T2, T3, T4> table, final Table4.Row4<T1, T2, T3, T4> row) {
		table.append(row);
		return table;
	}
	public static <T1, T2, T3, T4, T5> Table5<T1, T2, T3, T4, T5> operator_add(final Table5<T1, T2, T3, T4, T5> table, final Table5.Row5<T1, T2, T3, T4, T5> row) {
		table.append(row);
		return table;
	}
	public static <T1, T2, T3, T4, T5, T6> Table6<T1, T2, T3, T4, T5, T6> operator_add(final Table6<T1, T2, T3, T4, T5, T6> table, final Table6.Row6<T1, T2, T3, T4, T5 ,T6> row) {
		table.append(row);
		return table;
	}

}
