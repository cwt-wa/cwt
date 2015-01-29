<?php
if ($tournamentYear != $currentTournament['Tournament']['year']) {
    echo $this->element('archiveof', array(
        'tournamentYear' => $tournamentYear,
        'link' => false
    ));
}
?>
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
