<?php

/*
 * This is a page used for AJAX responses on form submission.
 */

if (isset($failed) && $failed != false) {
    echo '<span class=" LV_validation_message LV_invalid">';
} else {
    echo '<span class=" LV_validation_message LV_valid">';
}

// The array would usually be returned by Model::validationErrors()
if (is_array($response)) {
    echo '- ';
    foreach (array_values($response) as $item) {
        echo $item[0] . ' - ';
    }
} else {
    // Just a regular text output.
    echo $response;
}

echo '</span>';