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

package com.google.sps.data;

/** A comment class that contains information about each comment stored including its sentiment analysis score (ranging from -1 as negative to 1 as positive). */
public final class Comment {

  private final long id;
  private final String name;
  private final String email;
  private final String message;
  private final long timestamp;
  private final double score;
  private final boolean usercomment;

  public Comment(long id, String name, String email, String message, long timestamp, double score, boolean usercomment) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.message = message;
    this.timestamp = timestamp;
    this.score = score;
    this.usercomment = usercomment;
  }
}