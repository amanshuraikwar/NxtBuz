package io.github.amanshuraikwar.howmuch.data.transaction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Note:
 *      1. Sheet cell range format: <sheet_title>!<start_col><start_row_num>:<end_col>
 *
 * @author amanshuraikwar
 */
public interface Constants {

    //region metadata

    String METADATA_SHEET_TITLE = "Metadata";

    //region Categories

    //   |B|C|
    // |2|x|
    // |3|x|x|
    // |-|-|-|
    // |4|x|x|

    String DEFAULT_MONTHLY_LIMIT = "1000.00";

    List<List<String>> DEFAULT_CATEGORIES_WITH_HEADING = Arrays.asList(
            Collections.singletonList("Categories"),// 2
                          // B              // C
            Arrays.asList("Name",           DEFAULT_MONTHLY_LIMIT),// 3
            Arrays.asList("Food",           DEFAULT_MONTHLY_LIMIT), // 4
            Arrays.asList("Health/Medical", DEFAULT_MONTHLY_LIMIT),
            Arrays.asList("Home",           DEFAULT_MONTHLY_LIMIT),
            Arrays.asList("Transportation", DEFAULT_MONTHLY_LIMIT),
            Arrays.asList("Personal",       DEFAULT_MONTHLY_LIMIT),
            Arrays.asList("Utilities",      DEFAULT_MONTHLY_LIMIT),
            Arrays.asList("Travel",         DEFAULT_MONTHLY_LIMIT)
    );

    int CATEGORIES_START_ROW_WITH_HEADING = 2;
    int CATEGORIES_START_ROW_WITHOUT_HEADING = CATEGORIES_START_ROW_WITH_HEADING + 2;

    String CATEGORIES_START_COL = "B";
    String CATEGORIES_END_COL = "C";

    String CATEGORIES_CELL_RANGE_WITH_HEADING =
            CATEGORIES_START_COL + CATEGORIES_START_ROW_WITH_HEADING + ":" + CATEGORIES_END_COL;

    String CATEGORIES_CELL_RANGE_WITHOUT_HEADING =
            CATEGORIES_START_COL + CATEGORIES_START_ROW_WITHOUT_HEADING + ":" + CATEGORIES_END_COL;

    String CATEGORIES_SPREAD_SHEET_RANGE_WITH_HEADING =
            METADATA_SHEET_TITLE + "!" + CATEGORIES_CELL_RANGE_WITH_HEADING;

    String CATEGORIES_SPREAD_SHEET_RANGE_WITHOUT_HEADING =
            METADATA_SHEET_TITLE + "!" + CATEGORIES_CELL_RANGE_WITHOUT_HEADING;

    //endregion

    //endregion

    //region Transactions

    String TRANSACTIONS_SHEET_TITLE = "Transactions-1";

    List<List<String>> TRANSACTIONS_HEADING = Arrays.asList(
            Collections.singletonList("Transactions"),
            Arrays.asList(
                    "DateTime",
                    "Amount",
                    "Title",
                    "CategoryId"
            )
    );

    int TRANSACTION_START_ROW_WITH_HEADING = 2;
    int TRANSACTION_START_ROW_WITHOUT_HEADING = TRANSACTION_START_ROW_WITH_HEADING + 2;

    String TRANSACTION_START_COL = "B";
    String TRANSACTION_END_COL = "E";

    int TRANSACTION_ROW_COLUMN_COUNT = TRANSACTIONS_HEADING.get(1).size();

    String TRANSACTIONS_CELL_RANGE_WITH_HEADING =
            TRANSACTION_START_COL + TRANSACTION_START_ROW_WITH_HEADING + ":" + TRANSACTION_END_COL;

    String TRANSACTIONS_CELL_RANGE_WITHOUT_HEADING =
            TRANSACTION_START_COL + TRANSACTION_START_ROW_WITHOUT_HEADING + ":" + TRANSACTION_END_COL;

    //endregion
}
