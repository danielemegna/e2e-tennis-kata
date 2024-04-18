<html>
  <head>
    <style>
      body {
        padding: 0;
        margin: 0;
        display: flex;
        justify-content: center;
        align-items: center;
      }
      #background-image {
        position: fixed;
        width: 100%;
        height: 100%;
        transform: scale(1.1); /* hide blurred edged */
        z-index: -10;
        object-fit: cover;
        -webkit-filter: blur(14px) brightness(90%);
        filter: blur(14px) brightness(90%);
      }
      #center-box {
        filter: opacity(95%);
        background-color: rgb(11 23 42);
        width: 800px;
        height: 280px;
      }
      #scoreboard {
        width: 100%;
        height: 100%;
        border-collapse: collapse;
        color: lightgray;
        text-transform: uppercase;
        font-family: system-ui;
        font-size: 60px;
        font-weight: bold;
      }
      #scoreboard td {
        vertical-align: middle;
        text-align: center;
        border: 4px solid gray;
      }
      #scoreboard td.ball-hitter {
        width: 80px;
        font-size: 120px;
        line-height: 60px;
        color: rgb(14 164 150);
      }
      #scoreboard td.player-name {
        text-align: left;
      }
      #scoreboard td.current-set {
        background-color: rgb(14 164 150);
        width: 110px;
      }
      #scoreboard td.current-game {
        background-color: rgb(37 59 80);
        width: 120px;
      }
    </style>
  </head>
  <body>
    <img id="background-image" src="assets/background.png" />
    <div id="center-box">
      <#include "scoreboard.ftl">
    </div>
    <script src="https://unpkg.com/htmx.org@1.9.12" integrity="sha384-ujb1lZYygJmzgSwoxRggbCHcjc0rB2XoQrxeTUQyRjrOnlCoYta87iKBWq3EsdM2" crossorigin="anonymous"></script>
  </body>
</html>