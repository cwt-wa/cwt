Hey <?php echo $username; ?>,<br/>
<br/>
you have requested a new password.<br/>
Go to the following page to reset you password:<br/>
<br/>
<a href="<?php echo 'http://' . $_SERVER['HTTP_HOST'] . '/user/reset_password/' . $resetKey; ?>"><?php echo 'http://' . $_SERVER['HTTP_HOST'] . '/user/reset_password/' . $resetKey; ?></a><br/>
<br/>
Sincerely,<br/>
The CWT Admin Team