<?php if ($current_user['admin']): ?>
    <div id="box" style="background-color:#3F2828; text-align:center;">
        <h1>Admin Panel</h1>
    </div>
    <br>
    <div id="boxFloat" style="background-color:#3F2828; width:475px;">
        <h2>Website settings</h2>

        <ul>
            <li>
                <a href="/admin/news/edit">Write admin news</a>
            </li>
            <li>
                <a href="/admin/tournaments/upload">Admin files</a>
            </li>
        </ul>

        <h3>Logs</h3>

        <ul>
            <li>
                <a href="/pages/logs?log=login">Login</a>
            </li>
            <li>
                <a href="/pages/logs?log=download">Replay</a>
            </li>
            <li>
                <a href="/pages/logs?log=detaner">Detaner</a>
            </li>
            <li>
                <a href="/pages/logs?log=twitch">Twitch</a>
            </li>
        </ul>
    </div>
    <div id="box" style="background-color:#3F2828; margin-left:530px; width:408px; margin-top:0px;">
        <h2>Tournament settings</h2>

        <ul>
            <li>
                <?php if ($currentTournament['Tournament']['status'] === null): ?>
                    <a href="/admin/tournaments/add">Start a new tournament</a>
                <?php elseif ($currentTournament['Tournament']['status'] == Tournament::PENDING): ?>
                    <a href="/admin/tournaments/edit">Start group stage</a>
                <?php
                elseif ($currentTournament['Tournament']['status'] == Tournament::GROUP): ?>
                    <a href="/admin/tournaments/edit">Start the playoffs</a>
                <?php
                elseif ($currentTournament['Tournament']['status'] == Tournament::PLAYOFF): ?>
                    <a href="/admin/tournaments/edit" onclick="return confirm('Are you sure?');">
                        Archive the tournament</a>
                <?php endif; ?>
            </li>
            <?php if ($currentTournament['Tournament']['status'] == Tournament::GROUP
                || $currentTournament['Tournament']['status'] == Tournament::PLAYOFF
            ): ?>
                <li>
                    <?php
                    echo $this->Html->link(
                        'Add a tech. win',
                        '/admin/games/techwin'
                    );
                    ?>
                </li>
            <?php endif; ?>
            <li>
                <a href="/admin/rules/edit">Edit Rules</a>
            </li>
            <?php if ($currentTournament['Tournament']['status'] == Tournament::GROUP): ?>
                <li>
                    <a href="/admin/groups/edit">Replace a player</a>
                </li>
            <?php endif; ?>
            <li>
                <a href="/admin/tournaments/review">Edit a tournament review</a>
            </li>
        </ul>
    </div>
<?php else: ?>
    <div id="authMessage" class="message">
        You don't have the required rights to access that page.
    </div>
<?php endif; ?>
