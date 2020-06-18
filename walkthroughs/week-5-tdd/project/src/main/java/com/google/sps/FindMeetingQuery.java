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

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.ArrayList;


public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

  // Get attendees of the meeting
  Collection<String> attendees = request.getAttendees();
  long duration = request.getDuration();

  // Initialize empty arrays of already booked times and free times
  ArrayList<TimeRange> booked = new ArrayList<TimeRange>();
  ArrayList<TimeRange> mandatory = new ArrayList<TimeRange>();
  
  // Get list of the events those attendees already have scheduled
  for (Event event: events) {
    for (String attendee: attendees) {
      if (event.getAttendees().contains(attendee)) {
        booked.add(event.getWhen());
      }
    }
  }

  //Sort the list
  Collections.sort(booked, TimeRange.ORDER_BY_START);

  int currentTime = TimeRange.START_OF_DAY;
  for (TimeRange event: booked) {
    // Current time is not contained by a booked event and there is time for the event's duration
    if (!event.contains(currentTime) && event.start() - currentTime >= duration) {
      mandatory.add(TimeRange.fromStartEnd(currentTime, event.start(), false));
    }

    // Move current time pointer to the end of the booked event just processed
    if (event.end() > currentTime) {
      currentTime = event.end();
    }
  }

  // Check if there is a free slot after all booked times and before the end of day
  if (TimeRange.END_OF_DAY - currentTime >= duration) {
    mandatory.add(TimeRange.fromStartEnd(currentTime, TimeRange.END_OF_DAY, true));
  }

    Collection<String> optionalAttendees = request.getOptionalAttendees();
  //Rerun query on optional attendees
  if (optionalAttendees.size() > 0) {
    // Generate list of all attendees
    Collection<String> allAttendees = new ArrayList<String>();
    allAttendees.addAll(optionalAttendees);
    allAttendees.addAll(attendees);

    //Re-run query with everyone
    MeetingRequest requestOptional = new MeetingRequest(allAttendees, request.getDuration());
    Collection<TimeRange> optional = query(events, requestOptional);

    if (optional.isEmpty()) {
      return mandatory;
    } else {
      return optional;
    }
  }

  return mandatory;
  }
}
