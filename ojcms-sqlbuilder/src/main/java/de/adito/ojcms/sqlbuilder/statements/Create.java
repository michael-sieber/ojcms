package de.adito.ojcms.sqlbuilder.statements;

import de.adito.ojcms.sqlbuilder.*;
import de.adito.ojcms.sqlbuilder.definition.*;
import de.adito.ojcms.sqlbuilder.definition.column.*;
import de.adito.ojcms.sqlbuilder.format.*;
import de.adito.ojcms.sqlbuilder.util.OJDatabaseException;

import java.util.*;
import java.util.stream.Collectors;

import static de.adito.ojcms.sqlbuilder.format.EFormatConstant.*;
import static de.adito.ojcms.sqlbuilder.format.EFormatter.*;
import static de.adito.ojcms.sqlbuilder.format.ESeparator.*;

/**
 * A create statement.
 *
 * @author Simon Danner, 26.04.2018
 */
public class Create extends AbstractBaseStatement<Void, Create>
{
  private final IColumnDefinition idColumnDefinition;
  private final List<IColumnDefinition> columns = new ArrayList<>();

  /**
   * Creates the create statement.
   *
   * @param pStatementExecutor the executor for this statement
   * @param pBuilder           the builder that created this statement to use other kinds of statements for a concrete statement
   * @param pDatabaseType      the database type used for this statement
   * @param pSerializer        the value serializer
   * @param pIdColumnName      the name of the id column for this table
   */
  public Create(IStatementExecutor<Void> pStatementExecutor, AbstractSQLBuilder pBuilder, EDatabaseType pDatabaseType,
                IValueSerializer pSerializer, String pIdColumnName)
  {
    super(pStatementExecutor, pBuilder, pDatabaseType, pSerializer, pIdColumnName);
    idColumnDefinition = IColumnDefinition.of(pIdColumnName.toUpperCase(),
                                              EColumnType.INT.create().primaryKey().modifiers(EColumnModifier.NOT_NULL));
  }

  /**
   * Sets the table name for this create statement.
   *
   * @param pTableName the table name
   * @return the create statement itself to enable a pipelining mechanism
   */
  public Create tableName(String pTableName)
  {
    return super.setTableName(pTableName);
  }

  /**
   * Determines the columns to create.
   *
   * @param pColumnDefinitions the column definitions to create
   * @return the create statement itself to enable a pipelining mechanism
   */
  public Create columns(IColumnDefinition... pColumnDefinitions)
  {
    if (pColumnDefinitions == null || pColumnDefinitions.length == 0)
      throw new OJDatabaseException("The columns to create cannot be empty!");
    columns.addAll(Arrays.asList(pColumnDefinitions));
    return this;
  }

  /**
   * Configures the create statement to include an id column additionally.
   *
   * @return the create statement itself to enable a pipelining mechanism
   */
  public Create withIdColumn()
  {
    columns.add(0, idColumnDefinition);
    return this;
  }

  /**
   * Executes the statement and creates the table in the database.
   */
  public void create()
  {
    if (columns.isEmpty())
      throw new OJDatabaseException("At least one column must be defined to create a table!");

    final StatementFormatter statement = CREATE.create(databaseType, idColumnDefinition.getColumnName())
        .appendTableName(getTableName())
        .openBracket()
        .appendMultiple(columns.stream(), COMMA, NEW_LINE)
        .appendFunctional(this::_primaryKeys)
        .appendFunctional(this::_foreignKeys)
        .closeBracket();
    executeStatement(statement);
  }

  /**
   * Adds a primary key to the statement format, if there are columns marked as primary keys.
   *
   * @param pFormatter the formatter for the statement
   */
  private void _primaryKeys(StatementFormatter pFormatter)
  {
    final List<String> primaryKeyColumnNames = columns.stream()
        .filter(pColumn -> pColumn.getColumnType().isPrimaryKey())
        .map(pColumnDefinition -> pColumnDefinition.getColumnName().toUpperCase())
        .collect(Collectors.toList());
    if (primaryKeyColumnNames.isEmpty())
      return;
    pFormatter.appendSeparator(COMMA, NEW_LINE);
    pFormatter.appendConstant(PRIMARY_KEY, String.join(", ", primaryKeyColumnNames));
  }

  /**
   * Adds foreign key constraints to the statement format, if present.
   * The referenced database tables of the foreign keys may be created, if necessary.
   *
   * @param pFormatter the formatter for the statement
   */
  private void _foreignKeys(StatementFormatter pFormatter)
  {
    //noinspection OptionalGetWithoutIsPresent
    final Map<String, IForeignKey> foreignKeyMapping = columns.stream()
        .filter(pColumn -> pColumn.getColumnType().getForeignKey().isPresent())
        .collect(Collectors.toMap(IColumnDefinition::getColumnName, pColumn -> pColumn.getColumnType().getForeignKey().get()));
    if (foreignKeyMapping.isEmpty())
      return;
    final OJSQLBuilder tableChecker = OJSQLBuilderFactory.newSQLBuilder(builder).create();
    foreignKeyMapping.forEach((pColumn, pReference) -> {
      if (!tableChecker.hasTable(pReference.getTableName()))
        pReference.createReferencedTable(tableChecker.getConnectionInfo()); //Create referenced table, if necessary
      pFormatter.appendSeparator(COMMA, NEW_LINE);
      pFormatter.appendConstant(FOREIGN_KEY, pColumn, pReference.getTableName(),
                                String.join(", ", pReference.getColumnNames()));
    });
  }
}
