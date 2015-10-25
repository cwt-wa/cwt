<?php
$news = $this->requestAction('/news/view/');
$this->Helpers->load('Bbcode');
$this->Helpers->load('Time');
?>


<div id="box" style="background-color: #3E283E; margin-bottom: -60px; margin-top:45px">
    <?php if ($news['News']['text'] != ''): ?>
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
                'end' => '+12 hour',
                'accuracy' => array('hour' => 'hour')
            ));
            ?>
        </div>
        <?php echo nl2br($this->Bbcode->parse($news['News']['text'])); ?>
    <?php else: ?>
        <div id="adminNews">
            Welcome to CWT
        </div>
        Hello, welcome to <b>Crespo’s Worms Tournament</b>.
    <?php endif; ?>
</div>
