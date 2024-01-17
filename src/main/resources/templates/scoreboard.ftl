<table id="scoreboard">
  <tr>
    <td class="ball-hitter"><#if isFirstPlayerServing()>&centerdot;</#if></td>
    <td class="player-name">${firstPlayerName}</td>
    <td class="current-set">${firstPlayerCurrentSetScore}</td>
    <td class="current-game">${firstPlayerCurrentGameScore}</td>
  </tr>
  <tr>
    <td class="ball-hitter"><#if !isFirstPlayerServing()>&centerdot;</#if></td>
    <td class="player-name">${secondPlayerName}</td>
    <td class="current-set">${secondPlayerCurrentSetScore}</td>
    <td class="current-game">${secondPlayerCurrentGameScore}</td>
  </tr>
</table>