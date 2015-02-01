<?php
echo $this->Html->css('groups', null, array('inline' => false));
?>

<table border="0" align="center" cellpadding="5">
    <tr>
        <th class="bottomBorder">Title</th>
        <th class="bottomBorder">Status</th>
        <th class="bottomBorder">Views</th>
        <th class="bottomBorder">Length in Hours</th>
        <th class="bottomBorder">Channel</th>
        <th class="bottomBorder">Recorded at</th>
    </tr>
    <?php foreach ($videos as $video): ?>
        <tr>
            <td class="bottomBorder" align="right">
                <?php
                echo $this->Html->link($video['Video']['id'], '/videos/view/' . $video['Video']['id']);
                ?>
            </td>
        </tr>
    <?php endforeach; ?>
</table>
