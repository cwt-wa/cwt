<div id="box" style="background-color: rgba(63, 40, 40, 0.4); text-align: center;">
    No warranty for accuracy of data.
</div>

<?php
echo $this->element('groups', array(
    'group' => $group
));
?>
<br/>
<?php
echo $this->element('playoffs', array(
    'playoff' => $playoff
));
?>
