package de.adito.ojcms.sqlbuilder;

import de.adito.ojcms.sqlbuilder.definition.*;
import de.adito.ojcms.sqlbuilder.format.StatementFormatter;
import de.adito.ojcms.utils.StringUtility;

import java.io.IOException;

/**
 * Abstract base class for every database statement.
 * It mainly provides a method the execute the statements finally.
 * For this purpose a {@link IStatementExecutor} is necessary to send statements to the database.
 * Furthermore the table name to execute the statements at and the database type are also stored here.
 *
 * @param <RESULT>    the generic result type of the executor (e.g. {@link java.sql.ResultSet}
 * @param <STATEMENT> the final concrete type of this statement
 * @author Simon Danner, 26.04.2018
 */
public abstract class AbstractBaseStatement<RESULT, STATEMENT extends AbstractBaseStatement<RESULT, STATEMENT>> implements IStatement
{
  private final IStatementExecutor<RESULT> executor;
  protected final AbstractSQLBuilder builder;
  protected final EDatabaseType databaseType;
  protected final IValueSerializer serializer;
  protected final IColumnIdentification<Integer> idColumnIdentification;
  private String tableName;

  /**
   * Creates the base statement.
   *
   * @param pExecutor     the executor for the statements
   * @param pBuilder      the builder that created this statement to use other kinds of statements for a concrete statement
   * @param pDatabaseType the database type used for this statement
   * @param pSerializer   the value serializer
   * @param pIdColumnName the name of the global id column
   */
  protected AbstractBaseStatement(IStatementExecutor<RESULT> pExecutor, AbstractSQLBuilder pBuilder, EDatabaseType pDatabaseType,
                                  IValueSerializer pSerializer, String pIdColumnName)
  {
    executor = pExecutor;
    builder = pBuilder;
    databaseType = pDatabaseType;
    serializer = pSerializer;
    idColumnIdentification = IColumnIdentification.of(pIdColumnName.toUpperCase(), Integer.class);
  }

  /**
   * Executes a SQL statement.
   *
   * @param pFormat the statement defined through a formatter
   * @return the result of the execution
   */
  protected RESULT executeStatement(StatementFormatter pFormat)
  {
    return executor.executeStatement(pFormat.getStatement(), pFormat.getSerialArguments(serializer));
  }

  /**
   * The table name to use for this statement.
   *
   * @return a table name of the database
   */
  protected String getTableName()
  {
    return StringUtility.requireNotEmpty(tableName, "table name");
  }

  /**
   * Sets the table name for this statement.
   *
   * @param pTableName the table name
   * @return the statement itself
   */
  protected STATEMENT setTableName(String pTableName)
  {
    tableName = pTableName.toUpperCase();
    //noinspection unchecked
    return (STATEMENT) this;
  }

  /**
   * Determines, if the table the statement is based on has an id column.
   *
   * @return <tt>true</tt> if the id column is present
   */
  protected boolean isIdColumnPresent()
  {
    return builder.hasColumn(getTableName(), idColumnIdentification.getColumnName());
  }

  @Override
  public void close() throws IOException
  {
    executor.close();
  }
}
