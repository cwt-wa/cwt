<table border="0" align="center" cellpadding="5" id="streamsList">
    <thead>
    <tr style="cursor:pointer;">
        <th class="bottomBorder" align="center">Title</th>
        <th class="bottomBorder" align="center">Views</th>
        <th class="bottomBorder" align="center">Length</th>
        <th class="bottomBorder" align="center">Channel</th>
        <th class="bottomBorder" align="center">Recorded at</th>
    </tr>
    </thead>
    <tbody>
    <?php foreach ($videos as $video): ?>
        <tr>
            <td class="bottomBorder" align="left">
                <?php
                echo $this->Html->link($video['title'], '/streams/view/' . $video['_id']);
                ?>
            </td>
            <td class="bottomBorder" align="center">
                <?php echo $video['views'] ?>
            </td>
            <td class="bottomBorder" align="center">
                <?php echo gmdate('H:i:s', $video['length']) ?>
            </td>
            <td class="bottomBorder" align="center">
                <?php echo $video['_channel'] ?>
            </td>
            <td class="bottomBorder" align="center">
                <?php echo $this->Time->format($video['recorded_at'], '%Y-%m-%d %H:%M:%S') ?>
            </td>
        </tr>
    <?php endforeach; ?>
    </tbody>
</table>
