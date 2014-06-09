<?php
if (isset($unknowns)) {
    if (count($unknowns) > 1) {
        echo 'No such users as <b>' . $this->Text->toList($unknowns, 'and') . '</b>';
    } else {
        echo "No such user as <b>$unknowns[0]</b>";
    }
}
