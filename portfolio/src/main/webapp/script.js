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

function getLogin() {
  fetch('/login').then(response => response.json()).then((account) => {
    loginLink = document.getElementById("login-link");
    //If not logged in
    if(account.loggedIn == false) {
      loginLink.href = account.loginUrl;
      loginLink.innerText = "Login";
      document.getElementById("reminder-container").style.display ="block";
    } else {
      loginLink.href = account.loginUrl;
      loginLink.innerText = "Logout";
      document.getElementById("name").value = account.nickname;
      document.getElementById("email").value = account.email;
      document.getElementById("h2-container").style.display ="block";
      document.getElementById("h2-container").innerText ="Welcome " + account.nickname;
      document.getElementById("form-container").style.display ="block";

      
    }
  });
}

/* Gets past comment history from the DataServlet server and adds it to the DOM */
function getCommentHistory() {
  // Show the title
  const hiddenElement = document.getElementById('hidden');
  hiddenElement.style.display = 'block';

  // Fetch the comments
  const commentNumberElement = document.getElementById('comment-number');
  const sortParamElement = document.getElementById('sort-param');

  fetch('/history?comment-number=' + commentNumberElement.value + '&sort-param=' + sortParamElement.value)
      .then(response => response.json()).then((comments) => {
        const historyElement = document.getElementById('history-container');

        // Delete the old history list from the html tree if it exists
        const historyList = document.getElementsByClassName('history')[0];
        if (historyList !== undefined){
          while (historyList.firstChild) {
            historyList.removeChild(historyList.firstChild);
          }
          historyElement.removeChild(historyElement.firstChild);
        }
        
        // Make new history list
        var node = document.createElement('ul');
        node.className = 'history';
        historyElement.appendChild(node);
        comments.forEach((comment) => {
          node.appendChild(createHistoryElement(comment));
        })
  });
}

/** Creates an element that represents a task, including its delete button and tooltip box. */
function createHistoryElement(comment) {
  // Convert date
  var time = comment.timestamp;
  var date = new Date(time);
  time = date.toString();
  
  // Create element
  const historyElement = document.createElement('li');
  historyElement.className = 'comment';
  const titleElement = document.createElement('span');
  titleElement.innerText = "\"" + comment.message + "\" by " + comment.name + " on " + time;

  // Initialize tooltip
  const ttBox = document.createElement("div");
  ttBox.className = 'tooltip';
  ttBox.style.visibility = "hidden"; // make it hidden till mouse over

  const ttBoxText = document.createElement('span');
  ttBoxText.className = 'tooltip-text';
  ttBoxText.innerText = comment.score.toFixed(2);

  // Display sentiment analysis
  const sentimentButtonElement = document.createElement('button');
  sentimentButtonElement.className = 'sentiment-button';
  sentimentButtonElement.addEventListener('mouseover', () => {
    ttBox.style.visibility = 'visible';
    ttBoxText.style.visibility = 'visible';
    });
  sentimentButtonElement.addEventListener('mouseout', () => {
    ttBox.style.visibility = 'hidden';
    ttBoxText.style.visibility = 'hidden';
    });

  var newScore = (1 - Math.abs(comment.score)) * 255;

  if (comment.score >= 0.5) {
    sentimentButtonElement.innerText = 'Positive';
    newScore = newScore * 3;
    sentimentButtonElement.style.backgroundColor = "rgb(0, " + newScore + ", 0)";   
  }
  else if (comment.score >= 0) {
    sentimentButtonElement.innerText = 'Neutral';
    sentimentButtonElement.style.backgroundColor = "rgb(" + newScore + ", 255, 0)";   
  }
  else if (comment.score >= -0.5) {
    sentimentButtonElement.innerText = 'Neutral';
    sentimentButtonElement.style.backgroundColor = "rgb(255, " + newScore + ", 0)";   
  }
  else {
    sentimentButtonElement.innerText = 'Negative';
    newScore = newScore * 4;
    sentimentButtonElement.style.backgroundColor = "rgb(" + newScore + ", 0, 0)";   
  }


  historyElement.appendChild(titleElement);
  historyElement.appendChild(sentimentButtonElement);
  historyElement.appendChild(ttBox);
  ttBox.appendChild(ttBoxText);

  //If the comment is that of the current user, add a delete button
  if (comment.isAuthor == true) {
    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.innerText = 'Delete';
    deleteButtonElement.className = 'delete-button';
    deleteButtonElement.addEventListener('click', () => {
      deleteHistory(comment);
      historyElement.remove();
    });
    historyElement.appendChild(deleteButtonElement);
  }
  return historyElement;
}

/** Tells the server to delete the comment. */
function deleteHistory(comment) {
  const params = new URLSearchParams();
  params.append('id', comment.id);
  fetch('/delete-comment', {method: 'POST', body: params});
}

/* Prints and deletes rotating strings that self-describe */

/* Immediately after window loads, call function on array of strings and period */
window.onload = function() {
  var text_elements = document.getElementsByClassName('txt-rotate');
  for (var i = 0; i < text_elements.length; i++) {
    var toRotate = text_elements[i].getAttribute('data-rotate');
    var period = text_elements[i].getAttribute('data-period');
    if (toRotate != null) {
      new TxtRotate(text_elements[i], JSON.parse(toRotate), period);
    }
  }
}

/* Initialize object */
var TxtRotate = function(el, toRotate, period) {
  this.toRotate = toRotate;
  this.el = el;
  this.loopNum = 0;
  this.period = parseInt(period, 10) || 2000;
  this.txt = '';
  this.tick();
  this.isDeleting = false;
};

/* Print, wait the period, then delete */
TxtRotate.prototype.tick = function() {
  var i = this.loopNum % this.toRotate.length;
  var fullTxt = this.toRotate[i];

  if (this.isDeleting) {
    this.txt = fullTxt.substring(0, this.txt.length - 1);
  } else {
    this.txt = fullTxt.substring(0, this.txt.length + 1);
  }

  this.el.innerHTML = '<span class="wrap">' + this.txt + '</span>';

  var that = this;
  var delta = 300 - Math.random() * 100;

  if (this.isDeleting) { delta /= 2; }

  if (!this.isDeleting && this.txt === fullTxt) {
    delta = this.period;
    this.isDeleting = true;
  } else if (this.isDeleting && this.txt === '') {
    this.isDeleting = false;
    this.loopNum++;
    delta = 400;
  }

  setTimeout(function() {
    that.tick();
  }, delta);
}