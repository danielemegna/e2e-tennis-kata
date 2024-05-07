<table id="scoreboard">
  <tr>
    <td class="ball-hitter"><#if isFirstPlayerServing()>&centerdot;</#if></td>
    <td class="player-name" hx-post="/player/1/point" hx-trigger="click" hx-target="#scoreboard">${firstPlayerName}</td>
    <td class="finished-set won">6</td>
    <td class="finished-set lost">4</td>
    <td class="current-set">${firstPlayerCurrentSetScore}</td>
    <td class="current-game">${firstPlayerCurrentGameScore}</td>
  </tr>
  <tr>
    <td class="ball-hitter"><#if !isFirstPlayerServing()>&centerdot;</#if></td>
    <td class="player-name" hx-post="/player/2/point" hx-trigger="click" hx-target="#scoreboard">${secondPlayerName}</td>
    <td class="finished-set lost">2</td>
    <td class="finished-set won">6</td>
    <td class="current-set">${secondPlayerCurrentSetScore}</td>
    <td class="current-game">${secondPlayerCurrentGameScore}</td>
  </tr>
</table>
