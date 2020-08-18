<!DOCTYPE html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
    <head>
        <script src="https://cdn.firebase.com/libs/firebaseui/3.5.2/firebaseui.js"></script>
        <link type="text/css" rel="stylesheet" href="https://cdn.firebase.com/libs/firebaseui/3.5.2/firebaseui.css" />

        <!-- The core Firebase JS SDK is always required and must be listed first -->
        <script src="https://www.gstatic.com/firebasejs/7.17.1/firebase-app.js"></script>

        <script src="https://www.gstatic.com/firebasejs/7.17.1/firebase-auth.js"></script>

        <script src="welcome.js"></script>

        <link href='//fonts.googleapis.com/css?family=Marmelad' rel='stylesheet' type='text/css'>
        <link rel='stylesheet' type='text/css' href='welcomeStyle.css'>
        <title>CHAP</title>

    </head>
    <body>
        <div id="panel">
          <div id="nav">
            <button class="nav-btn show" id="profile-btn" onclick="showUserDetails()"></button>
            <button class="nav-btn" id="maps-btn" onclick="showUserMaps()"></button>
          </div>

          <div id="maps-wrapper" class="panel-content">
            <h2 id="my-maps" class="panel-header">My Maps</h2>
            <input type="button" id="add-map" class="panel-icon" onclick="showMapForm()">
            <input type="submit" id="submit-map" class="panel-icon" onclick="submitMap()" value="">
            <div id="user-maps">
              <button id="map-form-wrapper" class="roomBtn">
                <textarea id="new-map-form" placeholder="Name..."></textarea>
              </button>
            </div>
          </div>

          <div id="user-details" class="panel-content">
            <h2 id="profile" class="panel-header">Profile</h2>
            <input type="button" id="edit-details" class="panel-icon" onclick="showUsernameForm()" value="">
            <input type="submit" id="save-details" class="panel-icon" onclick="submitUsername()" value="">
          </div>
        </div>

        <div id="header">
          <h1>CHAP</h1>
          <h4 id="welcome-message"></h4>
        </div>

        <div id="loading">Loading...</div>
        <div id="firebaseui-auth-container"></div>

        <button id="sign-out" onclick="logOut()">Sign Out
            </button>
    </body>
</html>
