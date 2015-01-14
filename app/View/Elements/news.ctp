<?php
$news = $this->requestAction('/news/view/');
$this->Helpers->load('Bbcode');
$this->Helpers->load('Time');
?>

<?php if ($news['News']['text'] != ''): ?>
    <div id="box" style="background-color: #3E283E; margin-bottom: -60px; margin-top:45px">
        <div id="adminNews">
            <?php
            echo $this->Html->link(
                $news['User']['username'],
                '/users/view/' . $news['User']['id']
            );
            ?>
            <?php
            echo $this->Time->timeAgoInWords($news['News']['modified'], array(
                'format' => 'M j, Y \a\t H:i',
                'end' => '+1 day',
                'accuracy' => array('hour' => 'hour')
            ));
            ?>
        </div>
        <?php echo nl2br($this->Bbcode->parse($news['News']['text'])); ?>
    </div>
<?php endif; ?>
