<html>
  <head>
    <style>
      @font-face {
        font-family: 'GrowlyGrin';
        src:  url('assets/fonts/GrowlyGrin.ttf.woff') format('woff'),
        url('assets/fonts/GrowlyGrin.ttf.svg#GrowlyGrin') format('svg'),
        url('assets/fonts/GrowlyGrin.ttf.eot'),
        url('assets/fonts/GrowlyGrin.eot?#iefix') format('embedded-opentype');
        font-weight: normal;
        font-style: normal;
      }
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
        font-family: 'GrowlyGrin';
        font-size: 5.5rem;
        letter-spacing: 6px;
      }
      #scoreboard td {
        vertical-align: middle;
        text-align: center;
        border: 4px solid gray;
      }
      #scoreboard td.ball-hitter {
        width: 80px;
        font-size: 12rem;
        line-height: 0;
        color: rgb(14 164 150);
        font-family: system-ui;
      }
      #scoreboard td.player-name {
        text-align: left;
        padding-left: 30px;
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