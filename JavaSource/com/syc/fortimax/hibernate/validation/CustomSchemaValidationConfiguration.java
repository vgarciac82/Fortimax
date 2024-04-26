
package com.syc.fortimax.hibernate.validation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.Mapping;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.IdentifierGeneratorAggregator;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.IdentifierCollection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.ColumnMetadata;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.TableMetadata;
import org.hibernate.type.Type;

/**
 * This class overrides the default implementation of
 * { #validateSchema(dialect, databaseMetadata) validateSchema} method.
 * Basically, instead of validating the schema and throwing an exception when
 * first error is found, this class gathers all the errors and prints them into
 * the log in WARN level. This implementation allows the application to run
 * anyway if any violation is found (the same way if 'hibernate.hbm2ddl.auto'
 * validation is disabled). This is very useful to know how many violations
 * existed at first glance, if we are facing just a few or a lot of them. <br>
 * <br>
 * Note: All code is borrowed from original Configuration class. We had to
 * extend from deprecated AnnotationConfiguration class due to legacy reasons
 * (we are using AnnotationSessionFactoryBean as a session factory and only
 * accepts an AnnotationConfiguration as a configurationClass). Also remember
 * that this validation is just triggered when hibernate.hbm2ddl.auto is set to
 * 'validate'.
 */
public class CustomSchemaValidationConfiguration extends
        Configuration {

    private static final long serialVersionUID = 3678466532340192490L;

    private static final Logger LOGGER = Logger
        .getLogger(CustomSchemaValidationConfiguration.class);

    private final Mapping mapping = buildMapping();

    private final List<Exception> schemaViolations =
        new ArrayList<Exception>();

    @Override
    public void validateSchema(final Dialect dialect,
            final DatabaseMetadata databaseMetadata)
            throws HibernateException {
        secondPassCompile();

        String defaultCatalog =
            getProperties().getProperty(Environment.DEFAULT_CATALOG);
        String defaultSchema =
            getProperties().getProperty(Environment.DEFAULT_SCHEMA);

        Iterator<Table> iterTables = getTableMappings();
        while (iterTables.hasNext()) {
            Table table = iterTables.next();
            if (table.isPhysicalTable()) {

                TableMetadata tableInfo =
                    databaseMetadata.getTableMetadata(table.getName(),
                        (table.getSchema() == null) ? defaultSchema
                                : table.getSchema(),
                        (table.getCatalog() == null) ? defaultCatalog
                                : table.getCatalog(), table.isQuoted());
                if (tableInfo == null) {
                    Exception e =
                        new HibernateException("Missing table: "
                            + table.getName());
                    schemaViolations.add(e);
                } else {
                    try {
                        validateColumns(table, dialect, tableInfo);
                    } catch (HibernateException e) {
                        schemaViolations.add(e);
                    }
                }
            }
        }

        Iterator<IdentifierGenerator> iterGenerators =
            iterateGenerators(dialect);
        while (iterGenerators.hasNext()) {
            PersistentIdentifierGenerator generator =
                (PersistentIdentifierGenerator) iterGenerators.next();
            Object key = generator.generatorKey();
            if (!databaseMetadata.isSequence(key)
                && !databaseMetadata.isTable(key)) {
                Exception e =
                    new HibernateException("Missing sequence or table: "
                        + key);
                schemaViolations.add(e);
            }
        }

        if (!schemaViolations.isEmpty()) {
            logViolations();
        }
    }

    private void logViolations() {
        LOGGER.warn("There were discrepancies (" + schemaViolations.size()
            + ") between database and entity mappings.");
        for (Exception e : schemaViolations) {
            LOGGER.warn(e);
        }
    }

    private void validateColumns(final Table table, final Dialect dialect,
            final TableMetadata tableInfo) {
        Iterator<?> iter = table.getColumnIterator();
        while (iter.hasNext()) {
            Column col = (Column) iter.next();

            ColumnMetadata columnInfo =
                tableInfo.getColumnMetadata(col.getName());

            if (columnInfo == null) {
                Exception e =
                    new HibernateException("Missing column: "
                        + col.getName()
                        + " in "
                        + Table.qualify(tableInfo.getCatalog(),
                            tableInfo.getSchema(), tableInfo.getName()));
                schemaViolations.add(e);
            } else {
                final boolean typesMatch =
                    col.getSqlType(dialect, mapping)
                        .toLowerCase()
                        .startsWith(columnInfo.getTypeName().toLowerCase())
                        || columnInfo.getTypeCode() == col
                            .getSqlTypeCode(mapping);
                if (!typesMatch) {
                    Exception e =
                        new HibernateException(
                            "Wrong column type in "
                                + Table.qualify(tableInfo.getCatalog(),
                                    tableInfo.getSchema(),
                                    tableInfo.getName()) + " for column "
                                + col.getName() + ". Found: "
                                + columnInfo.getTypeName().toLowerCase()
                                + ", expected: "
                                + col.getSqlType(dialect, mapping));
                    schemaViolations.add(e);
                }
            }
        }

    }

    @Override
    public Mapping buildMapping() {
        return new Mapping() {
            public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
                return getIdentifierGeneratorFactory();
            }

            /**
             * Returns the identifier type of a mapped class
             */
            public Type getIdentifierType(final String entityName)
                    throws MappingException {
                PersistentClass pc = classes.get(entityName);
                if (pc == null) {
                    throw new MappingException(
                        "persistent class not known: " + entityName);
                }
                return pc.getIdentifier().getType();
            }

            public String getIdentifierPropertyName(final String entityName)
                    throws MappingException {
                final PersistentClass pc = classes.get(entityName);
                if (pc == null) {
                    throw new MappingException(
                        "persistent class not known: " + entityName);
                }
                if (!pc.hasIdentifierProperty()) {
                    return null;
                }
                return pc.getIdentifierProperty().getName();
            }

            public Type getReferencedPropertyType(final String entityName,
                    final String propertyName) throws MappingException {
                final PersistentClass pc = classes.get(entityName);
                if (pc == null) {
                    throw new MappingException(
                        "persistent class not known: " + entityName);
                }
                Property prop = pc.getReferencedProperty(propertyName);
                if (prop == null) {
                    throw new MappingException("property not known: "
                        + entityName + '.' + propertyName);
                }
                return prop.getType();
            }
        };
    }

    private Iterator<IdentifierGenerator> iterateGenerators(
            final Dialect dialect) throws MappingException {

        TreeMap<Object, IdentifierGenerator> generators =
            new TreeMap<Object, IdentifierGenerator>();
        String defaultCatalog =
            getProperties().getProperty(Environment.DEFAULT_CATALOG);
        String defaultSchema =
            getProperties().getProperty(Environment.DEFAULT_SCHEMA);

        for (PersistentClass pc : classes.values()) {
            if (!pc.isInherited()) {
                IdentifierGenerator ig =
                    pc.getIdentifier().createIdentifierGenerator(
                        getIdentifierGeneratorFactory(), dialect,
                        defaultCatalog, defaultSchema, (RootClass) pc);

                if (ig instanceof PersistentIdentifierGenerator) {
                    generators.put(((PersistentIdentifierGenerator) ig)
                        .generatorKey(), ig);
                } else if (ig instanceof IdentifierGeneratorAggregator) {
                    ((IdentifierGeneratorAggregator) ig)
                        .registerPersistentGenerators(generators);
                }
            }
        }

        for (Collection collection : collections.values()) {
            if (collection.isIdentified()) {
                IdentifierGenerator ig =
                    ((IdentifierCollection) collection).getIdentifier()
                        .createIdentifierGenerator(
                            getIdentifierGeneratorFactory(), dialect,
                            defaultCatalog, defaultSchema, null);

                if (ig instanceof PersistentIdentifierGenerator) {
                    generators.put(((PersistentIdentifierGenerator) ig)
                        .generatorKey(), ig);
                }
            }
        }

        return generators.values().iterator();
    }

    public List<Exception> getSchemaViolations() {
        return schemaViolations;
    }

}