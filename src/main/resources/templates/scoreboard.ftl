<#if infoTooltipText?? && infoTooltipText != "">
  <div id="info-tooltip">
    ${infoTooltipText}
  </div>
</#if>
<table id="scoreboard">
  <tr>
    <td class="ball-hitter"><#if isFirstPlayerServing()>&centerdot;</#if></td>
    <td class="player-name" hx-post="/${matchId}/player/1/point" hx-trigger="click" hx-target="#scoreboard">${firstPlayerName}</td>
    <#list finishedSets as finishedSet>
      <td class="finished-set ${finishedSet.firstPlayerCssClass}">
        <span>${finishedSet.firstPlayerScore}</span>
        <span class="tie-break-score">${(finishedSet.firstPlayerTieBreakScore)!}</span>
      </td>
    </#list>
    <td class="current-set">${firstPlayerCurrentSetScore}</td>
    <td class="current-game">${firstPlayerCurrentGameScore}</td>
  </tr>
  <tr>
    <td class="ball-hitter"><#if !isFirstPlayerServing()>&centerdot;</#if></td>
    <td class="player-name" hx-post="/${matchId}/player/2/point" hx-trigger="click" hx-target="#scoreboard">${secondPlayerName}</td>
    <#list finishedSets as finishedSet>
      <td class="finished-set ${finishedSet.secondPlayerCssClass}">
        <span>${finishedSet.secondPlayerScore}</span>
        <span class="tie-break-score">${(finishedSet.secondPlayerTieBreakScore)!}</span>
      </td>
    </#list>
    <td class="current-set">${secondPlayerCurrentSetScore}</td>
    <td class="current-game">${secondPlayerCurrentGameScore}</td>
  </tr>
</table>
