Hey <?php echo $username; ?>,<br/>
<br/>
you have requested a new password.<br/>
Go to the following page to reset your password:<br/>
<br/>
<a href="<?php echo 'http://' . $_SERVER['HTTP_HOST'] . '/users/reset_password/' . $resetKey; ?>"><?php echo 'http://' . $_SERVER['HTTP_HOST'] . '/users/reset_password/' . $resetKey; ?></a>