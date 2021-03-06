package com.cs32.app;

import com.cs32.app.database.Connection;

import com.cs32.app.handlers.AnonymousAnswerHandler;
import com.cs32.app.handlers.AnsweredPollsHandler;
import com.cs32.app.handlers.CheckOffHandler;
import com.cs32.app.handlers.CreatedPollsHandler;
import com.cs32.app.handlers.DeletePollHandler;
import com.cs32.app.handlers.GetGamePollHandler;
import com.cs32.app.handlers.GetStatsHandler;
import com.cs32.app.handlers.GetSuggestedPollsHandler;
import com.cs32.app.handlers.NewPollHandler;
import com.cs32.app.handlers.NewUserHandler;
import com.cs32.app.handlers.SearchPollsHandler;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import spark.Spark;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Main {

  private static final int DEFAULT_PORT = Integer.parseInt(System.getenv("PORT"));
  private final String[] args;

  public Main(String[] args) {
    this.args = args;
  }

  public void run() {
    this.runSparkServer(DEFAULT_PORT);
  }


  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");

      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }
      return "OK";
    });
    Spark.before((request, response) -> {
      response.header("Access-Control-Allow-Origin", "*");
      response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
      response.header("Access-Control-Allow-Headers",
          "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
    });
    // TODO: Change true back to false when finish populating the database for testing
    // Connections class for MongoDB related queries
    Connection conn = new Connection(false);

    // Authentication using Firebase
    try {
      URLConnection connection = new URL("https://www.midpoint.fun/firebase_config.json").openConnection();
      InputStream response = connection.getInputStream();

      FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(response))
            .setDatabaseUrl(System.getenv("FIREBASE_DB_URL"))
            .build();
      FirebaseApp.initializeApp(options);
    } catch (IOException e) {
      e.printStackTrace();
    }

//    Spark.externalStaticFileLocation("src/main/resources/static");
//    Spark.exception(Exception.class, new ExceptionPrinter());
    Spark.post("/user/get-suggested", new GetSuggestedPollsHandler());
    Spark.post("/user/new", new NewUserHandler());
    Spark.post("/poll/new", new NewPollHandler());
    Spark.post("/poll/stats", new GetStatsHandler());
    Spark.post("/poll/anon-answer", new AnonymousAnswerHandler());
    Spark.post("/game/new-poll", new GetGamePollHandler());
    Spark.post("/poll/check-off", new CheckOffHandler());
    Spark.post("/poll/search", new SearchPollsHandler());
    Spark.post("/user/answered-polls", new AnsweredPollsHandler());
    Spark.post("/user/created-polls", new CreatedPollsHandler());
    Spark.post("/poll/delete", new DeletePollHandler());
  }
}
