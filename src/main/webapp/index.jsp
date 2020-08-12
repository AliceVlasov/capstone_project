<!DOCTYPE html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
    <head>
        <script src="https://cdn.firebase.com/libs/firebaseui/3.5.2/firebaseui.js"></script>
        <link type="text/css" rel="stylesheet" href="https://cdn.firebase.com/libs/firebaseui/3.5.2/firebaseui.css" />

        <!-- The core Firebase JS SDK is always required and must be listed first -->
        <script src="https://www.gstatic.com/firebasejs/7.17.1/firebase-app.js"></script>

        <!-- TODO: Add SDKs for Firebase products that you want to use
            https://firebase.google.com/docs/web/setup#available-libraries -->

        <script src="https://www.gstatic.com/firebasejs/7.17.1/firebase-auth.js"></script>

        <script src="authentication.js"></script>

        <link href='//fonts.googleapis.com/css?family=Marmelad' rel='stylesheet' type='text/css'>
        <link rel='stylesheet' type='text/css' href='welcomeStyle.css'>
        <title>CHAP</title>

    </head>
    <body>
        <div id="header">
          <h1>CHAP</h1>
          <h4 id="welcome-message"></h4>
        </div>

        <div id="pageContent">

          <div id="user-details" class="panel">
            <h2 id="profile" class="panel-header">Profile</h2>
          </div>

          <div id="firebaseui-auth-container"></div>

          <form id="username-form" onsubmit="submitUsername()"
                class="panel form">
            <label for="input-username" class="panel-header">My Username:
              </label>
            <textarea id="input-username" placeholder="Enter name...">
              </textarea>
            <input type="submit" value="Submit">
          </form>

          <form id="new-map-form" onsubmit="submitMap()" class="panel form">
            <label for="map-name" class="panel-header">New Map:</label>
            <textarea id="map-name" placeholder="Enter name..."></textarea>
            <input type="submit" value="Submit">
          </form>

          <div id="maps-wrapper" class="panel">
            <h2 id="my-maps" class="panel-header">My Maps</h2>
            <div id="user-maps"></div>
          </div>
        </div>

        <button id="sign-out" onclick="logOut()">Sign Out
            </button>
    </body>
</html>
