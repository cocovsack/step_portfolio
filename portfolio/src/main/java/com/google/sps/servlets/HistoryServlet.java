// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Comment;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import static javax.swing.JOptionPane.showMessageDialog;


/** Servlet that returns some user comments. */
@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int numberParam = getNumberParams(request);
    if (numberParam == 0) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer greater than 1.");
      return;
    }

    String sortParam = request.getParameter("sort-param");

    Query query;
    if (sortParam.equals("timestamp")){
      query = new Query("Comment").addSort(sortParam, SortDirection.DESCENDING);
    }
    else{
      query = new Query("Comment").addSort(sortParam, SortDirection.ASCENDING);
    }
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      if (comments.size() == numberParam){
          break;
      }
      long id = entity.getKey().getId();
      String name = (String) entity.getProperty("name");
      String email = (String) entity.getProperty("email");
      String message = (String) entity.getProperty("message");
      long timestamp = (long) entity.getProperty("timestamp");
      double score;
      // Check for valid score and return 0 if none availble
      if (entity.getProperty("score") == null) {
        score = 0.0;
      }
      else {
        score = (double) entity.getProperty("score");
      }

      Comment comment = new Comment(id, name, email, message, timestamp, score);
      comments.add(comment);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().print(gson.toJson(comments));
  }

  /* Returns the number of comments to be displayed while ensuring it is in range*/
  private int getNumberParams(HttpServletRequest request) {
    // Get the number of comments input from the form.
    String stringNumberParam = request.getParameter("comment-number");
    int numberParam;
    try {
      numberParam = Integer.parseInt(stringNumberParam);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + stringNumberParam);
      return 0;
    }

    // Check that the input is above 1
    if (numberParam < 1) {
      System.err.println("Must display more than 1 comment");
      return 0;
    }

    return numberParam;
  }
}