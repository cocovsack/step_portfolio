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

/**
 * Gets past comment history from the DataServlet server and adds it to the DOM
 */
function getCommentHistory() {
    fetch('/history?comment-number=' + document.getElementById('comment-number').value).then(response => response.json()).then((comments) => {
    console.log(comments);
    const historyElement = document.getElementById('history-container');
    
    // Delete the old history list if it exists
    const historyList = document.getElementsByClassName('history')[0];
    console.log(historyList);
    if (historyList !== undefined){
      while (historyList.firstChild) {
        historyList.removeChild(historyList.firstChild);
      }
      historyElement.removeChild(historyElement.firstChild);
    }

    // Make new history list
    var node = document.createElement("ul");
    node.className = 'history';
    historyElement.appendChild(node);
    comments.forEach((comment) => {
      node.appendChild(createHistoryElement(comment));
    })
  });
}

/** Creates an element that represents a task, including its delete button. */
function createHistoryElement(comment) {
  const historyElement = document.createElement('li');
  historyElement.className = 'comment';

  const titleElement = document.createElement('span');
  titleElement.innerText = "* " + comment.message + " by " + comment.name + " on " + comment.timestamp;

//   const deleteButtonElement = document.createElement('button');
//   deleteButtonElement.innerText = 'Delete';
//   deleteButtonElement.addEventListener('click', () => {
//     deleteHistory(comment);

//     // Remove the task from the DOM.
//     historyElement.remove();
//  });

  historyElement.appendChild(titleElement);
  //taskElement.appendChild(deleteButtonElement);
  return historyElement;
}

// /** Tells the server to delete the task. */
// function deleteTask(task) {
//   const params = new URLSearchParams();
//   params.append('id', task.id);
//   fetch('/delete-task', {method: 'POST', body: params});
// }

//     messages.forEach((line) => {
//       historyElement.appendChild(createListElement(line));
//     });
//   });
// }

// /** Creates an <li> element containing text. */
// function createListElement(text) {
//   const liElement = document.createElement('li');
//   liElement.innerText = text;
//   return liElement;
// }

/**
 * Prints and deletes rotating strings that self-describe
 */

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