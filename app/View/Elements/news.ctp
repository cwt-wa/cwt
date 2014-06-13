<?php
$news = $this->requestAction('/news/view/');
$this->Helpers->load('Bbcode');
$this->Helpers->load('Time');
?>

<?php if ($news['News']['text'] != ''): ?>
    <div id="box" style="background-color: #3E283E; margin-bottom: -60px;">
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
        <div style="text-align: center;">
            <a href="https://twitter.com/cwtwa" class="twitter-follow-button" data-show-count="false" data-size="large">
                Follow @cwtwa
            </a>
            <script>!function (d, s, id) {
                    var js, fjs = d.getElementsByTagName(s)[0], p = /^http:/.test(d.location) ? 'http' : 'https';
                    if (!d.getElementById(id)) {
                        js = d.createElement(s);
                        js.id = id;
                        js.src = p + '://platform.twitter.com/widgets.js';
                        fjs.parentNode.insertBefore(js, fjs);
                    }
                }(document, 'script', 'twitter-wjs');</script>
        </div>
        <br/>
        <?php echo nl2br($this->Bbcode->parse($news['News']['text'])); ?>
    </div>
<?php endif; ?>
