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
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  public final class loginStats {

    private final boolean loggedIn;
    private final String loginUrl;
    private final String email;
    private final String nickname;

    public loginStats(boolean loggedIn, String loginUrl, String email, String nickname)
    {
      this.loggedIn = loggedIn;
      this.loginUrl = loginUrl;
      this.email = email;
      this.nickname = nickname;
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("applicaiton/json");
    UserService userService = UserServiceFactory.getUserService();
    Gson gson = new Gson();

    loginStats account;
    // If user is not logged in
    if (!userService.isUserLoggedIn()) {
      String url = userService.createLoginURL("/comment.html");
      account = new loginStats(false, url, null, null);
    }   
    // If user is logged in
    else {
      String nickname = userService.getCurrentUser().getNickname();
      String email = userService.getCurrentUser().getEmail();
      String url = userService.createLogoutURL("/index.html");
      account = new loginStats(true, url, email, nickname);
    }
    response.getWriter().println(gson.toJson(account));
  }
}
