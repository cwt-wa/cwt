<?php if (!$current_user['admin']): ?>
    <div id="authMessage" class="message">
        You don't have the required rights to access that page.
    </div>
<?php else: ?>
    <div id="box" style="background-color:#3F2828; text-align:center;">
        <h1>Landing Page</h1>
    </div>
<?php endif; ?>
