<?php if (!$current_user['admin']): ?>
    <div id="authMessage" class="message">You don't have the required rights to access that page.</div>
<?php else: ?>
    <div id="box" style="background-color:#3F2828;">
        <h1 align="center"><?php echo ucfirst($_GET['log']) ?> Log</h1>
    </div>
    <div id="box" style="background-color:#3F2828; text-align:center;">
        More Logs to come sooner or later. Newest entries at the bottom.
    </div>
    <div id="box" style="background-color:#3F2828;">
        <?php
        foreach (file(LOGS . DS . $_GET['log'] . '.log') as $line) {
            echo nl2br($line);
        }
        ?>
    </div>
<?php endif; ?>
