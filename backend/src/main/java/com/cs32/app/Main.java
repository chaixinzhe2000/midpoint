package com.cs32.app;

import com.cs32.app.database.Connection;
import com.cs32.app.handlers.*;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.github.cdimascio.dotenv.Dotenv;
import spark.Spark;

import java.io.FileInputStream;
import java.io.IOException;

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

    // Connections class for MongoDB related queries
    Connection conn = new Connection();

    // Authentication using Firebase
    try {
      FileInputStream serviceAccount =
            new FileInputStream(System.getenv("PATH_TO_PRIVATE_KEY"));
      FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl(System.getenv("FIREBASE_DB_URL"))
            .build();
      FirebaseApp.initializeApp(options);
    } catch (IOException e) {
      e.printStackTrace();
    }


    Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
//    Spark.externalStaticFileLocation("src/main/resources/static");
//    Spark.exception(Exception.class, new ExceptionPrinter());
    // TODO: @Jacqueline: once Connection has been changed to non-static, we need to pass 'conn' into each handler
    Spark.get("/api/user/get-suggested", new GetSuggestedPollsHandler());
    Spark.post("/api/user/new", new NewUserHandler());
    Spark.post("/api/poll/new", new NewPollHandler());
    Spark.get("/api/poll/stats", new GetStatsHandler());
    Spark.post("/api/poll/anon-answer", new AnonymousAnswerHandler());
  }
}