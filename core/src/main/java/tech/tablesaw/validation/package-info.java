/**
 * This package provides a framework for validating tables. It is intended to help you check the
 * quality of your data at any point in the analysis process:
 *
 * <p>- When you receive new input data
 *
 * <p>- While your work progresses
 *
 * <p>- Before your final output is delivered
 *
 * <p>
 *
 * <p>The framework operates at the column level. {@link tech.tablesaw.api.ColumnType}-specific
 * "Validators" are added to the columns that you want to validate. Validators are essentially,
 * named predicates that are applied to a column using one of three methods defined on the Column
 * interface:
 *
 * <p>- summaryValidation returns a table that counts the number of validation failures for each
 * {@link tech.tablesaw.validation.Validator} applied to the column. This is a good place to start
 * if you don't know how many failures to expect
 *
 * <p>- detailedValidation returns a table with each failure reported on a specific row. The row
 * contains the cell number (element index) of the column where the exception occurred and what
 * value was found - intermediateValidation returns a table that counts the failures by value found.
 * In this way, you can get a more nuanced view that that provided by the summary, but with
 * (possibly) fewer rows than the detailed report
 *
 * <p>- strictValidation throws a ValidationException when the first validation error is found. Use
 * this when the data needs to be perfect.
 *
 * <p>Because different forms of validation are required, there are numerous validation classes that
 * may be used to define a validation requirement.These may be extended by the user if more complex
 * forms are required.
 *
 * <p>To simplify the process, a number of pre-built Validators are available. These can be accessed
 * as static methods on each of the concrete Validation classes. For example, {@link
 * tech.tablesaw.validation.StringStringValidator} provides a pre-built Validator for identifying
 * values that (don't) endWith a given substring. You set the substring to check for using the
 * value() method.
 */
package tech.tablesaw.validation;
