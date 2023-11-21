package repository.impl;

import java.util.function.Function;
import java.util.stream.Stream;

class SQLQueryBuilder {

    private StringBuilder builder;

    public SQLQueryBuilder() {
        this.builder = new StringBuilder();
    }

    public SQLQueryBuilder(int capacity) {
        this.builder = new StringBuilder(capacity);
    }

    public String getQuery() {
        String query = builder.toString();
        builder = new StringBuilder(builder.capacity());
        return query;
    }

    public SQLQueryBuilder selectAll(String table) {
        String query = String.format("SELECT * FROM `%s` ", table);
        builder.append(query);
        return this;
    }

    public SQLQueryBuilder selectById(String table, String id) {
        String query = String.format("SELECT * FROM `%s` WHERE `id` = '%s' ", table, id);
        builder.append(query);
        return this;
    }

    public SQLQueryBuilder delete(String table) {
        String query = String.format("DELETE FROM `%s` ", table);
        builder.append(query);
        return this;
    }


    public SQLQueryBuilder insertInto(String table, String... cAndVPairs) {
        checkArgsPairs(cAndVPairs);
        String columns = mapPairs(cAndVPairs, SQLQueryBuilder::columnQuotes, 0, 2);
        String values = mapPairs(cAndVPairs, SQLQueryBuilder::valueQuotes, 1, 2);
        String query = String.format("INSERT INTO `%s`(%s) values(%s) ", table, columns, values);
        builder.append(query);
        return this;
    }

    public SQLQueryBuilder update(String table, String... cAndVPairs) {
        int pairsCount = cAndVPairs.length >> 1;
        String[] pairs = new String[pairsCount];
        String currentPair = "";
        for (int i = 0; i < pairsCount; i++) {
            String column = columnQuotes(cAndVPairs[i << 1]);
            String value = valueQuotes(cAndVPairs[(i << 1) + 1]);
            pairs[i] = column + " = " + value;
        }
        String joinedPairs = String.join(", ", pairs);
        String query = String.format("UPDATE `%s` SET %s ", table, joinedPairs);
        builder.append(query);
        return this;
    }

    private static String mapPairs(String[] pairs, Function<String, String> mapper, int startIndex, int step) {
        Stream<String> stream =
                Stream.iterate(startIndex, i -> i + step)
                        .limit(pairs.length >> 1)
                        .map(i -> pairs[i]);
        return stream
                .map(mapper)
                .reduce((s1, s2) -> String.join(", ", s1, s2))
                .get();
    }

    public SQLQueryBuilder join(String joinType, String table1, String table1Col, String table2, String table2Col) {
        builder.append(String.format("%s JOIN `%s` ON %s.%s = %s.%s ", joinType, table1, table1, table1Col, table2, table2Col));
        return this;
    }

    public SQLQueryBuilder where(String column, String value) {
        builder.append(String.format("WHERE %s = '%s' ", column, value));
        return this;
    }

    public SQLQueryBuilder and(String column, String value) {
        builder.append(String.format("AND %s = '%s'", column, value));
        return this;
    }

    public SQLQueryBuilder endQuery() {
        builder.append("; ");
        return this;
    }

    private static String generateValues(int length) {
        return Stream.generate(() -> "?")
                .limit(length)
                .reduce((q1, q2) -> String.join(", ", q1, q2))
                .get();
    }

    private static String columnQuotes(String column) {
        return '`' + column + '`';
    }

    private static String valueQuotes(String value) {
        return '\'' + value + '\'';
    }

    private static void checkArgsPairs(String[] pairs) {
        if ((pairs.length & 1) == 1) {
            throw new IllegalArgumentException("must be even arguments count");
        }
        if (pairs.length < 2) {
            throw new IllegalArgumentException("must be at least one arguments pair");
        }
    }
}
