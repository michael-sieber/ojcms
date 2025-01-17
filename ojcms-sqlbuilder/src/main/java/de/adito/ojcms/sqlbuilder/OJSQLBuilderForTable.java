package de.adito.ojcms.sqlbuilder;

import de.adito.ojcms.sqlbuilder.definition.*;
import de.adito.ojcms.sqlbuilder.definition.column.IColumnDefinition;
import de.adito.ojcms.sqlbuilder.statements.Create;
import de.adito.ojcms.sqlbuilder.util.DBConnectionInfo;
import de.adito.ojcms.utils.StringUtility;

import java.util.function.Consumer;

/**
 * A SQL statement builder for a single database table.
 * The database statements can be used and adapted in an functional way like the Java streams.
 * This builder can be configured via {@link OJSQLBuilderFactory}.
 *
 * @author Simon Danner, 15.05.2018
 */
public final class OJSQLBuilderForTable extends AbstractSQLBuilder
{
  private final String tableName;

  /**
   * Creates a new builder.
   *
   * @param pDatabaseType        the database type to use for this builder
   * @param pConnectionInfo      the database connection information
   * @param pCloseAfterStatement <tt>true</tt>, if the connection should be closed after executing one statement
   * @param pSerializer          the value serializer
   * @param pTableName           the name of the table to use for this builder
   * @param pIdColumnName        a global id column name for this builder instance
   */
  OJSQLBuilderForTable(EDatabaseType pDatabaseType, DBConnectionInfo pConnectionInfo, boolean pCloseAfterStatement,
                       IValueSerializer pSerializer, String pTableName, String pIdColumnName)
  {
    super(pDatabaseType, pConnectionInfo, pCloseAfterStatement, pSerializer, pIdColumnName);
    tableName = StringUtility.requireNotEmpty(pTableName, "table name");
  }

  /**
   * Drops the table of the builder.
   */
  public void dropTable()
  {
    boolean result = super.dropTable(tableName);
    if (!result)
      throw new IllegalStateException("The table " + tableName + " is not existing anymore!");
  }

  /**
   * Adds a column to the database table of this builder.
   *
   * @param pColumnDefinition information about the new column
   */
  public void addColumn(IColumnDefinition pColumnDefinition)
  {
    super.addColumn(tableName, pColumnDefinition);
  }

  /**
   * Removes a column from the database table of this builder.
   *
   * @param pColumn the column to remove
   */
  public void removeColumn(IColumnIdentification<?> pColumn)
  {
    super.removeColumn(tableName, pColumn);
  }

  /**
   * Checks, if the table of this builder exists in the database.
   *
   * @return <tt>true</tt>, if the table is existing
   */
  public boolean hasTable()
  {
    return super.hasTable(tableName);
  }

  /**
   * Executes a create statement, if the table of this builder is not existing in the database.
   *
   * @param pCreateStatement the create statement to execute (defined in a pipelining mechanism)
   */
  public void ifTableNotExistingCreate(Consumer<Create> pCreateStatement)
  {
    super.ifTableNotExistingCreate(tableName, pCreateStatement);
  }

  /**
   * The column count of the table of this builder.
   *
   * @return the number of columns of the database table
   */
  public int getColumnCount()
  {
    return super.getColumnCount(tableName);
  }

  /**
   * Determines, if a column is present.
   *
   * @param pColumnName the name of the column to check
   * @return <tt>true</tt> if the column is present
   */
  public boolean hasColumn(String pColumnName)
  {
    return super.hasColumn(tableName, pColumnName);
  }

  @Override
  protected <RESULT, STATEMENT extends AbstractBaseStatement<RESULT, STATEMENT>> STATEMENT configureStatementBeforeExecution(STATEMENT pStatement)
  {
    pStatement.setTableName(tableName);
    return pStatement;
  }
}
