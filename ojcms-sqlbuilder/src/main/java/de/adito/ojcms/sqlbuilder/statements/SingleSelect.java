package de.adito.ojcms.sqlbuilder.statements;

import de.adito.ojcms.sqlbuilder.*;
import de.adito.ojcms.sqlbuilder.definition.*;
import de.adito.ojcms.sqlbuilder.result.SingleColumnResult;
import de.adito.ojcms.sqlbuilder.util.OJDatabaseException;
import de.adito.ojcms.utils.OptionalNullable;

import java.sql.ResultSet;

/**
 * A single column select statement.
 *
 * @param <VALUE> the type of the result
 * @author Simon Danner, 26.04.2018
 */
public class SingleSelect<VALUE> extends AbstractSelect<SingleSelect<VALUE>>
{
  private IColumnIdentification<VALUE> column;

  /**
   * Creates a new select statement based on single column.
   *
   * @param pStatementExecutor the executor for this statement
   * @param pBuilder           the builder that created this statement to use other kinds of statements for a concrete statement
   * @param pDatabaseType      the database type used for this statement
   * @param pSerializer        the value serializer
   * @param pIdColumnName      the name of the id column
   */
  public SingleSelect(IStatementExecutor<ResultSet> pStatementExecutor, AbstractSQLBuilder pBuilder, EDatabaseType pDatabaseType,
                      IValueSerializer pSerializer, String pIdColumnName, IColumnIdentification<VALUE> pColumnToSelect)
  {
    super(pStatementExecutor, pBuilder, pDatabaseType, pSerializer, pIdColumnName);
    column = pColumnToSelect;
    addColumns(column);
  }

  /**
   * The result of the first row.
   * The result is optional because there may not be a first row.
   *
   * @return a optional result of the query (first row)
   */
  public OptionalNullable<VALUE> firstResult()
  {
    return fullResult().getFirst();
  }

  /**
   * The full result of the query.
   * In this case this is a set of values of the field's data type for each result row.
   *
   * @return the result of the query
   */
  public SingleColumnResult<VALUE> fullResult()
  {
    if (column == null)
      throw new OJDatabaseException("A column must be selected to use a single select statement!");
    return new SingleColumnResult<>(column, doQuery());
  }
}
