<?php if (!empty($liveStreams)): ?>
    <?php foreach ($liveStreams as $liveStream): ?>
        <div id="box" style="background: #303F4B url('/img/popcorn.png') no-repeat 15px center; padding-left: 60px;">
            Live Stream: <?php echo $liveStream['_title'] ?> is live streaming <?php echo $this->Html->link($liveStream['stream']['channel']['status'], $liveStream['stream']['channel']['url']) ?> right now! Tune in!
        </div>
    <?php endforeach; ?>
<?php endif; ?>
