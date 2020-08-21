// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the 'License');
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an 'AS IS' BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

let chatOpen = false;

/**
 * Changes the status of the chat's open-ness
 */
function toggleChat() {
  if (chatOpen) {
    closeChat_();
  } else {
    openChat_();
  }
}

/**
 * @Private
 * Opens the chat
 */
function openChat_() {
  addClass_("myForm", "chatOpen");
  removeClass_("mapOverlay", "expanded");
  chatOpen =  true;
}

/**
 * @Private
 * Closes the chat
 */
function closeChat_() {
  removeClass_("myForm", "chatOpen");
  addClass_("mapOverlay", "expanded");
  chatOpen = false;
}

/**
 * @Private
 * Adds a classname to a DOM element with the given id
 * @param {String} id the id of the DOM element
 * @param {String} className the class name to be added
 */
function addClass_(id, className) {
  document.getElementById(id).classList.add(className);
}


/**
 * @Private
 * Removes a classname to a DOM element with the given id
 * @param {String} id the id of the DOM element
 * @param {String} className the class name to be removed
 */
function removeClass_(id, className) {
  document.getElementById(id).classList.remove(className);
}

/**
 * Loads chat
 * @throws Will throw an error if cannot get chat history associated with current user
 */
function loadChatHistory() {
  firebase.auth().currentUser.getIdToken(/* forceRefresh= */ true)
      .then(idToken => getChatHistory_(idToken))
      .catch(error => {
        throw "Problem getting chat history";
      });
}

/**
 * @Private
 * Retrieves chat history from the server and adds them to the page
 * @param{number} idToken The ID associated with the current user
 */
function getChatHistory_(idToken) {
  fetch(`/chat-server?idToken=${idToken}&idRoom=${roomId}`)
        .then(response => response.json())
        .then((comments) => {
          for (const index in comments) {
            let comment = comments[index];
            handleChatMessage(comment);
          }
        });
}

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// PROCESS CHAT MESSAGES

const MAP_TRIGGER = "/MAP";
const ADD_COMMAND = "ADD";
const HELP_COMMAND = "HELP";
const COMMAND_BODY = "Generated by map commands";

/**
 * Sends nonempty chat comment from text area to server
*/
function addChatComment() {
    var commentContent = document.getElementById('comment-container').value.trim();
    document.getElementById('comment-container').value = "";

    if(commentContent.indexOf("\n")!==0 && commentContent!==""){
      if(commentContent.split(" ")[0].toUpperCase() === MAP_TRIGGER) {
        handleMapCommand(commentContent);
      } else {
        var commentObj = {
          type : "MSG_SEND",
          message : commentContent
        };
  
        if (connection) {
            connection.send(JSON.stringify(commentObj));
        }
      }
    }
}

/**
 * @param{!Object} obj A JSON Object that represents the comment to be added
 * Adds comment to page
 */
function handleChatMessage(obj) {
  //TODO(astepin): Include timestamp in the message

  var node = document.createElement("div");
  var textnode = document.createTextNode(obj.name + ": " + obj.message);

  let userId = firebase.auth().currentUser.uid;
  if(obj.uid === userId) {
    node.classList.add("myMessage");
  } else {
    node.classList.add("otherMessage");
  }
  node.classList.add("message");

  node.appendChild(textnode);
  document.getElementById("past-comments").appendChild(node);
}

/**
 * Deals with all map commands written in the chat
 * @param {string} commentContent Represents the content of user's comment
 * @returns {!Object} Command to be executed
 */
function handleMapCommand(commentContent){
  var commentContentArr = commentContent.split(" ");
  var commentContentSize = commentContentArr.length;
  var commentAddress = commentContent.split(' ').slice(2).join(' ');

  if(commentContentSize > 1 && commentContentArr[1].toUpperCase() === ADD_COMMAND) {
    var position;

    if(!isNaN(parseFloat(commentContentArr[2])) && !isNaN(parseFloat(commentContentArr[3]))){
      position = new google.maps.LatLng(commentContentArr[2], commentContentArr[3]);
      myMap.sendPermMarkerInfo(position, commentAddress, COMMAND_BODY);
      myMap.panTo(position);
    } else {
      let commentContentUrl = commentAddress.replace(/ /g, "+");
      let addressUrl = `https://maps.googleapis.com/maps/api/geocode/json?address=${commentContentUrl}&key=AIzaSyDhchLLErkJukOoDeEbXfvtvYfntXh-z7I`;

      getCoordsFromAddress(addressUrl).then(position => {
        myMap.sendPermMarkerInfo(position, commentAddress, COMMAND_BODY);
        myMap.panTo(position);
      });
      console.log("this is position: " + position);
      
      //getCoordsFromAddress();
    }  
  } else if (commentContentSize > 1 && commentContentArr[1] === HELP_COMMAND){
    // TODO(astepin): Open list of commands
  } else {
    // TODO(astepin): Signal that command not understood, open help
  }
}

function getCoordsFromAddress(addressUrl) {
  return httpGetAsync(addressUrl).then(result => {
    var addressObj = JSON.parse(result);
    if(addressObj.status === "OK"){
      let lat = addressObj.results[0].geometry.location.lat;
      let lng = addressObj.results[0].geometry.location.lng;
      position = new google.maps.LatLng(lat, lng);
      return position;
      
    } else {
      // TODO(astepin): Signal address not understood, open help
    }
  });
}

/**
 * Asynchronously retrieves information about a given location
 * @param {string} contentUrl URL which provides location information 
 * @returns {string} JSON text representing all information about given location
 */
function httpGetAsync(contentUrl) {
  return new Promise(function(resolve){
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() { 
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
            resolve(xmlHttp.responseText);
    }
    xmlHttp.open("GET", contentUrl, true); 
    xmlHttp.send(null);
  })  
}
