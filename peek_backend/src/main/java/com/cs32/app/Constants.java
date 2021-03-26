package com.cs32.app;

public class Constants {
  public static final String[] ALL_CATEGORIES = {"politics", "sports", "funny", "culture", "entertainment", "food", "education", "serious", "relationships"};
  public static final Double INITIAL_NEW_POLL_CAT_PTS = 1000.0;
  public static final Integer ALGORITHM_RANDOM_POLL_BATCH_SZ = 70;
  public static final Double STARTING_CLICKRATE = 0.5;
  public static final Double UNTAGGED_CAT_STARTING_PTS = INITIAL_NEW_POLL_CAT_PTS/ALL_CATEGORIES.length;
  public static final int QUERY_RAND_POLLS_NUM_BATCH = 3;
}
