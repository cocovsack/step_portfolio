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


/** Servlet that returns some example content. */
@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    int numberParam = getHistoryParameters(request);
    if (numberParam == -1) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer greater than 1.");
      return;
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    int numberParam = getHistoryParameters(request);
    if (numberParam == -1) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer greater than 1.");
      return;
    }

    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

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

      Comment comment = new Comment(id, name, email, message, timestamp);
      comments.add(comment);
    }

    Gson gson = new Gson();

    // response.sendRedirect("/history.html");
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  /* Returns the number of comments to be displayed while ensuring it is in range*/
  private int getHistoryParameters(HttpServletRequest request) {
    // Get the input from the form.
    String stringNumberParam = request.getParameter("comment-number");
    int numberParam;
    try {
      numberParam = Integer.parseInt(stringNumberParam);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + stringNumberParam);
      return -1;
    }

    // Check that the input is above 1
    if (numberParam < 1) {
      System.err.println("Must display more than 1 comment");
      return -1;
    }

    return numberParam;
  }
}