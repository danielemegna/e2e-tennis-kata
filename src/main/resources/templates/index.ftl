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
    <img id="background-image" src="background.png" />
    <div id="center-box">
      <table id="scoreboard">
        <tr>
          <td class="ball-hitter">&centerdot;</td>
          <td class="player-name">sinner</td>
          <td class="current-set">3</td>
          <td class="current-game">15</td>
        </tr>
        <tr>
          <td class="ball-hitter"></td>
          <td class="player-name">djokovic</td>
          <td class="current-set">2</td>
          <td class="current-game">40</td>
        </tr>
      </table>
    </div>
  </body>
</html>