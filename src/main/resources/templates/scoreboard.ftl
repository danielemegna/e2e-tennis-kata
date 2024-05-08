<table id="scoreboard">
  <tr>
    <td class="ball-hitter"><#if isFirstPlayerServing()>&centerdot;</#if></td>
    <td class="player-name" hx-post="/player/1/point" hx-trigger="click" hx-target="#scoreboard">${firstPlayerName}</td>
    <#list finishedSets as finishedSet>
      <td class="finished-set ${finishedSet.wonByFirstPlayer ? then('won','lost')}">
        ${finishedSet.firstPlayerScore}
      </td>
    </#list>
    <td class="current-set">${firstPlayerCurrentSetScore}</td>
    <td class="current-game">${firstPlayerCurrentGameScore}</td>
  </tr>
  <tr>
    <td class="ball-hitter"><#if !isFirstPlayerServing()>&centerdot;</#if></td>
    <td class="player-name" hx-post="/player/2/point" hx-trigger="click" hx-target="#scoreboard">${secondPlayerName}</td>
    <#list finishedSets as finishedSet>
      <td class="finished-set ${finishedSet.wonByFirstPlayer ? then('lost','won')}">
        ${finishedSet.secondPlayerScore}
      </td>
    </#list>
    <td class="current-set">${secondPlayerCurrentSetScore}</td>
    <td class="current-game">${secondPlayerCurrentGameScore}</td>
  </tr>
</table>
