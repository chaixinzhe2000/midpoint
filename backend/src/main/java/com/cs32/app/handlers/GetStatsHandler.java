package com.cs32.app.handlers;

import com.cs32.app.Constants;
import com.cs32.app.database.Connection;
import com.cs32.app.exceptions.MissingDBObjectException;
import com.cs32.app.poll.AnswerOption;
import com.cs32.app.poll.Poll;
import com.cs32.app.poll.PollResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONException;
import spark.Request;
import spark.Response;
import spark.Route;

import org.json.JSONObject;

import java.util.*;

/**
 * The handler which is responsible for:
 * - sending the statistics for a poll to the frontend.
 */
public class GetStatsHandler implements Route {
  private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
      .create();

  @Override
  public Object handle(Request req, Response res) {
    Map<String, Object> variables = new HashMap<>();
    List<Map<String, Object>> stats = new ArrayList<>();
    Map<String, Map<String, Object>> statsMap = new HashMap();
    boolean status;
    try {
      JSONObject jsonReqObject = new JSONObject(req.body());
      String pollId = jsonReqObject.getString("pollId");

//      String userIdToken = jsonReqObject.getString("userIdToken");
//      FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(userIdToken);
//      String userId = decodedToken.getUid();
//      User user = Connection.getUserById(userId);

//      if (!user.getAnsweredPolls().getSet().contains(pollId)) {
//        status = false;
//      } else {
      String userMetaDataGrouping = jsonReqObject.getString("userMetaDataGrouping");
      Poll poll = Connection.getPollById(pollId);

      // create and set up sub-map for each answer option
      for (AnswerOption answerOption : poll.getAnswerOptions()) {
        Map<String, Object> mapForEachAnswerOption = new HashMap<>();
        mapForEachAnswerOption.put("answerOptionValue", answerOption.getValue());
        statsMap.put(answerOption.getId(), mapForEachAnswerOption);
        for (String eachGroup : Constants.USER_GROUPINGS.get(userMetaDataGrouping)) {
          mapForEachAnswerOption.put(eachGroup, 0);
        }
      }

      // calculate number of responders for each userMetaData grouping
      List<PollResponse> allPollResponses = Connection.getResponses(pollId);
      for (PollResponse pollResponse : allPollResponses) {
        String metaDataIdentity = pollResponse.getUserMetaData().get(userMetaDataGrouping);
        if (metaDataIdentity.equals("")) {
          metaDataIdentity = Constants.UNIDENTIFIED;
        }
        Map<String, Object> answerOptionMiniMap = statsMap.get(pollResponse.getAnswerOptionId());
        Integer curVal = (Integer) answerOptionMiniMap.get(metaDataIdentity);
        answerOptionMiniMap.put(metaDataIdentity, curVal + 1);
      }

      // convert statsMap to stats
      for (AnswerOption answerOption : poll.getAnswerOptions()) {
        Map<String, Object> answerOptionMiniMap = statsMap.get(answerOption.getId());
        stats.add(answerOptionMiniMap);
      }
      Map<String, Object> chartData = new HashMap<>();
      chartData.put("stats", stats);
      chartData.put("identities", Constants.USER_GROUPINGS.get(userMetaDataGrouping));

      // calculate strongly correlated with
      List<Map.Entry<String, Double>> categoriesRankedByCorrelation = new ArrayList<>(poll.getCatPts().getMap().entrySet());
      categoriesRankedByCorrelation.sort(Map.Entry.comparingByValue());
      Collections.reverse(categoriesRankedByCorrelation);
      List<String> rankedCategoriesToSend = new ArrayList<>();
      for (Map.Entry<String, Double> entry : categoriesRankedByCorrelation) {
        rankedCategoriesToSend.add(entry.getKey());
      }

      variables.put("chartData", chartData);
      variables.put("poll", poll);

      variables.put("categoriesRankedByCorrelation", rankedCategoriesToSend);

      // provide mini-stats
      // Initialize a map for counting the occurrence of every answer option
      Map<String, Double> counts = new HashMap<>();
      for (AnswerOption answerOption : poll.getAnswerOptions()) {
        counts.put(answerOption.getId(), 0.0);
      }

      // count the number of responses for each answer option
      for (PollResponse everyResponse : allPollResponses) {
        counts.put(everyResponse.getAnswerOptionId(),
              counts.get(everyResponse.getAnswerOptionId()) + 1);
      }

      // Send mini-stats to front end
      Map<String, Double> miniStats = new HashMap<>();
      int numResponses = allPollResponses.size();
      for (AnswerOption answerOption : poll.getAnswerOptions()) {
        double percentage;
        if (numResponses == 0) {
          percentage = 0;
        } else {
          percentage = counts.get(answerOption.getId()) / allPollResponses.size();
        }
        miniStats.put(answerOption.getId(), percentage * Constants.PERCENTAGE);
      }
      variables.put("miniStats", miniStats);

      status = true;
//      }

    } catch (JSONException e) {
      e.printStackTrace();
      System.err.println("ERROR: Incorrect JSON object formatting");
      status = false;
      // TODO: send the error message to the frontend
//    } catch (FirebaseAuthException e) {
//      e.printStackTrace();
//      status = false;
    } catch (MissingDBObjectException e) {
      e.printStackTrace();
      status = false;
    }
    variables.put("status", status);
    return GSON.toJson(variables);
  }
}
